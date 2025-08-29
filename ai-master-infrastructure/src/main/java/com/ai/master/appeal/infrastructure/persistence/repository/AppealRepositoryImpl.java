package com.ai.master.appeal.infrastructure.persistence.repository;

import com.ai.master.appeal.domain.entity.Appeal;
import com.ai.master.appeal.domain.entity.AppealItem;
import com.ai.master.appeal.domain.enums.AppealStatus;
import com.ai.master.appeal.domain.enums.AppealType;
import com.ai.master.appeal.domain.enums.AppealItemStatus;
import com.ai.master.appeal.domain.repository.AppealRepository;
import com.ai.master.appeal.domain.valueobject.AppealId;
import com.ai.master.appeal.domain.valueobject.AppealItemId;
import com.ai.master.appeal.domain.valueobject.AppealerId;
import com.ai.master.appeal.infrastructure.persistence.entity.AppealEntity;
import com.ai.master.appeal.infrastructure.persistence.entity.AppealItemEntity;
import com.ai.master.appeal.infrastructure.persistence.mapper.AppealItemMapper;
import com.ai.master.appeal.infrastructure.persistence.mapper.AppealMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 申诉仓储实现
 */
@Repository
@RequiredArgsConstructor
public class AppealRepositoryImpl implements AppealRepository {
    
    private final AppealMapper appealMapper;
    private final AppealItemMapper appealItemMapper;
    
    @Override
    public void save(Appeal appeal) {
        AppealEntity entity = toEntity(appeal);
        Date now = new Date();
        
        if (appealMapper.selectByPrimaryKey(entity.getId()) != null) {
            // 更新时只更新updatedAt
            entity.setUpdatedAt(now);
            appealMapper.updateByPrimaryKeySelective(entity);
        } else {
            // 新建时设置createdAt和updatedAt
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            appealMapper.insertSelective(entity);
        }
        
        // 保存申诉项
        if (appeal.getItems() != null) {
            appeal.getItems().forEach(item -> {
                AppealItemEntity itemEntity = toItemEntity(item);
                if (appealItemMapper.selectByPrimaryKey(itemEntity.getId()) != null) {
                    // 更新时只更新updatedAt
                    itemEntity.setUpdatedAt(now);
                    appealItemMapper.updateByPrimaryKeySelective(itemEntity);
                } else {
                    // 新建时设置createdAt和updatedAt
                    itemEntity.setCreatedAt(now);
                    itemEntity.setUpdatedAt(now);
                    appealItemMapper.insertSelective(itemEntity);
                }
            });
        }
    }
    
    @Override
    public Optional<Appeal> findById(AppealId id) {
        AppealEntity entity = appealMapper.selectByPrimaryKey(id.getValue());
        if (entity == null) {
            return Optional.empty();
        }
        
        Appeal appeal = toDomain(entity);
        
        // 加载申诉项
        Example example = new Example(AppealItemEntity.class);
        example.createCriteria().andEqualTo("appealId", id.getValue());
        List<AppealItemEntity> itemEntities = appealItemMapper.selectByExample(example);
        
        appeal.setItems(itemEntities.stream()
            .map(this::toItemDomain)
            .collect(Collectors.toList()));
        
        return Optional.of(appeal);
    }
    
    @Override
    public List<Appeal> findByAppealerId(AppealerId appealerId) {
        Example example = new Example(AppealEntity.class);
        example.createCriteria().andEqualTo("appealerId", appealerId.getValue());
        List<AppealEntity> entities = appealMapper.selectByExample(example);
        
        return entities.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Appeal> findByStatus(AppealStatus status) {
        Example example = new Example(AppealEntity.class);
        example.createCriteria().andEqualTo("status", status.name());
        List<AppealEntity> entities = appealMapper.selectByExample(example);
        
        return entities.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Appeal> findPendingAudit() {
        Example example = new Example(AppealEntity.class);
        example.createCriteria().andEqualTo("status", AppealStatus.SUBMITTED.name());
        List<AppealEntity> entities = appealMapper.selectByExample(example);
        
        return entities.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Appeal> findAll(int page, int size) {
        Example example = new Example(AppealEntity.class);
        example.orderBy("createdAt").desc();
        
        int offset = (page - 1) * size;
        example.setOrderByClause("created_at DESC LIMIT " + offset + ", " + size);
        
        List<AppealEntity> entities = appealMapper.selectByExample(example);
        
        return entities.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public long count() {
        return appealMapper.selectCount(null);
    }
    
    @Override
    public void delete(AppealId id) {
        appealMapper.deleteByPrimaryKey(id.getValue());
        
        // 删除关联的申诉项
        Example example = new Example(AppealItemEntity.class);
        example.createCriteria().andEqualTo("appealId", id.getValue());
        appealItemMapper.deleteByExample(example);
    }
    
    @Override
    public boolean exists(AppealId id) {
        return appealMapper.selectByPrimaryKey(id.getValue()) != null;
    }
    
    private AppealEntity toEntity(Appeal appeal) {
        AppealEntity entity = new AppealEntity();
        entity.setId(appeal.getId().getValue());
        entity.setAppealerId(appeal.getAppealerId().getValue());
        entity.setReason(appeal.getReason());
        entity.setStatus(appeal.getStatus().name());
        entity.setAttachments(appeal.getAttachments() != null ? 
            String.join(",", appeal.getAttachments()) : null);
        
        return entity;
    }
    
    private AppealItemEntity toItemEntity(AppealItem item) {
        AppealItemEntity entity = new AppealItemEntity();
        entity.setId(item.getId().getValue());
        entity.setAppealId(item.getAppealId().getValue());
        entity.setType(item.getType().name());
        entity.setTitle(item.getTitle());
        entity.setStatus(item.getStatus().name());
        entity.setContent(item.getContent());
        
        return entity;
    }
    
    private Appeal toDomain(AppealEntity entity) {
        Appeal appeal = new Appeal();
        appeal.setId(AppealId.of(entity.getId()));
        appeal.setAppealerId(AppealerId.of(entity.getAppealerId()));
        appeal.setReason(entity.getReason());
        appeal.setStatus(AppealStatus.valueOf(entity.getStatus()));
        appeal.setAttachments(entity.getAttachments() != null ? 
            List.of(entity.getAttachments().split(",")) : List.of());
        // 时间属性由基础设施层管理，不在领域层处理
        return appeal;
    }
    
    private AppealItem toItemDomain(AppealItemEntity entity) {
        AppealItem item = new AppealItem();
        item.setId(AppealItemId.of(entity.getId()));
        item.setAppealId(AppealId.of(entity.getAppealId()));
        item.setType(AppealType.valueOf(entity.getType()));
        item.setTitle(entity.getTitle());
        item.setStatus(AppealItemStatus.valueOf(entity.getStatus()));
        item.setContent(entity.getContent());
        // 时间属性由基础设施层管理，不在领域层处理
        return item;
    }
}