package com.ai.master.appeal.infrastructure.persistence.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.*;
import java.util.Date;

/**
 * 申诉项实体
 */
@Data
@Table(name = "appeal_item")
public class AppealItemEntity {
    
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    
    @Column(name = "appeal_id")
    private Long appealId;
    
    private String type;
    
    private String title;
    
    private String status;
    
    private String content;
    
    @Column(name = "created_at")
    private Date createdAt;
    
    @Column(name = "updated_at")
    private Date updatedAt;
}