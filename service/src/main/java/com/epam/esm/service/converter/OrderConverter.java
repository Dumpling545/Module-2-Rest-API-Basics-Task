package com.epam.esm.service.converter;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

/**
 * Interface mapping order entities and DTOs. Used by MapStruct to generate actual mapper class
 *
 * @see <a href="https://mapstruct.org/">MapStruct library</a>
 */
@Mapper(componentModel = "spring")
public interface OrderConverter {
    @Mapping(target = "cost", source = "cost")
    Order convert(OrderDTO dto, BigDecimal cost);

    OrderDTO convert(Order order);
}
