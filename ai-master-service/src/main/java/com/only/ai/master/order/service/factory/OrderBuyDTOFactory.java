package com.only.ai.master.order.service.factory;

import com.only.ai.master.order.api.module.dto.OrderBuyDTO;
import com.only.ai.master.order.application.result.OrderBuyResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author only
 * @date 2022-05-27
 */
@Mapper
public interface OrderBuyDTOFactory {
    OrderBuyDTOFactory INSTANCE = Mappers.getMapper(OrderBuyDTOFactory.class);

    OrderBuyDTO toDTO(OrderBuyResult result);
}
