package com.epam.esm.service.converter;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel="spring")
public interface OrderConverter {
	@Mapping(target = "cost", source = "cost")
	Order convert(OrderDTO dto, BigDecimal cost);
	OrderDTO convert(Order order);
}
