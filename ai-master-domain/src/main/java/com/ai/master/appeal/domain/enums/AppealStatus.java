package com.ai.master.appeal.domain.enums;

/**
 * 申诉单状态枚举
 */
public enum AppealStatus {
    CREATED("已创建"),
    VERIFIED("已验证"),
    SUBMITTED("已受理"),
    AUDITING("审核中"),
    ALL_PASSED("全部通过"),
    PARTIAL_PASSED("部分通过"),
    ALL_REJECTED("全部驳回"),
    CANCELLED("已取消");
    
    private final String description;
    
    AppealStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}