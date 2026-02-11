package com.only.ai.master.order.api;

import io.swagger.annotations.Api;
import com.only.ai.master.order.api.module.request.OrderBuyRequest;
import com.only.ai.master.order.api.module.response.OrderBuyResponse;

@Api(tags = "示例服务")
public interface OrderService {
    OrderBuyResponse buy(OrderBuyRequest buyRequest);
}
