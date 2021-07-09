package com.epam.esm.web.assembler;

import com.epam.esm.model.dto.IdentifiableDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public abstract class ExtendedRepresentationModelAssembler<T extends IdentifiableDTO, C>
		extends RepresentationModelAssemblerSupport<T, EntityModel<T>> {
	public ExtendedRepresentationModelAssembler(Class<C> controllerClass, Class<T> entityType) {
		super(controllerClass, (Class<EntityModel<T>>) (Class<?>) entityType);
	}

	protected List<Link> rootAdditionalLinks() {
		return Collections.EMPTY_LIST;
	}

	protected List<Link> entityAdditionalLinks(T entity) {
		return Collections.EMPTY_LIST;
	}
	protected abstract EntityModel<T> createModel(T entity);

	@Override
	protected EntityModel<T> instantiateModel(T entity) {
		return EntityModel.of(entity);
	}

	@Override
	public EntityModel<T> toModel(T entity) {
		EntityModel<T> model = createModel(entity);
		model.add(entityAdditionalLinks(entity));
		model.add(rootAdditionalLinks());
		return model;
	}

	@Override
	public CollectionModel<EntityModel<T>> toCollectionModel(Iterable<? extends T> entities) {
		CollectionModel model = StreamSupport.stream(entities.spliterator(), false)
				.map(e -> createModelWithId(e.getId(), e).add(entityAdditionalLinks(e)))
				.collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
		model.add(rootAdditionalLinks());
		return model;
	}

	public CollectionModel<EntityModel<T>> toPagedCollectionModel(int pageNumber,
	                                                                  PagedResultDTO<T> pagedResultDTO,
	                                                                  BiFunction<C, Integer, ResponseEntity<CollectionModel>> pagedResultControllerHandler) {
		CollectionModel model = toCollectionModel(pagedResultDTO.getPage());
		model.addIf(!pagedResultDTO.isFirst(),
				() -> linkTo(pagedResultControllerHandler.apply((C) methodOn(getControllerClass()), 1))
						.withRel(IanaLinkRelations.FIRST))
				.addIf(!pagedResultDTO.isFirst(),
						() -> linkTo(pagedResultControllerHandler.apply((C) methodOn(getControllerClass()), pageNumber - 1))
								.withRel(IanaLinkRelations.PREV))
				.addIf(!pagedResultDTO.isLast(),
						() -> linkTo(pagedResultControllerHandler.apply((C) methodOn(getControllerClass()), pageNumber + 1))
								.withRel(IanaLinkRelations.NEXT));
		return model;
	}
}
