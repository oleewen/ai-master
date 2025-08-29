package com.ai.master.appeal.application.command;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 删除申诉项命令
 */
@Data
public class DeleteAppealItemCommand {
    
    /** 申诉单ID */
    @NotNull(message = "申诉单ID不能为空")
    private Long appealId;
    
    /** 申诉项ID */
    @NotNull(message = "申诉项ID不能为空")
    private Long appealItemId;
}