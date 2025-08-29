package com.ai.master.appeal.application.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 添加申诉项结果
 */
@Data
@Builder
public class AddAppealItemResult {
    
    /** 申诉单ID */
    private Long appealId;
    
    /** 申诉项ID */
    private Long appealItemId;
}