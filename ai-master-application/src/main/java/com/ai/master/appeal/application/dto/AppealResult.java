package com.ai.master.appeal.application.dto;

import com.ai.master.appeal.domain.enums.AppealStatus;
import com.ai.master.appeal.domain.enums.AppealType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 申诉结果DTO
 */
@Data
@Builder
public class AppealResult {
    
    /** 申诉单ID */
    private Long appealId;
    
    /** 申诉理由 */
    private String reason;
    
    /** 申诉状态 */
    private AppealStatus status;
    
    /** 申诉项列表 */
    private List<AppealItemResult> items;
    
    @Data
    @Builder
    public static class AppealItemResult {
        /** 申诉项ID */
        private Long itemId;
        
        /** 申诉类型 */
        private AppealType type;
        
        /** 申诉项标题 */
        private String title;
        
        /** 申诉内容 */
        private String content;
        
        /** 申诉项状态 */
        private AppealStatus status;
    }
}