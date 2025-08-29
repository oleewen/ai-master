package com.ai.master.appeal.api.dto;

import lombok.Data;

import java.util.List;

/**
 * 查询申诉进展响应DTO
 */
@Data
public class QueryProgressResponse {
    
    /** 申诉单ID */
    private Long appealId;
    
    /** 申诉状态 */
    private String status;
    
    /** 进展描述 */
    private String progress;
    
    /** 申诉项列表 */
    private List<AppealItemProgress> items;
    
    @Data
    public static class AppealItemProgress {
        /** 申诉项ID */
        private Long itemId;
        
        /** 申诉项标题 */
        private String title;
        
        /** 申诉项状态 */
        private String status;
    }
}