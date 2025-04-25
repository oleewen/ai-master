package com.cursor.master.order.service.rpc;

import com.cursor.master.order.api.OrderService;
import com.cursor.master.order.api.module.request.OrderBuyRequest;
import com.cursor.master.order.api.module.response.OrderBuyResponse;
import com.cursor.master.order.application.command.OrderBuyCommand;
import com.cursor.master.order.application.result.OrderBuyResult;
import com.cursor.master.order.application.service.OrderApplicationService;
import com.cursor.master.order.service.factory.OrderCommandFactory;
import com.cursor.master.order.service.factory.OrderResultFactory;
import com.transformer.log.annotation.Call;

/**
 * 服务实现样例
 *
 * @author only
 * @since 2020-05-22
 */
public class OrderServiceProvider implements OrderService {
    private OrderApplicationService orderApplicationService;

    @Override
    @Call(elapsed = 1200, sample = 10000)
    public OrderBuyResponse buy(OrderBuyRequest buyRequest) {
        /** 处理参数验证错误 */
        if (buyRequest.validator()) {
            return OrderBuyResponse.empty();
        }

        /** 请求转命令 */
        OrderBuyCommand buyCommand = OrderCommandFactory.asCommand(buyRequest);

        /** 交易下单 */
        OrderBuyResult buyResult = orderApplicationService.doBuy(buyCommand);

        /** 输出转换 */
        return OrderResultFactory.asResponse(buyResult);
    }
}

