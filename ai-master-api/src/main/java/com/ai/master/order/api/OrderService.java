package com.ai.master.order.api;

import io.swagger.annotations.Api;
import com.ai.master.order.api.module.request.OrderBuyRequest;
import com.ai.master.order.api.module.response.OrderBuyResponse;

@Api(value = "示例服务", description = "示例服务")
public interface OrderService {
    OrderBuyResponse buy(OrderBuyRequest buyRequest);
}
