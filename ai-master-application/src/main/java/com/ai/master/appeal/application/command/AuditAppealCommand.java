package com.ai.master.appeal.application.command;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 审核申诉命令
 */
@Data
public class AuditAppealCommand {
    
    /** 申诉单ID */
    @NotNull(message = "申诉单ID不能为空")
    private Long appealId;
    
    /** 审核结果 */
    @NotNull(message = "审核结果不能为空")
    private Boolean passed;
    
    /** 审核意见 */
    private String auditOpinion;
    
    /** 申诉项审核信息 */
    @Valid
    private List<AppealItemAuditCommand> itemAudits;
    
    @Data
    public static class AppealItemAuditCommand {
        /** 申诉项ID */
        @NotNull(message = "申诉项ID不能为空")
        private Long itemId;
        
        /** 审核结果 */
        @NotNull(message = "审核结果不能为空")
        private Boolean passed;
        
        /** 审核意见 */
        private String opinion;
    }
}