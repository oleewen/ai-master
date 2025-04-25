package com.cursor.master.goods.domain.repository;

import com.cursor.master.goods.domain.model.Goods;

/**
 * 商品资源库
 *
 * @author only
 * @since 2020-05-22
 */
public interface GoodsRepository {
    Goods acquireGoods(Long goodsId);
}
