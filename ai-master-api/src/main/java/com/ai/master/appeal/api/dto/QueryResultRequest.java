package com.ai.master.appeal.api.dto;

import lombok.Data;

/**
 * 查询申诉结果请求DTO
 */
@Data
public class QueryResultRequest {
    
    /** 申诉单ID */
    private Long appealId;
}