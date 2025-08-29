package com.ai.master.appeal.domain.enums;

/**
 * 申诉类型枚举
 */
public enum AppealType {
    WEIGHT("重量", "重量申诉"),
    FEE("费用", "费用申诉"),
    DESTINATION("目的地", "目的地申诉"),
    TIME("时效", "时效申诉"),
    SERVICE("服务", "服务申诉"),
    OTHER("其他", "其他申诉");
    
    private final String code;
    private final String title;
    
    AppealType(String code, String title) {
        this.code = code;
        this.title = title;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getTitle() {
        return title;
    }
}