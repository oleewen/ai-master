package com.ai.master.appeal.domain.entity;

import com.ai.master.appeal.domain.enums.AppealType;
import com.ai.master.appeal.domain.enums.AppealItemStatus;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 申诉项实体测试
 */
public class AppealItemTest {

    private AppealItem appealItem;

    @Before
    public void setUp() {
        appealItem = AppealItem.create(AppealType.FEE, "运费申诉", "实际运费与计算不符");
    }

    @Test
    public void testCreateAppealItem() {
        assertNotNull(appealItem.getId());
        assertEquals(AppealType.FEE, appealItem.getType());
        assertEquals("运费申诉", appealItem.getTitle());
        assertEquals("实际运费与计算不符", appealItem.getContent());
        assertEquals(AppealItemStatus.CREATED, appealItem.getStatus());
    }

    @Test
    public void testValidateSuccess() {
        appealItem.validate(true, null);
        assertEquals(AppealItemStatus.VALIDATION_PASSED, appealItem.getStatus());
    }

    @Test
    public void testValidateFailed() {
        appealItem.validate(false, "数据不符");
        assertEquals(AppealItemStatus.VALIDATION_FAILED, appealItem.getStatus());
        assertTrue(appealItem.getContent().contains("验证失败原因: 数据不符"));
    }

    @Test
    public void testSubmitSuccess() {
        appealItem.setStatus(AppealItemStatus.VALIDATION_PASSED);
        appealItem.submit();
        assertEquals(AppealItemStatus.PENDING_AUDIT, appealItem.getStatus());
    }

    @Test
    public void testSubmitWithoutValidationShouldFail() {
        try {
            appealItem.submit();
            fail("应该抛出异常");
        } catch (IllegalStateException e) {
            assertEquals("申诉项状态不允许提交", e.getMessage());
        }
    }

    @Test
    public void testAuditPassed() {
        appealItem.setStatus(AppealItemStatus.PENDING_AUDIT);
        appealItem.audit(true, "审核通过");
        assertEquals(AppealItemStatus.AUDIT_PASSED, appealItem.getStatus());
        assertTrue(appealItem.getContent().contains("审核意见: 审核通过"));
    }

    @Test
    public void testAuditRejected() {
        appealItem.setStatus(AppealItemStatus.PENDING_AUDIT);
        appealItem.audit(false, "审核驳回");
        assertEquals(AppealItemStatus.AUDIT_REJECTED, appealItem.getStatus());
        assertTrue(appealItem.getContent().contains("审核意见: 审核驳回"));
    }

    @Test
    public void testAuditWithoutPendingShouldFail() {
        try {
            appealItem.audit(true, "意见");
            fail("应该抛出异常");
        } catch (IllegalStateException e) {
            assertEquals("申诉项状态不允许审核", e.getMessage());
        }
    }
}