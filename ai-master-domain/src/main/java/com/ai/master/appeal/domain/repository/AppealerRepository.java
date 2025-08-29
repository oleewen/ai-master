package com.ai.master.appeal.domain.repository;

import com.ai.master.appeal.domain.entity.Appealer;
import com.ai.master.appeal.domain.enums.AppealerType;
import com.ai.master.appeal.domain.valueobject.AppealerId;

import java.util.List;
import java.util.Optional;

/**
 * 申诉方仓储接口
 * 定义申诉方数据的持久化操作
 */
public interface AppealerRepository {
    
    /**
     * 保存申诉方
     */
    void save(Appealer appealer);
    
    /**
     * 根据ID查询申诉方
     */
    Optional<Appealer> findById(AppealerId id);
    
    /**
     * 根据编码查询申诉方
     */
    Optional<Appealer> findByCode(String code);
    
    /**
     * 根据类型查询申诉方列表
     */
    List<Appealer> findByType(AppealerType type);
    
    /**
     * 查询所有申诉方
     */
    List<Appealer> findAll();
    
    /**
     * 判断申诉方是否存在
     */
    boolean exists(AppealerId id);
    
    /**
     * 判断编码是否已存在
     */
    boolean existsByCode(String code);
}