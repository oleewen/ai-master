package com.ai.master.appeal.api.dto;

import lombok.Data;

/**
 * 导入申诉明细请求DTO
 */
@Data
public class ImportItemsRequest {
    
    /** 申诉单ID */
    private Long appealId;
    
    /** 文件内容 */
    private byte[] fileContent;
    
    /** 文件名 */
    private String fileName;
}