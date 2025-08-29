package com.ai.master.appeal.service;

import com.ai.master.appeal.api.dto.*;
import com.ai.master.appeal.application.command.*;
import com.ai.master.appeal.application.service.AppealApplicationService;
import com.ai.master.common.result.Result;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 申诉服务实现测试
 */
@RunWith(MockitoJUnitRunner.class)
public class AppealServiceImplTest {

    @Mock
    private AppealApplicationService appealApplicationService;

    @InjectMocks
    private AppealServiceImpl appealService;

    @Before
    public void setUp() {
    }

    @Test
    public void testCreateAppealSuccess() {
        CreateAppealRequest request = new CreateAppealRequest();
        request.setAppealerId(1L);
        request.setReason("费用计算有误");
        
        CreateAppealRequest.AppealItemDto item = new CreateAppealRequest.AppealItemDto();
        item.setType("FEE");
        item.setTitle("运费申诉");
        item.setContent("实际运费与计算不符");
        request.setItems(Arrays.asList(item));

        CreateAppealResult result = new CreateAppealResult();
        result.setAppealId(1L);
        result.setStatus(AppealStatus.CREATED);

        when(appealApplicationService.createAppeal(any(CreateAppealCommand.class))).thenReturn(result);

        Result<CreateAppealResponse> response = appealService.create(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(1L, response.getData().getAppealId().longValue());
    }

    @Test
    public void testAddAppealItemSuccess() {
        AddAppealItemRequest request = new AddAppealItemRequest();
        request.setAppealId(1L);
        request.setType("FEE");
        request.setTitle("费用申诉");
        request.setContent("内容");

        AddAppealItemResult result = new AddAppealItemResult();
        result.setAppealId(1L);
        result.setAppealItemId(1L);

        when(appealApplicationService.addAppealItem(any(AddAppealItemCommand.class))).thenReturn(result);

        Result<AddAppealItemResponse> response = appealService.addItem(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(1L, response.getData().getAppealItemId().longValue());
    }

    @Test
    public void testSubmitAppealSuccess() {
        SubmitAppealRequest request = new SubmitAppealRequest();
        request.setAppealId(1L);

        doNothing().when(appealApplicationService).submitAppeal(any(SubmitAppealCommand.class));

        Result<Void> response = appealService.submit(request);

        assertTrue(response.isSuccess());
    }

    @Test
    public void testQueryProgressSuccess() {
        QueryProgressRequest request = new QueryProgressRequest();
        request.setAppealId(1L);

        AppealProgressResult result = AppealProgressResult.builder()
                .appealId(1L)
                .status(AppealStatus.CREATED)
                .progress("申诉已创建，等待验证")
                .build();

        when(appealApplicationService.getAppealProgress(1L)).thenReturn(result);

        Result<QueryProgressResponse> response = appealService.queryProgress(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(1L, response.getData().getAppealId().longValue());
    }

    @Test
    public void testQueryResultSuccess() {
        QueryResultRequest request = new QueryResultRequest();
        request.setAppealId(1L);

        AppealResult result = AppealResult.builder()
                .appealId(1L)
                .reason("测试申诉")
                .status(AppealStatus.ALL_PASSED)
                .build();

        when(appealApplicationService.getAppealResult(1L)).thenReturn(result);

        Result<QueryResultResponse> response = appealService.queryResult(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(1L, response.getData().getAppealId().longValue());
    }

    @Test
    public void testAuditAppealSuccess() {
        AuditAppealRequest request = new AuditAppealRequest();
        request.setAppealId(1L);
        request.setPassed(true);
        request.setAuditOpinion("同意申诉");

        doNothing().when(appealApplicationService).auditAppeal(any(AuditAppealCommand.class));

        Result<Void> response = appealService.audit(request);

        assertTrue(response.isSuccess());
    }

    @Test
    public void testDownloadTemplateSuccess() {
        DownloadTemplateRequest request = new DownloadTemplateRequest();
        request.setTemplateType("appeal");

        Result<DownloadTemplateResponse> response = appealService.downloadTemplate(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals("appeal_template.csv", response.getData().getFileName());
    }

    @Test
    public void testImportItemsSuccess() {
        ImportItemsRequest request = new ImportItemsRequest();
        request.setAppealId(1L);
        request.setFileContent("测试内容".getBytes());
        request.setFileName("test.csv");

        Result<ImportItemsResponse> response = appealService.importItems(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertTrue(response.getData().isSuccess());
    }

    @Test
    public void testValidateAppealSuccess() {
        ValidateAppealRequest request = new ValidateAppealRequest();
        request.setAppealId(1L);

        ValidateAppealResponse response = new ValidateAppealResponse();
        response.setValid(true);

        Result<ValidateAppealResponse> result = appealService.validate(request);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isValid());
    }

    @Test
    public void testQueryAppealsForAudit() {
        QueryAppealsForAuditRequest request = new QueryAppealsForAuditRequest();
        request.setPage(1);
        request.setSize(10);

        PageResult<AppealAuditQueryResult> pageResult = new PageResult<>();
        pageResult.setContent(Arrays.asList(new AppealAuditQueryResult(), new AppealAuditQueryResult()));
        pageResult.setTotal(2);

        when(appealApplicationService.queryAppealsForAudit(any(QueryAppealsCommand.class))).thenReturn(pageResult);

        Result<QueryAppealsForAuditResponse> response = appealService.queryForAudit(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().getAppeals().size());
    }

    @Test
    public void testDeleteAppealItemSuccess() {
        DeleteAppealItemRequest request = new DeleteAppealItemRequest();
        request.setAppealId(1L);
        request.setAppealItemId(1L);

        doNothing().when(appealApplicationService).deleteAppealItem(any(DeleteAppealItemCommand.class));

        Result<Void> response = appealService.deleteItem(request);

        assertTrue(response.isSuccess());
    }

    @Test
    public void testNotifySuccess() {
        AuditNotificationRequest request = new AuditNotificationRequest();
        request.setAppealId(1L);

        Result<Void> response = appealService.notify(request);

        assertTrue(response.isSuccess());
    }

    @Test
    public void testCreateAppealWithException() {
        CreateAppealRequest request = new CreateAppealRequest();
        request.setAppealerId(1L);
        request.setReason("测试");

        when(appealApplicationService.createAppeal(any(CreateAppealCommand.class)))
                .thenThrow(new RuntimeException("测试异常"));

        Result<CreateAppealResponse> response = appealService.create(request);

        assertFalse(response.isSuccess());
        assertEquals("测试异常", response.getMessage());
    }
}