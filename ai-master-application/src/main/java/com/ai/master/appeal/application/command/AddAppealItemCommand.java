package com.ai.master.appeal.application.command;

import com.ai.master.appeal.application.dto.AppealItemDto;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 添加申诉项命令
 */
@Data
public class AddAppealItemCommand {
    
    /** 申诉单ID */
    @NotNull(message = "申诉单ID不能为空")
    private Long appealId;
    
    /** 申诉项信息 */
    @Valid
    private AppealItemDto appealItem;
}