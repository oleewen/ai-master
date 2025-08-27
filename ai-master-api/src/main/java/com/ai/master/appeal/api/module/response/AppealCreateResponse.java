package com.ai.master.appeal.api.module.response;

import lombok.Data;

/**
 * 发起申诉响应对象
 */
@Data
public class AppealCreateResponse {
    private String appealId;
    private String status;
} 