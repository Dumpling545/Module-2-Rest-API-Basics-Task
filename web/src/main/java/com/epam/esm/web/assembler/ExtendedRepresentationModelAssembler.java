package com.epam.esm.web.assembler;

import com.epam.esm.model.dto.IdentifiableDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Spring's RepresentationModelAssemblerSupport extended with some useful methods aimed to reduce boilerplate code while
 * constructing models enriched with HATEOAS links
 */
@RequiredArgsConstructor
public abstract class ExtendedRepresentationModelAssembler<T extends IdentifiableDTO>
		implements RepresentationModelAssembler<T, EntityModel<T>> {

	private final PagedResourcesAssembler<T> pagedResourcesAssembler;

	/**
	 * Links to add at the root of any parent entity (both single or collection of entities)
	 *
	 * @return List of links
	 */
	public List<Link> rootAdditionalLinks() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * Links to add at the root of EntityModel created by {@link this#toModel(IdentifiableDTO)}
	 *
	 * @return List of links
	 */
	public List<Link> singleModelLinks(T entity) {
		return Collections.EMPTY_LIST;
	}

	/**
	 * Links to add at the root of EntityModels created by {@link this#toSliceModel(Slice)} (IdentifiableDTO)}
	 *
	 * @return List of links
	 */
	public List<Link> collectionItemModelLinks(T entity) {
		return Collections.EMPTY_LIST;
	}

	@Override
	public EntityModel<T> toModel(T entity) {
		EntityModel<T> model = EntityModel.of(entity);
		model.add(singleModelLinks(entity));
		model.add(rootAdditionalLinks());
		return model;
	}

	public CollectionModel<EntityModel<T>> toSliceModel(Slice<T> slice) {
		Page<T> page = new PageImpl<T>(slice.getContent(), slice.getPageable(), Integer.MAX_VALUE);
		var model = pagedResourcesAssembler.toModel(page);
		Link first = model.getRequiredLink(IanaLinkRelations.FIRST);
		Optional<Link> prev = model.getLink(IanaLinkRelations.PREV);
		Optional<Link> next = model.getLink(IanaLinkRelations.NEXT);
		List<Link> links = new ArrayList<>();
		if (slice.hasPrevious()) {
			links.addAll(List.of(first, prev.get()));
		}
		if (slice.hasNext()) {
			links.add(next.get());
		}
		List<EntityModel<T>> items = slice.getContent().stream()
				.map(e -> EntityModel.of(e, collectionItemModelLinks(e))).toList();
		return CollectionModel.of(items).add(links).add(rootAdditionalLinks());
	}
}
