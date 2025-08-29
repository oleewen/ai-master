package com.ai.master.appeal.application.service;

import com.ai.master.appeal.application.command.*;
import com.ai.master.appeal.application.dto.*;
import com.ai.master.appeal.domain.entity.Appeal;
import com.ai.master.appeal.domain.entity.AppealItem;
import com.ai.master.appeal.domain.entity.Appealer;
import com.ai.master.appeal.domain.enums.AppealStatus;
import com.ai.master.appeal.domain.enums.AppealType;
import com.ai.master.appeal.domain.repository.AppealRepository;
import com.ai.master.appeal.domain.repository.AppealerRepository;
import com.ai.master.appeal.domain.service.AppealDomainService;
import com.ai.master.appeal.domain.service.AppealValidationService;
import com.ai.master.appeal.domain.valueobject.AppealId;
import com.ai.master.appeal.domain.valueobject.AppealerId;
import com.ai.master.common.util.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 申诉应用服务
 * 负责编排申诉相关的用例
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppealApplicationService {
    
    private final AppealRepository appealRepository;
    private final AppealerRepository appealerRepository;
    private final AppealDomainService appealDomainService;
    private final AppealValidationService validationService;
    
    /**
     * 1.1 发起申诉
     */
    @Transactional
    public CreateAppealResult createAppeal(CreateAppealCommand command) {
        log.info("创建申诉单: {}", command);
        
        // 验证申诉方存在
        Appealer appealer = appealerRepository.findById(AppealerId.of(command.getAppealerId()))
            .orElseThrow(() -> new IllegalArgumentException("申诉方不存在"));
        
        // 创建申诉单
        Appeal appeal = appealDomainService.createAppeal(
            appealer.getId(), 
            command.getReason()
        );
        
        // 添加申诉项
        if (command.getItems() != null && !command.getItems().isEmpty()) {
            command.getItems().forEach(item -> {
                appealDomainService.addAppealItem(
                    appeal,
                    item.getType(),
                    item.getTitle(),
                    item.getContent()
                );
            });
        }
        
        // 保存申诉单
        appealRepository.save(appeal);
        
        return CreateAppealResult.builder()
            .appealId(appeal.getId().getValue())
            .status(appeal.getStatus())
            .build();
    }
    
    /**
     * 1.2 增加申诉项
     */
    @Transactional
    public AddAppealItemResult addAppealItem(AddAppealItemCommand command) {
        log.info("添加申诉项: {}", command);
        
        Appeal appeal = appealRepository.findById(AppealId.of(command.getAppealId()))
            .orElseThrow(() -> new IllegalArgumentException("申诉单不存在"));
        
        appealDomainService.addAppealItem(
            appeal,
            command.getAppealItem().getType(),
            command.getAppealItem().getTitle(),
            command.getAppealItem().getContent()
        );
        
        appealRepository.save(appeal);
        
        AppealItem lastItem = appeal.getItems().get(appeal.getItems().size() - 1);
        
        return AddAppealItemResult.builder()
            .appealId(appeal.getId().getValue())
            .appealItemId(lastItem.getId().getValue())
            .build();
    }
    
    /**
     * 1.5 删除申诉项
     */
    @Transactional
    public void deleteAppealItem(DeleteAppealItemCommand command) {
        log.info("删除申诉项: {}", command);
        
        Appeal appeal = appealRepository.findById(AppealId.of(command.getAppealId()))
            .orElseThrow(() -> new IllegalArgumentException("申诉单不存在"));
        
        appeal.removeItem(command.getAppealItemId());
        appealRepository.save(appeal);
    }
    
    /**
     * 1.6 提交申诉
     */
    @Transactional
    public void submitAppeal(SubmitAppealCommand command) {
        log.info("提交申诉: {}", command);
        
        Appeal appeal = appealRepository.findById(AppealId.of(command.getAppealId()))
            .orElseThrow(() -> new IllegalArgumentException("申诉单不存在"));
        
        appealDomainService.submitAppeal(appeal);
        appealRepository.save(appeal);
    }
    
    /**
     * 1.7 查询申诉进展
     */
    public AppealProgressResult getAppealProgress(Long appealId) {
        Appeal appeal = appealRepository.findById(AppealId.of(appealId))
            .orElseThrow(() -> new IllegalArgumentException("申诉单不存在"));
        
        return AppealProgressResult.builder()
            .appealId(appeal.getId().getValue())
            .status(appeal.getStatus())
            .progress(getProgressDescription(appeal.getStatus()))
            .items(appeal.getItems().stream()
                .map(item -> AppealProgressResult.AppealItemProgress.builder()
                    .itemId(item.getId().getValue())
                    .title(item.getTitle())
                    .status(item.getStatus())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
    
    /**
     * 1.8/3.3 查看申诉结果
     */
    public AppealResult getAppealResult(Long appealId) {
        Appeal appeal = appealRepository.findById(AppealId.of(appealId))
            .orElseThrow(() -> new IllegalArgumentException("申诉单不存在"));
        
        return AppealResult.builder()
            .appealId(appeal.getId().getValue())
            .reason(appeal.getReason())
            .status(appeal.getStatus())
            .items(appeal.getItems().stream()
                .map(item -> AppealResult.AppealItemResult.builder()
                    .itemId(item.getId().getValue())
                    .type(item.getType())
                    .title(item.getTitle())
                    .content(item.getContent())
                    .status(item.getStatus())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
    
    /**
     * 3.1 申诉审核查询
     */
    public PageResult<AppealAuditQueryResult> queryAppealsForAudit(QueryAppealsCommand command) {
        List<Appeal> appeals = appealRepository.findByStatus(AppealStatus.SUBMITTED);
        
        List<AppealAuditQueryResult> results = appeals.stream()
            .map(appeal -> AppealAuditQueryResult.builder()
                .appealId(appeal.getId().getValue())
                .reason(appeal.getReason())
                .itemCount(appeal.getItems().size())
                .createdAt(appeal.getCreatedAt())
                .build())
            .collect(Collectors.toList());
        
        return PageResult.of(results, command.getPage(), command.getSize());
    }
    
    /**
     * 3.2 申诉审核
     */
    @Transactional
    public void auditAppeal(AuditAppealCommand command) {
        log.info("审核申诉: {}", command);
        
        Appeal appeal = appealRepository.findById(AppealId.of(command.getAppealId()))
            .orElseThrow(() -> new IllegalArgumentException("申诉单不存在"));
        
        // 审核申诉单
        appealDomainService.auditAppeal(appeal, command.isPassed());
        
        // 审核申诉项
        if (command.getItemAudits() != null && !command.getItemAudits().isEmpty()) {
            command.getItemAudits().forEach(itemAudit -> {
                appeal.getItems().stream()
                    .filter(item -> item.getId().getValue().equals(itemAudit.getItemId()))
                    .findFirst()
                    .ifPresent(item -> {
                        appealDomainService.auditAppealItem(
                            item,
                            itemAudit.isPassed(),
                            itemAudit.getOpinion()
                        );
                    });
            });
        }
        
        appealRepository.save(appeal);
    }
    
    /**
     * 查询申诉列表
     */
    public PageResult<AppealListResult> queryAppeals(QueryAppealsCommand command) {
        List<Appeal> appeals;
        
        if (command.getStatus() != null) {
            appeals = appealRepository.findByStatus(command.getStatus());
        } else {
            appeals = appealRepository.findAll(command.getPage(), command.getSize());
        }
        
        List<AppealListResult> results = appeals.stream()
            .map(appeal -> AppealListResult.builder()
                .appealId(appeal.getId().getValue())
                .reason(appeal.getReason())
                .status(appeal.getStatus())
                .itemCount(appeal.getItems().size())
                .createdAt(appeal.getCreatedAt())
                .build())
            .collect(Collectors.toList());
        
        return PageResult.of(results, command.getPage(), command.getSize());
    }
    
    private String getProgressDescription(AppealStatus status) {
        switch (status) {
            case CREATED:
                return "申诉已创建，等待验证";
            case VERIFIED:
                return "申诉已验证，等待提交";
            case SUBMITTED:
                return "申诉已提交，等待审核";
            case AUDITING:
                return "申诉审核中";
            case ALL_PASSED:
                return "申诉已全部通过";
            case PARTIAL_PASSED:
                return "申诉部分通过";
            case ALL_REJECTED:
                return "申诉已全部驳回";
            case CANCELLED:
                return "申诉已取消";
            default:
                return "未知状态";
        }
    }
}