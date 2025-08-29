package com.ai.master.appeal.api;

import com.ai.master.appeal.api.dto.DownloadTemplateRequest;
import com.ai.master.appeal.api.dto.DownloadTemplateResponse;
import com.ai.master.common.result.Result;

/**
 * 申诉模板服务Dubbo接口
 */
public interface AppealTemplateService {
    
    /**
     * 1.3 下载模板
     */
    Result<DownloadTemplateResponse> download(DownloadTemplateRequest request);
}