package com.ai.master.appeal.application.dto;

import com.ai.master.appeal.domain.enums.AppealStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 申诉进展DTO
 */
@Data
@Builder
public class AppealProgressResult {
    
    /** 申诉单ID */
    private Long appealId;
    
    /** 申诉状态 */
    private AppealStatus status;
    
    /** 进展描述 */
    private String progress;
    
    /** 申诉项列表 */
    private List<AppealItemProgress> items;
    
    @Data
    @Builder
    public static class AppealItemProgress {
        /** 申诉项ID */
        private Long itemId;
        
        /** 申诉项标题 */
        private String title;
        
        /** 申诉项状态 */
        private AppealStatus status;
    }
}