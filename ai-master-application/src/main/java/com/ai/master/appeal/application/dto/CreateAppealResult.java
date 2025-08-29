package com.ai.master.appeal.application.dto;

import com.ai.master.appeal.domain.enums.AppealStatus;
import lombok.Builder;
import lombok.Data;

/**
 * 创建申诉结果
 */
@Data
@Builder
public class CreateAppealResult {
    
    /** 申诉单ID */
    private Long appealId;
    
    /** 申诉状态 */
    private AppealStatus status;
}