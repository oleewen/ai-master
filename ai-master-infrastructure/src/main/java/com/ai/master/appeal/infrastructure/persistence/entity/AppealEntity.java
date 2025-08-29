package com.ai.master.appeal.infrastructure.persistence.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.*;
import java.util.Date;

/**
 * 申诉单实体
 */
@Data
@Table(name = "appeal")
public class AppealEntity {
    
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    
    @Column(name = "appealer_id")
    private Long appealerId;
    
    private String reason;
    
    private String status;
    
    @Column(name = "attachments")
    private String attachments;
    
    @Column(name = "created_at")
    private Date createdAt;
    
    @Column(name = "updated_at")
    private Date updatedAt;
}