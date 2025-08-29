package com.ai.master.appeal.api.dto;

import lombok.Data;

import java.util.List;

/**
 * 审核申诉请求DTO
 */
@Data
public class AuditAppealRequest {
    
    /** 申诉单ID */
    private Long appealId;
    
    /** 审核结果 */
    private boolean passed;
    
    /** 审核意见 */
    private String auditOpinion;
    
    /** 申诉项审核信息 */
    private List<AppealItemAuditDto> itemAudits;
    
    @Data
    public static class AppealItemAuditDto {
        /** 申诉项ID */
        private Long itemId;
        
        /** 审核结果 */
        private boolean passed;
        
        /** 审核意见 */
        private String opinion;
    }
}