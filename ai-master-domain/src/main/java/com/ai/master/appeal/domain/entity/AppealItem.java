package com.ai.master.appeal.domain.entity;

import com.ai.master.appeal.domain.enums.AppealItemStatus;
import com.ai.master.appeal.domain.enums.AppealType;
import com.ai.master.appeal.domain.valueobject.AppealId;
import com.ai.master.appeal.domain.valueobject.AppealItemId;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 申诉项实体
 * 申诉单下具体的申诉明细，支持多类型申诉
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppealItem extends BaseEntity {
    
    /** 申诉项ID */
    private AppealItemId id;
    
    /** 申诉单ID */
    private AppealId appealId;
    
    /** 申诉类型 */
    private AppealType type;
    
    /** 申诉项标题 */
    private String title;
    
    /** 申诉项状态 */
    private AppealItemStatus status;
    
    /** 申诉内容 */
    private String content;
    
    
    /**
     * 创建申诉项
     */
    public static AppealItem create(AppealType type, String title, String content) {
        AppealItem item = new AppealItem();
        item.setId(AppealItemId.generate());
        item.setType(type);
        item.setTitle(title);
        item.setContent(content);
        item.setStatus(AppealItemStatus.CREATED);
        return item;
    }
    
    /**
     * 验证申诉项
     */
    public void validate(boolean passed, String reason) {
        if (this.status != AppealItemStatus.CREATED) {
            throw new IllegalStateException("申诉项状态不允许验证");
        }
        
        if (passed) {
            this.status = AppealItemStatus.VALIDATION_PASSED;
        } else {
            this.status = AppealItemStatus.VALIDATION_FAILED;
            this.content += " [验证失败原因: " + reason + "]";
        }
    }
    
    /**
     * 提交申诉项
     */
    public void submit() {
        if (this.status != AppealItemStatus.VALIDATION_PASSED) {
            throw new IllegalStateException("申诉项状态不允许提交");
        }
        
        this.status = AppealItemStatus.PENDING_AUDIT;
    }
    
    /**
     * 审核申诉项
     */
    public void audit(boolean passed, String opinion) {
        if (this.status != AppealItemStatus.PENDING_AUDIT) {
            throw new IllegalStateException("申诉项状态不允许审核");
        }
        
        if (passed) {
            this.status = AppealItemStatus.AUDIT_PASSED;
        } else {
            this.status = AppealItemStatus.AUDIT_REJECTED;
        }
        
        if (opinion != null && !opinion.isEmpty()) {
            this.content += " [审核意见: " + opinion + "]";
        }
    }
}