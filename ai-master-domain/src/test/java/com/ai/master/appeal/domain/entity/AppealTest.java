package com.ai.master.appeal.domain.entity;

import com.ai.master.appeal.domain.enums.AppealStatus;
import com.ai.master.appeal.domain.enums.AppealType;
import com.ai.master.appeal.domain.enums.AppealItemStatus;
import com.ai.master.appeal.domain.valueobject.AppealId;
import com.ai.master.appeal.domain.valueobject.AppealerId;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 申诉单实体测试
 */
public class AppealTest {

    private Appeal appeal;
    private AppealerId appealerId;

    @Before
    public void setUp() {
        appealerId = AppealerId.of(1L);
        appeal = Appeal.create(appealerId, "费用计算有误");
    }

    @Test
    public void testCreateAppeal() {
        assertNotNull(appeal.getId());
        assertEquals(appealerId, appeal.getAppealerId());
        assertEquals("费用计算有误", appeal.getReason());
        assertEquals(AppealStatus.CREATED, appeal.getStatus());
    }

    @Test
    public void testAddItemSuccess() {
        AppealItem item = AppealItem.create(AppealType.FEE, "运费申诉", "实际运费与计算不符");
        appeal.addItem(item);
        
        assertEquals(1, appeal.getItems().size());
        assertEquals(item, appeal.getItems().get(0));
    }

    @Test
    public void testAddItemAfterSubmitShouldFail() {
        appeal.setStatus(AppealStatus.SUBMITTED);
        AppealItem item = AppealItem.create(AppealType.FEE, "运费申诉", "内容");
        
        try {
            appeal.addItem(item);
            fail("应该抛出异常");
        } catch (IllegalStateException e) {
            assertEquals("当前状态不允许添加申诉项", e.getMessage());
        }
    }

    @Test
    public void testSubmitAppealSuccess() {
        appeal.setStatus(AppealStatus.VERIFIED);
        AppealItem item = AppealItem.create(AppealType.FEE, "运费申诉", "内容");
        item.setStatus(AppealItemStatus.VALIDATION_PASSED);
        appeal.addItem(item);
        
        appeal.submit();
        
        assertEquals(AppealStatus.SUBMITTED, appeal.getStatus());
        assertEquals(AppealItemStatus.PENDING_AUDIT, appeal.getItems().get(0).getStatus());
    }

    @Test
    public void testSubmitAppealWithoutItemsShouldFail() {
        appeal.setStatus(AppealStatus.VERIFIED);
        
        try {
            appeal.submit();
            fail("应该抛出异常");
        } catch (IllegalStateException e) {
            assertEquals("申诉单必须包含至少一个申诉项", e.getMessage());
        }
    }

    @Test
    public void testCancelAppealSuccess() {
        appeal.cancel();
        assertEquals(AppealStatus.CANCELLED, appeal.getStatus());
    }

    @Test
    public void testCancelCompletedAppealShouldFail() {
        appeal.setStatus(AppealStatus.ALL_PASSED);
        
        try {
            appeal.cancel();
            fail("应该抛出异常");
        } catch (IllegalStateException e) {
            assertEquals("已完成的申诉不能取消", e.getMessage());
        }
    }

    @Test
    public void testAuditAppealAllPassed() {
        appeal.setStatus(AppealStatus.VERIFIED);
        
        AppealItem item1 = AppealItem.create(AppealType.FEE, "运费申诉", "内容1");
        AppealItem item2 = AppealItem.create(AppealType.WEIGHT, "重量申诉", "内容2");
        
        appeal.addItem(item1);
        appeal.addItem(item2);
        
        // 设置验证通过状态
        item1.setStatus(AppealItemStatus.VALIDATION_PASSED);
        item2.setStatus(AppealItemStatus.VALIDATION_PASSED);
        
        // 提交申诉
        appeal.submit();
        
        // 模拟审核过程
        item1.audit(true, "审核通过");
        item2.audit(true, "审核通过");
        
        appeal.audit(true);
        assertEquals(AppealStatus.ALL_PASSED, appeal.getStatus());
    }

    @Test
    public void testAuditAppealAllRejected() {
        appeal.setStatus(AppealStatus.VERIFIED);
        
        AppealItem item1 = AppealItem.create(AppealType.FEE, "运费申诉", "内容1");
        AppealItem item2 = AppealItem.create(AppealType.WEIGHT, "重量申诉", "内容2");
        
        appeal.addItem(item1);
        appeal.addItem(item2);
        
        // 设置验证通过状态
        item1.setStatus(AppealItemStatus.VALIDATION_PASSED);
        item2.setStatus(AppealItemStatus.VALIDATION_PASSED);
        
        // 提交申诉
        appeal.submit();
        
        // 模拟审核过程
        item1.audit(false, "审核驳回");
        item2.audit(false, "审核驳回");
        
        appeal.audit(false);
        assertEquals(AppealStatus.ALL_REJECTED, appeal.getStatus());
    }

    @Test
    public void testRemoveItemSuccess() {
        AppealItem item = AppealItem.create(AppealType.FEE, "运费申诉", "内容");
        appeal.addItem(item);
        
        appeal.removeItem(item.getId().getValue());
        
        assertEquals(0, appeal.getItems().size());
    }
}