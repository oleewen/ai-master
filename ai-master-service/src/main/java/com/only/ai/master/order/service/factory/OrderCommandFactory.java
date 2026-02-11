package com.only.ai.master.order.service.factory;

import com.only.ai.master.order.api.module.request.OrderBuyRequest;
import com.only.ai.master.order.application.command.OrderBuyCommand;
import com.transformer.helper.BeanHelper;

public class OrderCommandFactory {
    public static OrderBuyCommand asCommand(OrderBuyRequest buyRequest) {
        OrderBuyCommand command = BeanHelper.copyProperties(new OrderBuyCommand(), buyRequest);
        return command;
    }
}
