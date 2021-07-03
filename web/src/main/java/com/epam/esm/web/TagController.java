package com.epam.esm.web;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.service.TagService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {
	private TagService tagService;

	public TagController(TagService tagService) {
		this.tagService = tagService;
	}

	@GetMapping("/{id}")
	public TagDTO getTag(@PathVariable("id") int id) {
		return tagService.getTag(id);
	}

	@GetMapping("/most-widely-used-with-highest-total-cost")
	public TagDTO getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(@RequestParam int userId) {
		return tagService.getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(userId);
	}

	@GetMapping
	public List<TagDTO> allTags() {
		return tagService.getAllTags();
	}

	@PostMapping
	public ResponseEntity<Object> createTag(@RequestBody @Valid TagDTO tagDTO, UriComponentsBuilder ucb) {
		TagDTO dto = tagService.createTag(tagDTO);
		HttpHeaders headers = new HttpHeaders();
		URI locationUri = ucb.path("/tags/").path(String.valueOf(dto.getId())).build().toUri();
		headers.setLocation(locationUri);
		ResponseEntity<Object> entity = new ResponseEntity<Object>(headers, HttpStatus.CREATED);
		return entity;
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/{id}")
	public void deleteTag(@PathVariable("id") int id) {
		tagService.deleteTag(id);
	}
}
