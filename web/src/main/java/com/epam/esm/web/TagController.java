package com.epam.esm.web;

import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.service.TagService;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {
	private TagService tagService;

	@Autowired
	public TagController(TagService tagService) {
		this.tagService = tagService;
	}

	@GetMapping("/{id}")
	public TagDTO getTag(@PathVariable("id") int id) {
		return tagService.getTag(id);
	}

	@GetMapping
	public List<TagDTO> allTags() {
		return tagService.getAllTags();
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public TagDTO createTag(@RequestBody TagDTO tagDTO) {
		tagService.createTag(tagDTO);
		return tagDTO;
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/{id}")
	public void deleteTag(@PathVariable("id") int id) {
		tagService.deleteTag(id);
	}
}
