package com.ai.master.appeal.domain.service;

import com.ai.master.appeal.domain.entity.Appeal;
import com.ai.master.appeal.domain.entity.AppealItem;
import com.ai.master.appeal.domain.enums.AppealType;
import com.ai.master.appeal.domain.enums.AppealItemStatus;
import com.ai.master.appeal.domain.valueobject.AppealerId;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 申诉领域服务测试
 */
public class AppealDomainServiceTest {

    private AppealDomainService appealDomainService;
    private AppealValidationService validationService;
    private AppealerId appealerId;

    @Before
    public void setUp() {
        appealDomainService = new AppealDomainService();
        validationService = new AppealValidationService();
        appealerId = AppealerId.of(1L);
    }

    @Test
    public void testCreateAppeal() {
        Appeal appeal = appealDomainService.createAppeal(appealerId, "测试申诉");
        
        assertNotNull(appeal);
        assertEquals(appealerId, appeal.getAppealerId());
        assertEquals("测试申诉", appeal.getReason());
    }

    @Test
    public void testAddAppealItem() {
        Appeal appeal = appealDomainService.createAppeal(appealerId, "测试申诉");
        
        appealDomainService.addAppealItem(appeal, AppealType.FEE, "费用申诉", "内容");
        
        assertEquals(1, appeal.getItems().size());
        assertEquals(AppealType.FEE, appeal.getItems().get(0).getType());
    }

    @Test
    public void testValidateAppeal() {
        Appeal appeal = appealDomainService.createAppeal(appealerId, "测试申诉");
        appealDomainService.addAppealItem(appeal, AppealType.FEE, "费用申诉", "内容");
        
        appealDomainService.validateAppeal(appeal);
        
        assertTrue(appeal.getItems().size() > 0);
    }

    @Test
    public void testSubmitAppeal() {
        Appeal appeal = appealDomainService.createAppeal(appealerId, "测试申诉");
        AppealItem item = AppealItem.create(AppealType.FEE, "费用申诉", "内容");
        item.setStatus(AppealItemStatus.VALIDATION_PASSED);
        appeal.addItem(item);
        
        appealDomainService.validateAppeal(appeal);
        appealDomainService.submitAppeal(appeal);
        
        assertNotNull(appeal);
    }

    @Test
    public void testAuditAppeal() {
        Appeal appeal = appealDomainService.createAppeal(appealerId, "测试申诉");
        AppealItem item = AppealItem.create(AppealType.FEE, "费用申诉", "内容");
        item.setStatus(AppealItemStatus.VALIDATION_PASSED);
        appeal.addItem(item);
        
        appealDomainService.validateAppeal(appeal);
        appealDomainService.submitAppeal(appeal);
        appealDomainService.auditAppeal(appeal, true);
        
        assertNotNull(appeal);
    }

    @Test
    public void testCalculatePassRate() {
        Appeal appeal = appealDomainService.createAppeal(appealerId, "测试申诉");
        
        AppealItem item1 = AppealItem.create(AppealType.FEE, "费用申诉", "内容1");
        AppealItem item2 = AppealItem.create(AppealType.WEIGHT, "重量申诉", "内容2");
        
        item1.setStatus(AppealItemStatus.AUDIT_PASSED);
        item2.setStatus(AppealItemStatus.AUDIT_REJECTED);
        
        appeal.addItem(item1);
        appeal.addItem(item2);
        
        double passRate = appealDomainService.calculatePassRate(appeal);
        assertEquals(0.5, passRate, 0.01);
    }

    @Test
    public void testValidateAppealWithNullItemsShouldFail() {
        Appeal appeal = appealDomainService.createAppeal(appealerId, "测试申诉");
        
        try {
            appealDomainService.validateAppeal(appeal);
            fail("应该抛出异常");
        } catch (IllegalArgumentException e) {
            assertEquals("申诉单必须包含至少一个申诉项", e.getMessage());
        }
    }
}