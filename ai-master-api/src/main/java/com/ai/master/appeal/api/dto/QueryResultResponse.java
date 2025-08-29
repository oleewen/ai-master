package com.ai.master.appeal.api.dto;

import lombok.Data;

import java.util.List;

/**
 * 查询申诉结果响应DTO
 */
@Data
public class QueryResultResponse {
    
    /** 申诉单ID */
    private Long appealId;
    
    /** 申诉理由 */
    private String reason;
    
    /** 申诉状态 */
    private String status;
    
    /** 申诉项列表 */
    private List<AppealItemResult> items;
    
    @Data
    public static class AppealItemResult {
        /** 申诉项ID */
        private Long itemId;
        
        /** 申诉类型 */
        private String type;
        
        /** 申诉项标题 */
        private String title;
        
        /** 申诉内容 */
        private String content;
        
        /** 申诉项状态 */
        private String status;
    }
}