package com.ai.master.appeal.infrastructure.persistence.repository;

import com.ai.master.appeal.domain.entity.Appeal;
import com.ai.master.appeal.domain.entity.AppealItem;
import com.ai.master.appeal.domain.enums.AppealStatus;
import com.ai.master.appeal.domain.enums.AppealType;
import com.ai.master.appeal.domain.enums.AppealItemStatus;
import com.ai.master.appeal.domain.valueobject.AppealId;
import com.ai.master.appeal.domain.valueobject.AppealItemId;
import com.ai.master.appeal.domain.valueobject.AppealerId;
import com.ai.master.appeal.infrastructure.persistence.entity.AppealEntity;
import com.ai.master.appeal.infrastructure.persistence.entity.AppealItemEntity;
import com.ai.master.appeal.infrastructure.persistence.mapper.AppealMapper;
import com.ai.master.appeal.infrastructure.persistence.mapper.AppealItemMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 申诉仓储集成测试 - 验证时间戳管理
 */
@ExtendWith(MockitoExtension.class)
class AppealRepositoryImplIntegrationTest {

    @Mock
    private AppealMapper appealMapper;

    @Mock
    private AppealItemMapper appealItemMapper;

    @InjectMocks
    private AppealRepositoryImpl appealRepository;

    private Appeal testAppeal;
    private AppealEntity testAppealEntity;
    private AppealItemEntity testAppealItemEntity;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testAppeal = Appeal.create(AppealerId.of(1001L), "测试申诉原因");
        testAppeal.addItem(AppealItem.create(AppealId.of(1L), AppealType.PRICE_DISPUTE, "商品A价格问题"));
        testAppeal.addItem(AppealItem.create(AppealId.of(1L), AppealType.QUALITY_ISSUE, "商品B质量问题"));

        testAppealEntity = new AppealEntity();
        testAppealEntity.setId(1L);
        testAppealEntity.setAppealerId(1001L);
        testAppealEntity.setReason("测试申诉原因");
        testAppealEntity.setStatus(AppealStatus.CREATED.name());
        testAppealEntity.setAttachments("");
        testAppealEntity.setCreatedAt(new Date());
        testAppealEntity.setUpdatedAt(new Date());

        testAppealItemEntity = new AppealItemEntity();
        testAppealItemEntity.setId(1L);
        testAppealItemEntity.setAppealId(1L);
        testAppealItemEntity.setType(AppealType.PRICE_DISPUTE.name());
        testAppealItemEntity.setTitle("商品A价格问题");
        testAppealItemEntity.setStatus(AppealItemStatus.PENDING_AUDIT.name());
        testAppealItemEntity.setContent("价格申诉内容");
        testAppealItemEntity.setCreatedAt(new Date());
        testAppealItemEntity.setUpdatedAt(new Date());
    }

    @Test
    void should_manage_timestamps_on_save_new_appeal() {
        // Given
        when(appealMapper.selectByPrimaryKey(1L)).thenReturn(null);

        // When
        appealRepository.save(testAppeal);

        // Then
        verify(appealMapper).insertSelective(argThat(entity -> {
            assertNotNull(entity.getCreatedAt());
            assertNotNull(entity.getUpdatedAt());
            assertEquals(entity.getCreatedAt(), entity.getUpdatedAt());
            return true;
        }));
    }

    @Test
    void should_manage_timestamps_on_update_existing_appeal() {
        // Given
        when(appealMapper.selectByPrimaryKey(1L)).thenReturn(testAppealEntity);

        // When
        appealRepository.save(testAppeal);

        // Then
        verify(appealMapper).updateByPrimaryKeySelective(argThat(entity -> {
            assertNotNull(entity.getUpdatedAt());
            assertNotEquals(testAppealEntity.getUpdatedAt(), entity.getUpdatedAt());
            return true;
        }));
    }

    @Test
    void should_manage_timestamps_on_save_appeal_items() {
        // Given
        when(appealMapper.selectByPrimaryKey(1L)).thenReturn(null);
        when(appealItemMapper.selectByPrimaryKey(anyLong())).thenReturn(null);

        // When
        appealRepository.save(testAppeal);

        // Then
        verify(appealItemMapper, times(2)).insertSelective(argThat(entity -> {
            assertNotNull(entity.getCreatedAt());
            assertNotNull(entity.getUpdatedAt());
            assertEquals(entity.getCreatedAt(), entity.getUpdatedAt());
            return true;
        }));
    }

    @Test
    void should_not_expose_timestamps_in_domain_objects() {
        // Given
        when(appealMapper.selectByPrimaryKey(1L)).thenReturn(testAppealEntity);

        // When
        Optional<Appeal> appealOpt = appealRepository.findById(AppealId.of(1L));

        // Then
        assertTrue(appealOpt.isPresent());
        Appeal appeal = appealOpt.get();
        
        // 验证领域对象没有创建时间和修改时间属性
        assertDoesNotThrow(() -> {
            appeal.getClass().getDeclaredMethod("getCreatedAt");
        }, "领域对象不应该有getCreatedAt方法");
        
        assertDoesNotThrow(() -> {
            appeal.getClass().getDeclaredMethod("getUpdatedAt");
        }, "领域对象不应该有getUpdatedAt方法");
    }

    @Test
    void should_map_appeal_without_time_attributes() {
        // Given
        Appeal appeal = Appeal.create(AppealerId.of(1001L), "测试申诉");
        appeal.submit();

        // When
        AppealEntity entity = appealRepository.toEntity(appeal);

        // Then
        assertEquals(appeal.getId().getValue(), entity.getId());
        assertEquals(appeal.getAppealerId().getValue(), entity.getAppealerId());
        assertEquals(appeal.getReason(), entity.getReason());
        assertEquals(appeal.getStatus().name(), entity.getStatus());
        
        // 验证时间属性由基础设施层管理
        assertNull(entity.getCreatedAt());  // 由仓储层设置
        assertNull(entity.getUpdatedAt());  // 由仓储层设置
    }

    @Test
    void should_handle_null_attachments_gracefully() {
        // Given
        Appeal appeal = Appeal.create(AppealerId.of(1001L), "无附件申诉");

        // When
        AppealEntity entity = appealRepository.toEntity(appeal);

        // Then
        assertNull(entity.getAttachments());
    }

    @Test
    void should_handle_empty_attachments() {
        // Given
        Appeal appeal = Appeal.create(AppealerId.of(1001L), "空附件申诉");
        appeal.setAttachments(List.of());

        // When
        AppealEntity entity = appealRepository.toEntity(appeal);

        // Then
        assertEquals("", entity.getAttachments());
    }
}