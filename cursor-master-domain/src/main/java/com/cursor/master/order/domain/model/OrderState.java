package com.cursor.master.order.domain.model;

import lombok.Getter;

@Getter
public class OrderState {
    private final OrderStatus status;

    public OrderState(OrderStatus status) {
        this.status = status;
    }

    public OrderState onAction(OrderAction action) {
        OrderStatus next = status.onAction(action);
        return new OrderState(next);
    }

    public int status() {
        return status.getStatus();
    }
} 