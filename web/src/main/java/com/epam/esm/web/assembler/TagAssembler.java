package com.epam.esm.web.assembler;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.web.TagController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Non-abstract {@link ExtendedRepresentationModelAssembler} implementation
 * for {@link TagDTO}-based models creation
 */
@Component
public class TagAssembler extends ExtendedRepresentationModelAssembler<TagDTO> {

	private static final String MOST_WIDELY_USED_TAG_REL = "most-popular-tag-by-user";

	public TagAssembler(PagedResourcesAssembler<TagDTO> pagedResourcesAssembler) {
		super(pagedResourcesAssembler);
	}

	@Override
	public List<Link> rootAdditionalLinks() {
		return List.of(linkTo(methodOn(TagController.class).getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(null))
				               .withRel(MOST_WIDELY_USED_TAG_REL));
	}

	@Override
	public List<Link> singleModelLinks(TagDTO tagDTO) {
		return List.of(linkTo(methodOn(TagController.class).allTags(null)).withRel(IanaLinkRelations.COLLECTION));
	}

	@Override
	public List<Link> collectionItemModelLinks(TagDTO tagDTO) {
		return List.of(linkTo(methodOn(TagController.class).getTag(tagDTO.getId())).withSelfRel());
	}
}
