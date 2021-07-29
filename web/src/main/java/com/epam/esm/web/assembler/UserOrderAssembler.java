package com.epam.esm.web.assembler;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.web.OrderController;
import com.epam.esm.web.UserController;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class UserOrderAssembler extends ExtendedRepresentationModelAssembler<OrderDTO, UserController> {
    private final ExtendedRepresentationModelAssembler<OrderDTO, OrderController> orderAssembler;

    public UserOrderAssembler(ExtendedRepresentationModelAssembler<OrderDTO, OrderController> orderAssembler) {
        super(UserController.class, OrderDTO.class);
        this.orderAssembler = orderAssembler;
    }

    @Override
    protected EntityModel<OrderDTO> createModel(OrderDTO entity) {
        return orderAssembler.createModel(entity);
    }
}
