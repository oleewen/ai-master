package com.ai.master.appeal.api.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 申诉审核查询响应DTO
 */
@Data
public class QueryAppealsForAuditResponse {
    
    /** 申诉列表 */
    private List<AppealInfo> appeals;
    
    /** 总数量 */
    private long total;
    
    /** 页码 */
    private int page;
    
    /** 每页数量 */
    private int size;
    
    @Data
    public static class AppealInfo {
        /** 申诉单ID */
        private Long appealId;
        
        /** 申诉理由 */
        private String reason;
        
        /** 申诉项数量 */
        private int itemCount;
        
        /** 创建时间 */
        private LocalDateTime createdAt;
    }
}