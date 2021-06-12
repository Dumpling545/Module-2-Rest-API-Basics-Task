package com.epam.esm.web;

import com.epam.esm.model.dto.*;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.service.GiftCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gift-certificates")
public class GiftCertificateController {
	private GiftCertificateService certService;

	@Autowired
	public GiftCertificateController(GiftCertificateService certService) {
		this.certService = certService;
	}

	@GetMapping(value = "/{id}")
	public GiftCertificateOutputDTO getCertificate(@PathVariable("id") int id) {
		return certService.getCertificate(id);
	}

	@GetMapping
	public List<GiftCertificateOutputDTO> filteredCertificates(
			@RequestParam(name = "namePart", required = false) String namePart,
			@RequestParam(name = "descriptionPart", required = false) String descriptionPart,
			@RequestParam(name = "tagName", required = false) String tagName,
			@RequestParam(name = "sortBy", required = false) Filter.SortOption sortOption) {
		FilterDTO filterDTO = new FilterDTO(namePart, descriptionPart, sortOption, tagName);
		return certService.getCertificates(filterDTO);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public GiftCertificateOutputDTO createCertificate(@RequestBody GiftCertificateDTO certDTO) {
		return certService.createCertificate(certDTO);
	}

	@PutMapping(value = "/{id}")
	public GiftCertificateOutputDTO updateCertificate(@PathVariable int id, @RequestBody GiftCertificateDTO certDTO) {
		certDTO.setId(id);
		return certService.updateCertificate(certDTO);
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/{id}")
	public void deleteCertificate(@PathVariable("id") int id) {
		certService.deleteCertificate(id);
	}

}
