package com.ai.master.goods.infrastructure.dal;

import com.ai.master.goods.domain.model.Goods;
import com.ai.master.goods.domain.repository.GoodsRepository;
import com.ai.master.goods.infrastructure.call.GoodsCall;
import com.ai.master.goods.infrastructure.entity.GoodsEntity;
import com.ai.master.goods.infrastructure.factory.GoodsFactory;
import com.transformer.log.annotation.Call;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * 商品数据访问层
 *
 * @author only
 * @since 2020-05-22
 */
@Repository
public class GoodsDal implements GoodsRepository {
    /** 商品服务 */
    @Resource
    private GoodsCall goodsCall;

    @Override
    @Call
    public Goods acquireGoods(Long goodsId) {
        GoodsEntity goods = goodsCall.getGoodsById(goodsId);

        return GoodsFactory.valueOf(goods);
    }
}
