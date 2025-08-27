package com.ai.master.order.domain.model;

import com.ai.master.common.domain.MonetaryAmount;
import com.ai.master.goods.domain.model.Goods;
import com.ai.master.goods.domain.model.GoodsId;
import com.ai.master.user.domain.model.BuyerId;
import com.ai.master.user.domain.model.SellerId;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单（聚合）
 *
 * @author only
 * @since 2020-05-22
 */
@Getter
public class Order {
    @Setter
    /** 订单id */
    private OrderId orderId;
    /** 商品id */
    private GoodsId goodsId;
    /** 买家id */
    private BuyerId buyerId;
    /** 卖家id */
    private SellerId sellerId;
    /** 购买件数 */
    private Integer itemCount;
    /** 订单金额 */
    private OrderAmount amount;
    /** 订单状态 */
    private OrderState state;

    public Order() {
        this.orderId = OrderId.create();
        this.state = new OrderState(OrderStatus.CREATED);
    }

    /**
     * 创建订单（工厂方法）
     * <pre>
     * 创建买家buyerId购买了count件商品goods的订单
     * 默认订单状态为已创建
     * </pre>
     *
     * @param buyerId   买家id
     * @param goods     商品
     * @param itemCount 商品件数
     *
     * @return 订单对象
     *
     * @see OrderAmount
     * @see OrderStatus
     */
    public static Order create(BuyerId buyerId, Goods goods, Integer itemCount) {
        Order order = new Order();
        order.goodsId = goods.getGoodsId();
        order.buyerId = buyerId;
        order.sellerId = goods.getSellerId();
        order.itemCount = itemCount;
        // 计算原价
        MonetaryAmount amount = goods.calculateAmount(itemCount);
        order.amount = OrderAmount.create(amount);
        return order;
    }

    public Long getId() {
        return orderId.id();
    }

    public OrderState getState() {
        return state;
    }

    /**
     * 执行订单动作（状态流转）
     * <pre>
     * 1. synchronized保证同一订单并发动作的互斥
     * 2. version用于乐观锁控制
     * 3. 返回流转后的状态，便于外部判断是否变更成功
     * </pre>
     *
     * @param action 订单动作
     * @return 流转后的订单状态
     */
    public synchronized OrderState doAction(OrderAction action) {
        this.state = this.state.onAction(action);
        return this.state;
    }

    public void enable() {
        this.state = this.state.onAction(OrderAction.SUBMIT);
    }
}
