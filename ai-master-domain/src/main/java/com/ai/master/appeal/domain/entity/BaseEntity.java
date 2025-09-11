package com.ai.master.appeal.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类
 */
@Data
public abstract class BaseEntity implements Serializable {
    
    /** 创建时间 */
    private Date createdAt;
    
    /** 更新时间 */
    private Date updatedAt;
}