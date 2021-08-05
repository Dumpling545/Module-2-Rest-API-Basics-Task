package com.epam.esm.web.assembler;

import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.web.UserController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Non-abstract {@link ExtendedRepresentationModelAssembler} implementation
 * for {@link UserDTO}-based models creation
 */
@Component
public class UserAssembler extends ExtendedRepresentationModelAssembler<UserDTO> {

	private static final String ORDERS = "orders";

	public UserAssembler(PagedResourcesAssembler<UserDTO> pagedResourcesAssembler) {
		super(pagedResourcesAssembler);
	}

	@Override
	public List<Link> rootAdditionalLinks() {
		return List.of(linkTo(methodOn(UserController.class).getOrdersByUser(null, null))
				               .withRel(ORDERS));
	}

	@Override
	public List<Link> singleModelLinks(UserDTO userDTO) {
		return List.of(linkTo(methodOn(UserController.class).allUsers(null)).withRel(IanaLinkRelations.COLLECTION));
	}

	@Override
	public List<Link> collectionItemModelLinks(UserDTO userDTO) {
		return List.of(linkTo(methodOn(UserController.class).getUser(userDTO.getId())).withSelfRel());
	}
}