package com.ai.master.appeal.api.module.request;

import com.ai.master.appeal.api.module.dto.AppealItemDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 发起申诉请求对象
 */
@Data
public class AppealCreateRequest {
    @NotBlank(message = "申诉原因不能为空")
    private String reason;

    @NotNull(message = "申诉项不能为空")
    private AppealItemDTO appealItem;
} 