package com.ai.master.appeal.api.dto;

import lombok.Data;

import java.util.List;

/**
 * 验证申诉响应DTO
 */
@Data
public class ValidateAppealResponse {
    
    /** 是否有效 */
    private boolean valid;
    
    /** 无效项目列表 */
    private List<InvalidItem> invalidItems;
    
    @Data
    public static class InvalidItem {
        /** 申诉项ID */
        private Long itemId;
        
        /** 无效原因 */
        private String reason;
    }
}