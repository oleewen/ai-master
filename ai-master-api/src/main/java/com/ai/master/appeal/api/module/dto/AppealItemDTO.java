package com.ai.master.appeal.api.module.dto;

import lombok.Data;

/**
 * 申诉项DTO
 */
@Data
public class AppealItemDTO {
    private String appealCode;
    private String appealType;
    private Object content; // 可根据业务细化为具体类型
} 