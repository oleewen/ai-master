package com.ai.master.appeal.application.command;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 创建申诉命令
 */
@Data
public class CreateAppealCommand {
    
    /** 申诉方ID */
    @NotNull(message = "申诉方不能为空")
    private Long appealerId;
    
    /** 申诉理由 */
    @NotBlank(message = "申诉理由不能为空")
    @Size(max = 500, message = "申诉理由不能超过500字符")
    private String reason;
    
    /** 申诉项列表 */
    @Valid
    private List<AppealItemCommand> items;
    
    @Data
    public static class AppealItemCommand {
        /** 申诉类型 */
        @NotNull(message = "申诉类型不能为空")
        private String type;
        
        /** 申诉项标题 */
        @NotBlank(message = "申诉项标题不能为空")
        @Size(max = 100, message = "申诉项标题不能超过100字符")
        private String title;
        
        /** 申诉内容 */
        @NotBlank(message = "申诉内容不能为空")
        @Size(max = 1000, message = "申诉内容不能超过1000字符")
        private String content;
    }
}