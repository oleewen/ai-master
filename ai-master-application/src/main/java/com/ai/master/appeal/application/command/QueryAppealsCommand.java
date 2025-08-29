package com.ai.master.appeal.application.command;

import com.ai.master.appeal.domain.enums.AppealStatus;
import lombok.Data;

import javax.validation.constraints.Min;

/**
 * 查询申诉列表命令
 */
@Data
public class QueryAppealsCommand {
    
    /** 申诉状态 */
    private AppealStatus status;
    
    /** 页码 */
    @Min(value = 1, message = "页码必须大于0")
    private int page = 1;
    
    /** 每页数量 */
    @Min(value = 1, message = "每页数量必须大于0")
    private int size = 20;
}