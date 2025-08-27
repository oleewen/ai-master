package com.ai.master.order.application.service;

import com.ai.master.order.application.action.InventoryLockAction;
import com.ai.master.order.application.action.OrderCreateAction;
import com.ai.master.order.application.action.OrderEnableAction;
import com.ai.master.order.application.command.OrderBuyCommand;
import com.ai.master.order.application.factory.OrderBuyResultFactory;
import com.ai.master.order.application.result.OrderBuyResult;
import com.ai.master.order.domain.model.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 交易流程
 *
 * @author only
 * @date 2020-05-22
 */
@Component
public class OrderApplicationService {
    /** 库存锁定 */
    @Resource
    private InventoryLockAction inventoryLockAction;
    /** 订单创建 */
    @Resource
    private OrderCreateAction orderCreateAction;
    /** 订单生效 */
    @Resource
    private OrderEnableAction orderEnableAction;

    /**
     * 交易创建流程
     *
     * @param buy 交易请求
     *
     * @return 交易创建结果
     */
    public OrderBuyResult doBuy(OrderBuyCommand buy) {
        /** 创建交易订单 */
        Order order = orderCreateAction.create(buy);

        /** 锁定商品库存 */
        boolean locked = inventoryLockAction.lock(order);

        /** 设置订单可见 */
        if (locked) {
            orderEnableAction.enable(order);
        }

        // 结果封装
        return OrderBuyResultFactory.INSTANCE.toResult(order);
    }
}
