package com.only.ai.master.order.api.module.request;

import lombok.Data;
import com.transformer.request.Request;

@Data
public class OrderBuyRequest implements Request {
    /** 买家id */
    private Long buyerId;
    /** 商品id */
    private Long goodsId;
    /** 商品件数 */
    private Integer itemCount;

    @Override
    public boolean validator() {
        return buyerId == null || goodsId == null || itemCount == null || itemCount <= 0;
    }
}
