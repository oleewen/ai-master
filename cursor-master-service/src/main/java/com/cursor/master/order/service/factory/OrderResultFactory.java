package com.cursor.master.order.service.factory;

import com.cursor.master.order.api.module.dto.OrderBuyDTO;
import com.cursor.master.order.api.module.response.OrderBuyResponse;
import com.cursor.master.order.application.result.OrderBuyResult;

public class OrderResultFactory {
    public static OrderBuyResponse asResponse(OrderBuyResult buyResult) {
        OrderBuyResponse response = OrderBuyResponse.success();
        OrderBuyDTO buyDTO = OrderBuyDTOFactory.INSTANCE.toDTO(buyResult);
        response.setModule(buyDTO);
        return response;
    }
}
