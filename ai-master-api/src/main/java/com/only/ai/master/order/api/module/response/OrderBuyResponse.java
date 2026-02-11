package com.only.ai.master.order.api.module.response;

import com.only.ai.master.order.api.module.dto.OrderBuyDTO;
import com.transformer.response.Result;
import lombok.Data;

@Data
public class OrderBuyResponse extends Result<OrderBuyDTO> {
    private static final OrderBuyResponse DEFAULT_RESPONSE = new OrderBuyResponse();

    public static OrderBuyResponse empty() {
        return DEFAULT_RESPONSE;
    }

    public static OrderBuyResponse success() {
        return new OrderBuyResponse();
    }

    public static OrderBuyResponse success(OrderBuyDTO module) {
        OrderBuyResponse response = new OrderBuyResponse();
        response.setModule(module);
        response.setSuccess(true);
        return response;
    }

    public static OrderBuyResponse fail(String code, String message) {
        OrderBuyResponse response = new OrderBuyResponse();
        response.setSuccess(false);
        response.setStatusCode(code);
        response.setMessage(message);
        return response;
    }
}
