package com.ai.master.appeal.infrastructure.persistence.repository;

import com.ai.master.appeal.domain.entity.Appealer;
import com.ai.master.appeal.domain.enums.AppealerType;
import com.ai.master.appeal.domain.repository.AppealerRepository;
import com.ai.master.appeal.domain.valueobject.AppealerId;
import com.ai.master.appeal.infrastructure.persistence.entity.AppealerEntity;
import com.ai.master.appeal.infrastructure.persistence.mapper.AppealerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 申诉方仓储实现
 */
@Repository
@RequiredArgsConstructor
public class AppealerRepositoryImpl implements AppealerRepository {
    
    private final AppealerMapper appealerMapper;
    
    @Override
    public void save(Appealer appealer) {
        AppealerEntity entity = toEntity(appealer);
        if (appealerMapper.selectByPrimaryKey(entity.getId()) != null) {
            appealerMapper.updateByPrimaryKeySelective(entity);
        } else {
            appealerMapper.insertSelective(entity);
        }
    }
    
    @Override
    public Optional<Appealer> findById(AppealerId id) {
        AppealerEntity entity = appealerMapper.selectByPrimaryKey(id.getValue());
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }
    
    @Override
    public Optional<Appealer> findByCode(String code) {
        Example example = new Example(AppealerEntity.class);
        example.createCriteria().andEqualTo("code", code);
        AppealerEntity entity = appealerMapper.selectOneByExample(example);
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }
    
    @Override
    public List<Appealer> findByType(AppealerType type) {
        Example example = new Example(AppealerEntity.class);
        example.createCriteria().andEqualTo("type", type.name());
        List<AppealerEntity> entities = appealerMapper.selectByExample(example);
        
        return entities.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Appealer> findAll() {
        List<AppealerEntity> entities = appealerMapper.selectAll();
        return entities.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean exists(AppealerId id) {
        return appealerMapper.selectByPrimaryKey(id.getValue()) != null;
    }
    
    @Override
    public boolean existsByCode(String code) {
        Example example = new Example(AppealerEntity.class);
        example.createCriteria().andEqualTo("code", code);
        return appealerMapper.selectCountByExample(example) > 0;
    }
    
    private AppealerEntity toEntity(Appealer appealer) {
        AppealerEntity entity = new AppealerEntity();
        entity.setId(appealer.getId().getValue());
        entity.setCode(appealer.getCode());
        entity.setType(appealer.getType().name());
        entity.setCreatedAt(appealer.getCreatedAt());
        entity.setUpdatedAt(appealer.getUpdatedAt());
        return entity;
    }
    
    private Appealer toDomain(AppealerEntity entity) {
        Appealer appealer = new Appealer();
        appealer.setId(AppealerId.of(entity.getId()));
        appealer.setCode(entity.getCode());
        appealer.setType(AppealerType.valueOf(entity.getType()));
        appealer.setCreatedAt(entity.getCreatedAt());
        appealer.setUpdatedAt(entity.getUpdatedAt());
        return appealer;
    }
}