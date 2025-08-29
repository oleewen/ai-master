package com.ai.master.appeal.domain.service;

import com.ai.master.appeal.domain.entity.Appeal;
import com.ai.master.appeal.domain.entity.AppealItem;
import com.ai.master.appeal.domain.entity.Appealer;
import com.ai.master.appeal.domain.enums.AppealType;
import com.ai.master.appeal.domain.valueobject.AppealId;
import com.ai.master.appeal.domain.valueobject.AppealerId;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 申诉领域服务
 * 处理申诉业务的核心逻辑
 */
@Service
public class AppealDomainService {
    
    /**
     * 创建申诉单
     */
    public Appeal createAppeal(AppealerId appealerId, String reason) {
        return Appeal.create(appealerId, reason);
    }
    
    /**
     * 添加申诉项
     */
    public void addAppealItem(Appeal appeal, AppealType type, String title, String content) {
        AppealItem item = AppealItem.create(type, title, content);
        appeal.addItem(item);
    }
    
    /**
     * 验证申诉单
     */
    public void validateAppeal(Appeal appeal) {
        if (appeal.getItems().isEmpty()) {
            throw new IllegalArgumentException("申诉单必须包含至少一个申诉项");
        }
        
        appeal.verify();
    }
    
    /**
     * 提交申诉
     */
    public void submitAppeal(Appeal appeal) {
        if (appeal.getItems().isEmpty()) {
            throw new IllegalArgumentException("申诉单必须包含至少一个申诉项");
        }
        
        appeal.submit();
    }
    
    /**
     * 审核申诉
     */
    public void auditAppeal(Appeal appeal, boolean passed) {
        appeal.audit(passed);
    }
    
    /**
     * 取消申诉
     */
    public void cancelAppeal(Appeal appeal) {
        appeal.cancel();
    }
    
    /**
     * 验证申诉项
     */
    public void validateAppealItem(AppealItem item, boolean passed, String reason) {
        item.validate(passed, reason);
    }
    
    /**
     * 审核申诉项
     */
    public void auditAppealItem(AppealItem item, boolean passed, String opinion) {
        item.audit(passed, opinion);
    }
    
    /**
     * 计算申诉通过率
     */
    public double calculatePassRate(Appeal appeal) {
        if (appeal.getItems().isEmpty()) {
            return 0.0;
        }
        
        long passedCount = appeal.getItems().stream()
            .filter(item -> item.getStatus().name().contains("PASSED"))
            .count();
        
        return (double) passedCount / appeal.getItems().size();
    }
}