package com.epam.esm.web;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.service.TagService;
import com.epam.esm.web.assembler.ExtendedRepresentationModelAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

import static com.epam.esm.web.ResourcePaths.TAGS;

/**
 * Controller handling requests to 'tag' resource
 */
@RestController
@RequestMapping(TAGS)
@RequiredArgsConstructor
public class TagController {
	private final TagService tagService;
	private final ExtendedRepresentationModelAssembler<TagDTO> assembler;

	@GetMapping("/{id}")
	public ResponseEntity<EntityModel> getTag(@PathVariable("id") Integer id) {
		return ResponseEntity.ok(assembler.toModel(tagService.getTag(id)));
	}

	@GetMapping("/most-widely-used-with-highest-total-cost")
	public ResponseEntity<EntityModel> getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(
			@RequestParam Integer userId) {
		return ResponseEntity.ok(
				assembler.toModel(tagService.getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(userId)));
	}

	@GetMapping
	public ResponseEntity<CollectionModel> allTags(Pageable pageable) {
		Slice<TagDTO> slice = tagService.getAllTags(pageable);
		CollectionModel<EntityModel<TagDTO>> model = assembler.toSliceModel(slice);
		return ResponseEntity.ok(model);
	}

	@PostMapping
	public ResponseEntity createTag(@RequestBody @Valid TagDTO tagDTO, UriComponentsBuilder ucb) {
		TagDTO dto = tagService.createTag(tagDTO);
		URI locationUri = ucb.path(TAGS + "/").path(String.valueOf(dto.getId())).build().toUri();
		return ResponseEntity.created(locationUri).build();
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity deleteTag(@PathVariable("id") Integer id) {
		tagService.deleteTag(id);
		return ResponseEntity.noContent().build();
	}
}
