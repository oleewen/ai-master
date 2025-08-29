package com.ai.master.appeal.api.dto;

import lombok.Data;

/**
 * 添加申诉项响应DTO
 */
@Data
public class AddAppealItemResponse {
    
    /** 申诉单ID */
    private Long appealId;
    
    /** 申诉项ID */
    private Long appealItemId;
}