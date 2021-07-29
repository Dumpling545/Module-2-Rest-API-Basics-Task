package com.epam.esm.web.assembler;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.web.OrderController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderAssembler extends ExtendedRepresentationModelAssembler<OrderDTO, OrderController> {

    public OrderAssembler() {
        super(OrderController.class, OrderDTO.class);
    }

    @Override
    public EntityModel<OrderDTO> createModel(OrderDTO entity) {
        return instantiateModel(entity).add(linkTo(methodOn(OrderController.class)
                .allOrders(null, null))
                .withRel(IanaLinkRelations.COLLECTION));
    }
}
