package com.ai.master.appeal.infrastructure.persistence.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.*;
import java.util.Date;

/**
 * 申诉方实体
 */
@Data
@Table(name = "appealer")
public class AppealerEntity {
    
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    
    private String code;
    
    private String type;
    
    @Column(name = "created_at")
    private Date createdAt;
    
    @Column(name = "updated_at")
    private Date updatedAt;
}