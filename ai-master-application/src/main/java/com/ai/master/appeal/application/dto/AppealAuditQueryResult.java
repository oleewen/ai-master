package com.ai.master.appeal.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 申诉审核查询结果DTO
 */
@Data
@Builder
public class AppealAuditQueryResult {
    
    /** 申诉单ID */
    private Long appealId;
    
    /** 申诉理由 */
    private String reason;
    
    /** 申诉项数量 */
    private int itemCount;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
}