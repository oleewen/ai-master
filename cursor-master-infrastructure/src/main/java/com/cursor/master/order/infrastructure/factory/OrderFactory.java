package com.cursor.master.order.infrastructure.factory;

import com.cursor.master.order.domain.model.Order;
import com.cursor.master.order.infrastructure.entity.OrderEntity;

/**
 * 订单工厂
 *
 * @author only
 * @since 2020-05-22
 */
public class OrderFactory {
    public static OrderEntity instance(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getOrderId().id());
        entity.setGoodsId(order.getGoodsId().id());
        entity.setBuyerId(order.getBuyerId().id());
        entity.setSellerId(order.getSellerId().id());
        entity.setAmount(order.getAmount().getCent());
        entity.setStatus(order.getState().status());

        return entity;
    }


}
