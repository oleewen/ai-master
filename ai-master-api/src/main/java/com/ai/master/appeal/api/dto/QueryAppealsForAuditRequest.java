package com.ai.master.appeal.api.dto;

import lombok.Data;

/**
 * 申诉审核查询请求DTO
 */
@Data
public class QueryAppealsForAuditRequest {
    
    /** 页码 */
    private int page = 1;
    
    /** 每页数量 */
    private int size = 20;
}