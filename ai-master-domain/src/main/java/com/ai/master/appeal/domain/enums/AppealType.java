package com.ai.master.appeal.domain.enums;

/**
 * 申诉类型枚举
 */
public enum AppealType {
    WEIGHT("weight", "重量申诉"),
    FEE("fee", "费用申诉"),
    DESTINATION("destination", "目的地申诉"),
    TIME("time", "时效申诉"),
    SERVICE("service", "服务申诉"),
    OTHER("other", "其他申诉"),
    UNDEFINED("undefined", "未定义");
    
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

    /**
     * 将字符串转换为申诉类型枚举
     */
    public static AppealType fromCode(String code) {
        if (code == null) {
            return UNDEFINED;
        }
        return java.util.Arrays.stream(values())
                .filter(e -> e.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(UNDEFINED);
    }
}