package com.ai.master.appeal.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 添加申诉项请求DTO
 */
@Data
public class AddAppealItemRequest {
    
    /** 申诉单ID */
    @NotNull(message = "申诉单ID不能为空")
    private Long appealId;
    
    /** 申诉类型 */
    @NotBlank(message = "申诉类型不能为空")
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