package com.only.ai.master.order.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.only.ai.master.order.api.module.request.OrderBuyRequest;
import com.only.ai.master.order.api.module.response.OrderBuyResponse;

@Api(value = "示例服务", description = "示例服务")
public interface OrderService {
    OrderBuyResponse buy(OrderBuyRequest buyRequest);
}
