package com.ai.master.appeal.api.dto;

import lombok.Data;

/**
 * 查询申诉进展请求DTO
 */
@Data
public class QueryProgressRequest {
    
    /** 申诉单ID */
    private Long appealId;
}