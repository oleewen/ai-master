package com.ai.master.appeal.application.dto;

import com.ai.master.appeal.domain.enums.AppealType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 申诉项DTO
 */
@Data
public class AppealItemDto {
    
    /** 申诉类型 */
    @NotNull(message = "申诉类型不能为空")
    private AppealType type;
    
    /** 申诉项标题 */
    @NotBlank(message = "申诉项标题不能为空")
    @Size(max = 100, message = "申诉项标题不能超过100字符")
    private String title;
    
    /** 申诉内容 */
    @NotBlank(message = "申诉内容不能为空")
    @Size(max = 1000, message = "申诉内容不能超过1000字符")
    private String content;
}