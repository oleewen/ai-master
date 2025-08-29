package com.ai.master.appeal.api.dto;

import lombok.Data;

import java.util.List;

/**
 * 审核通知请求DTO
 */
@Data
public class AuditNotificationRequest {
    
    /** 申诉单ID */
    private Long appealId;
    
    /** 通过的申诉项 */
    private List<Long> approvedItems;
    
    /** 驳回的申诉项 */
    private List<Long> rejectedItems;
}