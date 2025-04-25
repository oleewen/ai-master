package com.cursor.master.order.application.action;

import com.cursor.master.order.application.command.OrderBuyCommand;
import com.cursor.master.goods.domain.facade.ItemQueryFacade;
import com.cursor.master.goods.domain.model.Goods;
import com.cursor.master.order.domain.model.Order;
import com.cursor.master.order.domain.service.OrderDomainService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 订单创建
 *
 * @author only
 * @since 2020-05-22
 */
@Component
public class OrderCreateAction {
    /** 商品查询 */
    @Resource
    private ItemQueryFacade itemQueryFacade;
    /** 订单服务 */
    @Resource
    private OrderDomainService orderDomainService;

    public Order create(OrderBuyCommand buy) {
        /** 查询商品 */
        Goods goods = itemQueryFacade.requireGoods(buy.getGoodsId());

        /** 创建订单 */
        return orderDomainService.create(buy.getBuyerId(), goods, buy.getItemCount());
    }
}
