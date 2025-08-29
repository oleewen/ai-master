package com.ai.master.appeal.api.dto;

import lombok.Data;

/**
 * 下载模板请求DTO
 */
@Data
public class DownloadTemplateRequest {
    
    /** 模板类型 */
    private String templateType;
}