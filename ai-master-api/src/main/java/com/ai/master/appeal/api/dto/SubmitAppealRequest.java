package com.ai.master.appeal.api.dto;

import lombok.Data;

/**
 * 提交申诉请求DTO
 */
@Data
public class SubmitAppealRequest {
    
    /** 申诉单ID */
    private Long appealId;
}