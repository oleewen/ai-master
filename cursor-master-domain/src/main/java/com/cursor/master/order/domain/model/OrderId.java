package com.cursor.master.order.domain.model;

import java.util.UUID;

import com.cursor.master.common.domain.Id;

/**
 * 订单id（值对象）
 *
 * @author only
 * @since 2020-05-22
 */
public class OrderId extends Id {
    private OrderId(Long id) {
        super(id);
    }

    public static OrderId create(Long id) {
        return new OrderId(id);
    }

    public static OrderId create() {
        return create(UUID.randomUUID().getLeastSignificantBits());
    }

    public Long getId() {
        return super.id();
    }
}
