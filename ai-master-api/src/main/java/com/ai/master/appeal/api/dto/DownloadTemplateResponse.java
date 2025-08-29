package com.ai.master.appeal.api.dto;

import lombok.Data;

/**
 * 下载模板响应DTO
 */
@Data
public class DownloadTemplateResponse {
    
    /** 文件名 */
    private String fileName;
    
    /** 内容类型 */
    private String contentType;
    
    /** 文件内容 */
    private byte[] content;
}