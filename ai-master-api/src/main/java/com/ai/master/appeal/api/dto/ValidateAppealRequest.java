package com.ai.master.appeal.api.dto;

import lombok.Data;

import java.util.List;

/**
 * 验证申诉请求DTO
 */
@Data
public class ValidateAppealRequest {
    
    /** 申诉单ID */
    private Long appealId;
    
    /** 申诉项列表 */
    private List<AppealItemValidationDto> items;
    
    @Data
    public static class AppealItemValidationDto {
        /** 申诉项ID */
        private Long itemId;
        
        /** 申诉类型 */
        private String type;
        
        /** 申诉内容 */
        private String content;
    }
}