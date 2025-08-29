package com.ai.master.appeal.infrastructure.persistence.repository;

import com.ai.master.appeal.domain.entity.Appeal;
import com.ai.master.appeal.domain.enums.AppealStatus;
import com.ai.master.appeal.domain.valueobject.AppealId;
import com.ai.master.appeal.domain.valueobject.AppealerId;
import com.ai.master.appeal.infrastructure.persistence.entity.AppealEntity;
import com.ai.master.appeal.infrastructure.persistence.mapper.AppealMapper;
import com.ai.master.appeal.infrastructure.persistence.mapper.AppealItemMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 申诉仓储实现测试
 */
@RunWith(MockitoJUnitRunner.class)
public class AppealRepositoryImplTest {

    @Mock
    private AppealMapper appealMapper;

    @Mock
    private AppealItemMapper appealItemMapper;

    @InjectMocks
    private AppealRepositoryImpl appealRepository;

    private Appeal appeal;
    private AppealEntity appealEntity;

    @Before
    public void setUp() {
        appeal = new Appeal();
        appeal.setId(AppealId.of(1L));
        appeal.setAppealerId(AppealerId.of(1L));
        appeal.setReason("测试申诉");
        appeal.setStatus(AppealStatus.CREATED);

        appealEntity = new AppealEntity();
        appealEntity.setId(1L);
        appealEntity.setAppealerId(1L);
        appealEntity.setReason("测试申诉");
        appealEntity.setStatus("CREATED");
    }

    @Test
    public void testSaveNewAppeal() {
        when(appealMapper.selectByPrimaryKey(1L)).thenReturn(null);

        appealRepository.save(appeal);

        verify(appealMapper, times(1)).insertSelective(any(AppealEntity.class));
    }

    @Test
    public void testSaveExistingAppeal() {
        when(appealMapper.selectByPrimaryKey(1L)).thenReturn(appealEntity);

        appealRepository.save(appeal);

        verify(appealMapper, times(1)).updateByPrimaryKeySelective(any(AppealEntity.class));
    }

    @Test
    public void testFindByIdSuccess() {
        when(appealMapper.selectByPrimaryKey(1L)).thenReturn(appealEntity);

        Optional<Appeal> result = appealRepository.findById(AppealId.of(1L));

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId().getValue().longValue());
    }

    @Test
    public void testFindByIdNotFound() {
        when(appealMapper.selectByPrimaryKey(1L)).thenReturn(null);

        Optional<Appeal> result = appealRepository.findById(AppealId.of(1L));

        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByAppealerId() {
        AppealEntity entity = new AppealEntity();
        entity.setId(1L);
        entity.setAppealerId(1L);

        when(appealMapper.selectByExample(any(Example.class))).thenReturn(Arrays.asList(entity));

        List<Appeal> result = appealRepository.findByAppealerId(AppealerId.of(1L));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getAppealerId().getValue().longValue());
    }

    @Test
    public void testFindByStatus() {
        AppealEntity entity = new AppealEntity();
        entity.setId(1L);
        entity.setStatus("CREATED");

        when(appealMapper.selectByExample(any(Example.class))).thenReturn(Arrays.asList(entity));

        List<Appeal> result = appealRepository.findByStatus(AppealStatus.CREATED);

        assertEquals(1, result.size());
    }

    @Test
    public void testFindPendingAudit() {
        AppealEntity entity = new AppealEntity();
        entity.setId(1L);
        entity.setStatus("SUBMITTED");

        when(appealMapper.selectByExample(any(Example.class))).thenReturn(Arrays.asList(entity));

        List<Appeal> result = appealRepository.findPendingAudit();

        assertEquals(1, result.size());
    }

    @Test
    public void testDelete() {
        appealRepository.delete(AppealId.of(1L));

        verify(appealMapper, times(1)).deleteByPrimaryKey(1L);
    }

    @Test
    public void testExists() {
        when(appealMapper.selectByPrimaryKey(1L)).thenReturn(appealEntity);

        boolean result = appealRepository.exists(AppealId.of(1L));

        assertTrue(result);
    }

    @Test
    public void testCount() {
        when(appealMapper.selectCount(null)).thenReturn(5L);

        long result = appealRepository.count();

        assertEquals(5L, result);
    }
}