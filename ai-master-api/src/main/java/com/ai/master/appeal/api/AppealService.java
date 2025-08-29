package com.ai.master.appeal.api;

import com.ai.master.appeal.api.dto.*;
import com.ai.master.common.result.Result;

/**
 * 申诉服务Dubbo接口
 * 基于申诉详设文档的业务能力定义
 */
public interface AppealService {
    
    /**
     * 1.1 发起申诉
     */
    Result<CreateAppealResponse> create(CreateAppealRequest request);
    
    /**
     * 1.2 增加申诉项
     */
    Result<AddAppealItemResponse> addItem(AddAppealItemRequest request);
    
    /**
     * 1.3 下载模板
     */
    Result<DownloadTemplateResponse> downloadTemplate(DownloadTemplateRequest request);
    
    /**
     * 1.4 导入申诉明细
     */
    Result<ImportItemsResponse> importItems(ImportItemsRequest request);
    
    /**
     * 1.5 删除申诉项
     */
    Result<Void> deleteItem(DeleteAppealItemRequest request);
    
    /**
     * 1.6 提交申诉
     */
    Result<Void> submit(SubmitAppealRequest request);
    
    /**
     * 1.7 查询申诉进展
     */
    Result<QueryProgressResponse> queryProgress(QueryProgressRequest request);
    
    /**
     * 1.8/3.3 查看申诉结果
     */
    Result<QueryResultResponse> queryResult(QueryResultRequest request);
    
    /**
     * 2.1 验证申诉
     */
    Result<ValidateAppealResponse> validate(ValidateAppealRequest request);
    
    /**
     * 3.1 申诉审核查询
     */
    Result<QueryAppealsForAuditResponse> queryForAudit(QueryAppealsForAuditRequest request);
    
    /**
     * 3.2 申诉审核
     */
    Result<Void> audit(AuditAppealRequest request);
    
    /**
     * 3.4 审核通知
     */
    Result<Void> notify(AuditNotificationRequest request);
}