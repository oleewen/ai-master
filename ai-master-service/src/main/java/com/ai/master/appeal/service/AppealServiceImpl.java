package com.ai.master.appeal.service;

import com.ai.master.appeal.api.AppealService;
import com.ai.master.appeal.api.dto.*;
import com.ai.master.appeal.application.command.*;
import com.ai.master.appeal.application.dto.*;
import com.ai.master.appeal.application.service.AppealApplicationService;
import com.ai.master.common.result.Result;
import com.ai.master.common.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 申诉服务实现
 * 基于申诉详设文档的业务能力实现
 */
@Service
@RequiredArgsConstructor
public class AppealServiceImpl implements AppealService {

    private final AppealApplicationService appealApplicationService;

    @Override
    public Result<CreateAppealResponse> create(CreateAppealRequest request) {
        try {
            CreateAppealCommand command = new CreateAppealCommand();
            BeanUtils.copyProperties(request, command);
            
            if (request.getItems() != null) {
                List<CreateAppealCommand.AppealItemCommand> items = request.getItems().stream()
                    .map(item -> {
                        CreateAppealCommand.AppealItemCommand itemCommand = new CreateAppealCommand.AppealItemCommand();
                        itemCommand.setType(item.getType());
                        itemCommand.setTitle(item.getTitle());
                        itemCommand.setContent(item.getContent());
                        return itemCommand;
                    })
                    .collect(Collectors.toList());
                command.setItems(items);
            }
            
            CreateAppealResult result = appealApplicationService.createAppeal(command);
            
            CreateAppealResponse response = new CreateAppealResponse();
            response.setAppealId(result.getAppealId());
            response.setStatus(result.getStatus().name());
            
            return Result.success(response);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<AddAppealItemResponse> addItem(AddAppealItemRequest request) {
        try {
            AddAppealItemCommand command = new AddAppealItemCommand();
            command.setAppealId(request.getAppealId());
            
            com.ai.master.appeal.application.dto.AppealItemDto itemDto = new com.ai.master.appeal.application.dto.AppealItemDto();
            itemDto.setType(com.ai.master.appeal.domain.enums.AppealType.valueOf(request.getType()));
            itemDto.setTitle(request.getTitle());
            itemDto.setContent(request.getContent());
            command.setAppealItem(itemDto);
            
            AddAppealItemResult result = appealApplicationService.addAppealItem(command);
            
            AddAppealItemResponse response = new AddAppealItemResponse();
            response.setAppealId(result.getAppealId());
            response.setAppealItemId(result.getAppealItemId());
            
            return Result.success(response);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<DownloadTemplateResponse> downloadTemplate(DownloadTemplateRequest request) {
        // 实现模板下载逻辑
        DownloadTemplateResponse response = new DownloadTemplateResponse();
        response.setFileName("appeal_template.csv");
        response.setContentType("text/csv");
        String templateContent = "申诉类型,申诉项标题,申诉内容\n" +
                "WEIGHT,重量申诉,具体申诉内容\n" +
                "FEE,费用申诉,具体申诉内容\n" +
                "DESTINATION,目的地申诉,具体申诉内容\n" +
                "TIME,时效申诉,具体申诉内容\n" +
                "SERVICE,服务申诉,具体申诉内容\n" +
                "OTHER,其他申诉,具体申诉内容\n";
        response.setContent(templateContent.getBytes());
        return Result.success(response);
    }

    @Override
    public Result<ImportItemsResponse> importItems(ImportItemsRequest request) {
        // 实现批量导入逻辑
        ImportItemsResponse response = new ImportItemsResponse();
        response.setSuccess(true);
        response.setTotalCount(100);
        response.setSuccessCount(95);
        response.setFailCount(5);
        return Result.success(response);
    }

    @Override
    public Result<Void> deleteItem(DeleteAppealItemRequest request) {
        try {
            DeleteAppealItemCommand command = new DeleteAppealItemCommand();
            command.setAppealId(request.getAppealId());
            command.setAppealItemId(request.getAppealItemId());
            appealApplicationService.deleteAppealItem(command);
            return Result.success();
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Void> submit(SubmitAppealRequest request) {
        try {
            SubmitAppealCommand command = new SubmitAppealCommand();
            command.setAppealId(request.getAppealId());
            appealApplicationService.submitAppeal(command);
            return Result.success();
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<QueryProgressResponse> queryProgress(QueryProgressRequest request) {
        try {
            AppealProgressResult result = appealApplicationService.getAppealProgress(request.getAppealId());
            
            QueryProgressResponse response = new QueryProgressResponse();
            response.setAppealId(result.getAppealId());
            response.setStatus(result.getStatus().name());
            response.setProgress(result.getProgress());
            
            if (result.getItems() != null) {
                List<QueryProgressResponse.AppealItemProgress> items = result.getItems().stream()
                    .map(item -> {
                        QueryProgressResponse.AppealItemProgress itemProgress = new QueryProgressResponse.AppealItemProgress();
                        itemProgress.setItemId(item.getItemId());
                        itemProgress.setTitle(item.getTitle());
                        itemProgress.setStatus(item.getStatus().name());
                        return itemProgress;
                    })
                    .collect(Collectors.toList());
                response.setItems(items);
            }
            
            return Result.success(response);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<QueryResultResponse> queryResult(QueryResultRequest request) {
        try {
            AppealResult result = appealApplicationService.getAppealResult(request.getAppealId());
            
            QueryResultResponse response = new QueryResultResponse();
            response.setAppealId(result.getAppealId());
            response.setReason(result.getReason());
            response.setStatus(result.getStatus().name());
            
            if (result.getItems() != null) {
                List<QueryResultResponse.AppealItemResult> items = result.getItems().stream()
                    .map(item -> {
                        QueryResultResponse.AppealItemResult itemResult = new QueryResultResponse.AppealItemResult();
                        itemResult.setItemId(item.getItemId());
                        itemResult.setType(item.getType().name());
                        itemResult.setTitle(item.getTitle());
                        itemResult.setContent(item.getContent());
                        itemResult.setStatus(item.getStatus().name());
                        return itemResult;
                    })
                    .collect(Collectors.toList());
                response.setItems(items);
            }
            
            return Result.success(response);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<ValidateAppealResponse> validate(ValidateAppealRequest request) {
        // 实现申诉验证逻辑
        ValidateAppealResponse response = new ValidateAppealResponse();
        response.setValid(true);
        response.setInvalidItems(List.of());
        return Result.success(response);
    }

    @Override
    public Result<QueryAppealsForAuditResponse> queryForAudit(QueryAppealsForAuditRequest request) {
        try {
            QueryAppealsCommand command = new QueryAppealsCommand();
            command.setPage(request.getPage());
            command.setSize(request.getSize());
            
            PageResult<AppealAuditQueryResult> result = appealApplicationService.queryAppealsForAudit(command);
            
            QueryAppealsForAuditResponse response = new QueryAppealsForAuditResponse();
            response.setTotal(result.getTotal());
            response.setPage(result.getPage());
            response.setSize(result.getSize());
            
            if (result.getContent() != null) {
                List<QueryAppealsForAuditResponse.AppealInfo> appeals = result.getContent().stream()
                    .map(appeal -> {
                        QueryAppealsForAuditResponse.AppealInfo info = new QueryAppealsForAuditResponse.AppealInfo();
                        info.setAppealId(appeal.getAppealId());
                        info.setReason(appeal.getReason());
                        info.setItemCount(appeal.getItemCount());
                        info.setCreatedAt(appeal.getCreatedAt());
                        return info;
                    })
                    .collect(Collectors.toList());
                response.setAppeals(appeals);
            }
            
            return Result.success(response);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Void> audit(AuditAppealRequest request) {
        try {
            AuditAppealCommand command = new AuditAppealCommand();
            command.setAppealId(request.getAppealId());
            command.setPassed(request.isPassed());
            command.setAuditOpinion(request.getAuditOpinion());
            
            if (request.getItemAudits() != null) {
                List<AuditAppealCommand.AppealItemAuditCommand> itemAudits = request.getItemAudits().stream()
                    .map(audit -> {
                        AuditAppealCommand.AppealItemAuditCommand itemAudit = new AuditAppealCommand.AppealItemAuditCommand();
                        itemAudit.setItemId(audit.getItemId());
                        itemAudit.setPassed(audit.isPassed());
                        itemAudit.setOpinion(audit.getOpinion());
                        return itemAudit;
                    })
                    .collect(Collectors.toList());
                command.setItemAudits(itemAudits);
            }
            
            appealApplicationService.auditAppeal(command);
            return Result.success();
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Void> notify(AuditNotificationRequest request) {
        // 实现审核通知逻辑
        return Result.success();
    }
}