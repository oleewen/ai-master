package com.ai.master.appeal.api;

import com.ai.master.appeal.api.module.request.AppealCreateRequest;
import com.ai.master.appeal.api.module.response.AppealCreateResponse;

/**
 * 申诉服务接口
 * 
 * 提供申诉相关的RPC服务能力
 */
public interface AppealService {
    /**
     * 发起申诉
     *
     * @param request 申诉创建请求
     * @return 申诉创建响应
     */
    AppealCreateResponse create(AppealCreateRequest request);
} 