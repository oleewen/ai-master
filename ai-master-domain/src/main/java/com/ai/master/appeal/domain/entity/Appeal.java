package com.ai.master.appeal.domain.entity;

import com.ai.master.appeal.domain.enums.AppealStatus;
import com.ai.master.appeal.domain.enums.AppealItemStatus;
import com.ai.master.appeal.domain.valueobject.AppealId;
import com.ai.master.appeal.domain.valueobject.AppealerId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 申诉单聚合根
 * 记录并管理用户发起的所有申诉事项的聚合根对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Appeal extends BaseEntity {
    
    /** 申诉单ID */
    private AppealId id;
    
    /** 申诉方ID */
    private AppealerId appealerId;
    
    /** 申诉理由 */
    private String reason;
    
    /** 申诉状态 */
    private AppealStatus status;
    
    /** 附件列表 */
    private List<String> attachments = new ArrayList<>();
    
    /** 申诉项列表 */
    private List<AppealItem> items = new ArrayList<>();
    
    
    /**
     * 创建申诉单
     */
    public static Appeal create(AppealerId appealerId, String reason) {
        Appeal appeal = new Appeal();
        appeal.setId(AppealId.generate());
        appeal.setAppealerId(appealerId);
        appeal.setReason(reason);
        appeal.setStatus(AppealStatus.CREATED);
        return appeal;
    }
    
    /**
     * 提交申诉
     */
    public void submit() {
        if (this.status != AppealStatus.VERIFIED) {
            throw new IllegalStateException("申诉单状态不允许提交");
        }
        if (this.items.isEmpty()) {
            throw new IllegalStateException("申诉单必须包含至少一个申诉项");
        }
        this.status = AppealStatus.SUBMITTED;
        
        // 更新所有申诉项状态
        this.items.forEach(AppealItem::submit);
    }
    
    /**
     * 验证申诉
     */
    public void verify() {
        if (this.status != AppealStatus.CREATED) {
            throw new IllegalStateException("申诉单状态不允许验证");
        }
        this.status = AppealStatus.VERIFIED;
    }
    
    /**
     * 审核申诉
     */
    public void audit(boolean passed) {
        if (this.status != AppealStatus.SUBMITTED && this.status != AppealStatus.AUDITING) {
            throw new IllegalStateException("申诉单状态不允许审核");
        }
        
        long passedCount = items.stream()
            .filter(item -> item.getStatus() == AppealItemStatus.AUDIT_PASSED)
            .count();
        
        long rejectedCount = items.stream()
            .filter(item -> item.getStatus() == AppealItemStatus.AUDIT_REJECTED)
            .count();
        
        if (passedCount == items.size() && passedCount > 0) {
            this.status = AppealStatus.ALL_PASSED;
        } else if (rejectedCount == items.size() && rejectedCount > 0) {
            this.status = AppealStatus.ALL_REJECTED;
        } else if (passedCount > 0 || rejectedCount > 0) {
            this.status = AppealStatus.PARTIAL_PASSED;
        } else {
            // 如果没有审核结果，保持原状态
            this.status = AppealStatus.AUDITING;
        }
    }
    
    /**
     * 取消申诉
     */
    public void cancel() {
        if (this.status == AppealStatus.ALL_PASSED || 
            this.status == AppealStatus.ALL_REJECTED || 
            this.status == AppealStatus.PARTIAL_PASSED) {
            throw new IllegalStateException("已完成的申诉不能取消");
        }
        
        this.status = AppealStatus.CANCELLED;
    }
    
    /**
     * 添加申诉项
     */
    public void addItem(AppealItem item) {
        if (this.status != AppealStatus.CREATED && this.status != AppealStatus.VERIFIED) {
            throw new IllegalStateException("当前状态不允许添加申诉项");
        }
        
        item.setAppealId(this.id);
        this.items.add(item);
    }
    
    /**
     * 删除申诉项
     */
    public void removeItem(Long itemId) {
        if (this.status != AppealStatus.CREATED && this.status != AppealStatus.VERIFIED) {
            throw new IllegalStateException("当前状态不允许删除申诉项");
        }
        
        this.items.removeIf(item -> item.getId().getValue().equals(itemId));
    }
}