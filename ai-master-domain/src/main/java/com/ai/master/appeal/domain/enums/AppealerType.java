package com.ai.master.appeal.domain.enums;

/**
 * 申诉方类型枚举
 */
public enum AppealerType {
    SITE("网点"),
    CENTER("中心"),
    HEADQUARTERS("总部"),
    OTHER("其他");
    
    private final String description;
    
    AppealerType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}