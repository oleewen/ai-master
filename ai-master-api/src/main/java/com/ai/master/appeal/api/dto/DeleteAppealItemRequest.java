package com.ai.master.appeal.api.dto;

import lombok.Data;

/**
 * 删除申诉项请求DTO
 */
@Data
public class DeleteAppealItemRequest {
    
    /** 申诉单ID */
    private Long appealId;
    
    /** 申诉项ID */
    private Long appealItemId;
}