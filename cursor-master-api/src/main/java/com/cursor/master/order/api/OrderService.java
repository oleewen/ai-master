package com.cursor.master.order.api;

import io.swagger.annotations.Api;
import com.cursor.master.order.api.module.request.OrderBuyRequest;
import com.cursor.master.order.api.module.response.OrderBuyResponse;

@Api(value = "示例服务", description = "示例服务")
public interface OrderService {
    OrderBuyResponse buy(OrderBuyRequest buyRequest);
}
