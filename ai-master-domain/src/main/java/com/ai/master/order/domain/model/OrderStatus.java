package com.ai.master.order.domain.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

/**
 * 订单状态
 *
 * @author only
 * @since 2020-05-22
 */
@Getter
public enum OrderStatus {
    CREATED(0, "已创建"),
    ORDERED(1, "已下单"),
    PAID(2, "已支付"),
    DELIVERIED(3, "已发货"),
    RECEIVED(4, "已收货"),
    REFUNDED(5, "已退货"),
    FINISHED(6, "已完成"),
    CANCELLED(-1, "已取消");

    private final int status;
    private final String title;
    private final Map<OrderAction, OrderStatus> transmitMap = new HashMap<>();

    OrderStatus(int status, String title) {
        this.status = status;
        this.title = title;
    }

    public void addTransmit(OrderAction action, OrderStatus status) {
        transmitMap.put(action, status);
    }

    static {
        CREATED.addTransmit(OrderAction.SUBMIT, ORDERED);
        CREATED.addTransmit(OrderAction.CANCEL, CANCELLED);

        ORDERED.addTransmit(OrderAction.PAY, PAID);
        ORDERED.addTransmit(OrderAction.CANCEL, CANCELLED);

        PAID.addTransmit(OrderAction.DELIVERY, DELIVERIED);

        DELIVERIED.addTransmit(OrderAction.RECEIVE, RECEIVED);
        DELIVERIED.addTransmit(OrderAction.CANCEL, CANCELLED);

        RECEIVED.addTransmit(OrderAction.REFUND, REFUNDED);
        RECEIVED.addTransmit(OrderAction.FINISH, FINISHED);

        REFUNDED.addTransmit(OrderAction.FINISH, FINISHED);
        // FINISHED、CANCELLED无流转
    }

    public OrderStatus onAction(OrderAction action) {
        return onAction(action, new HashSet<>());
    }

    private OrderStatus onAction(OrderAction action, Set<OrderStatus> visited) {
        if (visited.contains(this)) {
            throw new IllegalStateException("Cycle detected in state transitions for status: " + this);
        }
        visited.add(this);
        OrderStatus next = transmitMap.get(action);
        if (next != null) {
            return next;
        }
        // 递归查找下级状态
        for (OrderStatus subStatus : transmitMap.values()) {
            if (!visited.contains(subStatus)) {
                try {
                    return subStatus.onAction(action, visited);
                } catch (IllegalArgumentException | IllegalStateException ignore) {
                    // 继续尝试下一个分支
                }
            }
        }
        throw new IllegalArgumentException("Action " + action + " is not allowed in status " + this + " or any sub-status");
    }
}
