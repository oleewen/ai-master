package com.ai.master.appeal.application.dto;

import com.ai.master.appeal.domain.enums.AppealStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 申诉列表结果DTO
 */
@Data
@Builder
public class AppealListResult {
    
    /** 申诉单ID */
    private Long appealId;
    
    /** 申诉理由 */
    private String reason;
    
    /** 申诉状态 */
    private AppealStatus status;
    
    /** 申诉项数量 */
    private int itemCount;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
}