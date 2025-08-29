package com.ai.master.appeal.application.service;

import com.ai.master.appeal.application.command.*;
import com.ai.master.appeal.application.dto.*;
import com.ai.master.appeal.domain.entity.Appeal;
import com.ai.master.appeal.domain.entity.Appealer;
import com.ai.master.appeal.domain.enums.AppealStatus;
import com.ai.master.appeal.domain.enums.AppealType;
import com.ai.master.appeal.domain.repository.AppealRepository;
import com.ai.master.appeal.domain.repository.AppealerRepository;
import com.ai.master.appeal.domain.service.AppealDomainService;
import com.ai.master.common.util.PageResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 申诉应用服务测试
 */
@RunWith(MockitoJUnitRunner.class)
public class AppealApplicationServiceTest {

    @Mock
    private AppealRepository appealRepository;

    @Mock
    private AppealerRepository appealerRepository;

    @Mock
    private AppealDomainService appealDomainService;

    @InjectMocks
    private AppealApplicationService appealApplicationService;

    @Before
    public void setUp() {
    }

    @Test
    public void testCreateAppealSuccess() {
        // 准备数据
        CreateAppealCommand command = new CreateAppealCommand();
        command.setAppealerId(1L);
        command.setReason("费用计算有误");
        
        CreateAppealCommand.AppealItemCommand item = new CreateAppealCommand.AppealItemCommand();
        item.setType("FEE");
        item.setTitle("运费申诉");
        item.setContent("实际运费与计算不符");
        command.setItems(Arrays.asList(item));

        // 模拟依赖
        when(appealerRepository.findById(any())).thenReturn(Optional.of(new Appealer()));
        when(appealDomainService.createAppeal(any(), any())).thenReturn(new Appeal());

        // 执行测试
        CreateAppealResult result = appealApplicationService.createAppeal(command);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getAppealId());
        verify(appealRepository, times(1)).save(any(Appeal.class));
    }

    @Test
    public void testAddAppealItemSuccess() {
        AddAppealItemCommand command = new AddAppealItemCommand();
        command.setAppealId(1L);
        
        com.ai.master.appeal.application.dto.AppealItemDto item = new com.ai.master.appeal.application.dto.AppealItemDto();
        item.setType(AppealType.FEE);
        item.setTitle("费用申诉");
        item.setContent("内容");
        command.setAppealItem(item);

        when(appealRepository.findById(any())).thenReturn(Optional.of(new Appeal()));

        AddAppealItemResult result = appealApplicationService.addAppealItem(command);

        assertNotNull(result);
        assertEquals(1L, result.getAppealId().longValue());
        verify(appealRepository, times(1)).save(any(Appeal.class));
    }

    @Test
    public void testDeleteAppealItemSuccess() {
        DeleteAppealItemCommand command = new DeleteAppealItemCommand();
        command.setAppealId(1L);
        command.setAppealItemId(1L);

        Appeal appeal = new Appeal();
        when(appealRepository.findById(any())).thenReturn(Optional.of(appeal));

        appealApplicationService.deleteAppealItem(command);

        verify(appealRepository, times(1)).save(any(Appeal.class));
    }

    @Test
    public void testSubmitAppealSuccess() {
        SubmitAppealCommand command = new SubmitAppealCommand();
        command.setAppealId(1L);

        Appeal appeal = new Appeal();
        when(appealRepository.findById(any())).thenReturn(Optional.of(appeal));

        appealApplicationService.submitAppeal(command);

        verify(appealDomainService, times(1)).submitAppeal(any(Appeal.class));
        verify(appealRepository, times(1)).save(any(Appeal.class));
    }

    @Test
    public void testGetAppealProgress() {
        QueryProgressRequest request = new QueryProgressRequest();
        request.setAppealId(1L);

        Appeal appeal = new Appeal();
        when(appealRepository.findById(any())).thenReturn(Optional.of(appeal));

        AppealProgressResult result = appealApplicationService.getAppealProgress(1L);

        assertNotNull(result);
        assertEquals(1L, result.getAppealId().longValue());
    }

    @Test
    public void testGetAppealResult() {
        QueryResultRequest request = new QueryResultRequest();
        request.setAppealId(1L);

        Appeal appeal = new Appeal();
        when(appealRepository.findById(any())).thenReturn(Optional.of(appeal));

        AppealResult result = appealApplicationService.getAppealResult(1L);

        assertNotNull(result);
        assertEquals(1L, result.getAppealId().longValue());
    }

    @Test
    public void testQueryAppealsForAudit() {
        QueryAppealsCommand command = new QueryAppealsCommand();
        command.setPage(1);
        command.setSize(10);

        List<Appeal> appeals = Arrays.asList(new Appeal(), new Appeal());
        when(appealRepository.findByStatus(any())).thenReturn(appeals);

        PageResult<AppealAuditQueryResult> result = appealApplicationService.queryAppealsForAudit(command);

        assertNotNull(result);
        assertNotNull(result.getContent());
    }

    @Test
    public void testAuditAppeal() {
        AuditAppealCommand command = new AuditAppealCommand();
        command.setAppealId(1L);
        command.setPassed(true);
        command.setAuditOpinion("同意申诉");

        Appeal appeal = new Appeal();
        when(appealRepository.findById(any())).thenReturn(Optional.of(appeal));

        appealApplicationService.auditAppeal(command);

        verify(appealDomainService, times(1)).auditAppeal(any(Appeal.class), anyBoolean());
        verify(appealRepository, times(1)).save(any(Appeal.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAppealWithInvalidAppealer() {
        CreateAppealCommand command = new CreateAppealCommand();
        command.setAppealerId(999L);
        command.setReason("测试");

        when(appealerRepository.findById(any())).thenReturn(Optional.empty());

        appealApplicationService.createAppeal(command);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAppealProgressWithInvalidAppealId() {
        when(appealRepository.findById(any())).thenReturn(Optional.empty());

        appealApplicationService.getAppealProgress(999L);
    }
}