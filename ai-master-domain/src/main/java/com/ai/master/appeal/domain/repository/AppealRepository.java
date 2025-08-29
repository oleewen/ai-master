package com.ai.master.appeal.domain.repository;

import com.ai.master.appeal.domain.entity.Appeal;
import com.ai.master.appeal.domain.enums.AppealStatus;
import com.ai.master.appeal.domain.valueobject.AppealId;
import com.ai.master.appeal.domain.valueobject.AppealerId;

import java.util.List;
import java.util.Optional;

/**
 * 申诉仓储接口
 * 定义申诉数据的持久化操作
 */
public interface AppealRepository {
    
    /**
     * 保存申诉单
     */
    void save(Appeal appeal);
    
    /**
     * 根据ID查询申诉单
     */
    Optional<Appeal> findById(AppealId id);
    
    /**
     * 根据申诉方ID查询申诉单列表
     */
    List<Appeal> findByAppealerId(AppealerId appealerId);
    
    /**
     * 根据状态查询申诉单列表
     */
    List<Appeal> findByStatus(AppealStatus status);
    
    /**
     * 查询待审核的申诉单
     */
    List<Appeal> findPendingAudit();
    
    /**
     * 分页查询申诉单列表
     */
    List<Appeal> findAll(int page, int size);
    
    /**
     * 统计申诉单数量
     */
    long count();
    
    /**
     * 删除申诉单
     */
    void delete(AppealId id);
    
    /**
     * 判断申诉单是否存在
     */
    boolean exists(AppealId id);
}