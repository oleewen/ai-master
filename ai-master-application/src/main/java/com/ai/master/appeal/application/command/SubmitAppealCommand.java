package com.ai.master.appeal.application.command;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 提交申诉命令
 */
@Data
public class SubmitAppealCommand {
    
    /** 申诉单ID */
    @NotNull(message = "申诉单ID不能为空")
    private Long appealId;
}