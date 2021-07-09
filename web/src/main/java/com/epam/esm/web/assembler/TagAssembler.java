package com.epam.esm.web.assembler;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.web.TagController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagAssembler extends ExtendedRepresentationModelAssembler<TagDTO, TagController> {

	private static final String MOST_WIDELY_USED_TAG_REL = "most-popular-tag-by-user";

	public TagAssembler() {
		super(TagController.class, TagDTO.class);
	}

	@Override
	protected List<Link> rootAdditionalLinks() {
		return List.of(linkTo(methodOn(TagController.class).getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(null))
						.withRel(MOST_WIDELY_USED_TAG_REL));
	}

	@Override
	public EntityModel<TagDTO> createModel(TagDTO entity) {
		return instantiateModel(entity)
				.add(linkTo(methodOn(TagController.class).allTags(null, null)).withRel(IanaLinkRelations.COLLECTION));
	}
}
