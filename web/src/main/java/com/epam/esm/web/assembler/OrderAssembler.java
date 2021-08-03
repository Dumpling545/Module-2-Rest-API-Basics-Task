package com.epam.esm.web.assembler;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.web.OrderController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Non-abstract {@link ExtendedRepresentationModelAssembler} implementation
 * for {@link OrderDTO}-based models creation
 */
@Component
public class OrderAssembler extends ExtendedRepresentationModelAssembler<OrderDTO> {

	public OrderAssembler(PagedResourcesAssembler<OrderDTO> pagedResourcesAssembler) {
		super(pagedResourcesAssembler);
	}

	@Override
	public List<Link> singleModelLinks(OrderDTO orderDTO) {
		return List.of(linkTo(methodOn(OrderController.class).allOrders(null)).withRel(IanaLinkRelations.COLLECTION));
	}

	@Override
	public List<Link> collectionItemModelLinks(OrderDTO orderDTO) {
		return List.of(linkTo(methodOn(OrderController.class).getOrder(orderDTO.getId())).withSelfRel());
	}
}
