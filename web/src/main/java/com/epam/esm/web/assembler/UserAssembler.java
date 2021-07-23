package com.epam.esm.web.assembler;

import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.web.UserController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserAssembler extends ExtendedRepresentationModelAssembler<UserDTO, UserController> {

	private static final String ORDERS = "orders";

	public UserAssembler() {
		super(UserController.class, UserDTO.class);
	}

	@Override
	protected List<Link> entityAdditionalLinks(UserDTO userDTO) {
		return List.of(linkTo(methodOn(UserController.class).getOrdersByUser(userDTO.getId(), null, null))
				.withRel(ORDERS));
	}

	@Override
	public EntityModel<UserDTO> createModel(UserDTO entity) {
		EntityModel<UserDTO> model = instantiateModel(entity);
		model.add(linkTo(methodOn(UserController.class).allUsers(null, null)).withRel(IanaLinkRelations.COLLECTION));
		return model;
	}
}