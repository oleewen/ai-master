package com.ai.master.appeal.api.dto;

import lombok.Data;

/**
 * 导入申诉明细响应DTO
 */
@Data
public class ImportItemsResponse {
    
    /** 是否成功 */
    private boolean success;
    
    /** 总数量 */
    private int totalCount;
    
    /** 成功数量 */
    private int successCount;
    
    /** 失败数量 */
    private int failCount;
    
    /** 失败项目列表 */
    private List<String> failItems;
}