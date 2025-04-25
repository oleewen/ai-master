package com.cursor.master.order.service.factory;

import com.cursor.master.order.api.module.request.OrderBuyRequest;
import com.cursor.master.order.application.command.OrderBuyCommand;
import com.transformer.helper.BeanHelper;

public class OrderCommandFactory {
    public static OrderBuyCommand asCommand(OrderBuyRequest buyRequest) {
        OrderBuyCommand command = BeanHelper.copyProperties(new OrderBuyCommand(), buyRequest);
        return command;
    }
}
