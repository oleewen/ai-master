package com.ai.master.appeal.domain.enums;

/**
 * 申诉项状态枚举
 */
public enum AppealItemStatus {
    CREATED("已创建"),
    VALIDATION_PASSED("验证通过"),
    VALIDATION_FAILED("验证失败"),
    VALIDATION_REJECTED("验证驳回"),
    PENDING_AUDIT("待审核"),
    AUDIT_PASSED("审核通过"),
    AUDIT_REJECTED("审核驳回");
    
    private final String description;
    
    AppealItemStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}