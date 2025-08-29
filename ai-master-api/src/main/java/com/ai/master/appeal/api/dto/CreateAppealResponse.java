package com.ai.master.appeal.api.dto;

import lombok.Data;

/**
 * 创建申诉响应DTO
 */
@Data
public class CreateAppealResponse {
    
    /** 申诉单ID */
    private Long appealId;
    
    /** 申诉状态 */
    private String status;
}