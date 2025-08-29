package com.ai.master.appeal.api.module.request;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 申诉创建请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppealCreateRequest {
    
    /**
     * 申诉方ID
     */
    @NotNull(message = "申诉方ID不能为空")
    private Long appealerId;
    
    /**
     * 申诉理由
     */
    @NotBlank(message = "申诉理由不能为空")
    private String reason;
    
    /**
     * 附件列表
     */
    private List<String> attachments;
    
    /**
     * 申诉项列表
     */
    @Valid
    @NotEmpty(message = "申诉项不能为空")
    private List<AppealItemRequest> appealItems;
    
    /**
     * 申诉项请求
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppealItemRequest {
        
        /**
         * 申诉项编码
         */
        @NotBlank(message = "申诉项编码不能为空")
        private String appealCode;
        
        /**
         * 申诉类型
         */
        @NotBlank(message = "申诉类型不能为空")
        private String appealType;
        
        /**
         * 申诉内容
         */
        @NotNull(message = "申诉内容不能为空")
        private Object content;
    }
}