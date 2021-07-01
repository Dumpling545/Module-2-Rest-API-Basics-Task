package com.epam.esm.web;

import com.epam.esm.model.dto.FilterDTO;
import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.GiftCertificateUpdateDTO;
import com.epam.esm.service.GiftCertificateService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.Validator;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/gift-certificates")
public class GiftCertificateController {
	private GiftCertificateService certService;

	public GiftCertificateController(GiftCertificateService certService) {
		this.certService = certService;
	}

	@GetMapping(value = "/{id}")
	public GiftCertificateOutputDTO getCertificate(@PathVariable("id") int id) {
		return certService.getCertificate(id);
	}

	@GetMapping
	public List<GiftCertificateOutputDTO> filteredCertificates(@RequestParam(required = false) String namePart,
	                                                           @RequestParam(required = false) String descriptionPart,
	                                                           @RequestParam(required = false) String tagName,
	                                                           @RequestParam(required = false) String sortOption) {
		FilterDTO filterDTO = new FilterDTO(namePart, descriptionPart, tagName, sortOption);
		return certService.getCertificates(filterDTO);
	}

	@PostMapping
	public ResponseEntity<Object> createCertificate(@RequestBody @Valid GiftCertificateCreateDTO certDTO,
	                                                UriComponentsBuilder ucb) {
		GiftCertificateOutputDTO dto = certService.createCertificate(certDTO);
		HttpHeaders headers = new HttpHeaders();
		URI locationUri = ucb.path("/gift-certificates/").path(String.valueOf(dto.getId())).build().toUri();
		headers.setLocation(locationUri);
		ResponseEntity<Object> entity = new ResponseEntity<Object>(headers, HttpStatus.CREATED);
		return entity;
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<Object> updateCertificate(@PathVariable("id") int id,
	                                                @RequestBody @Valid GiftCertificateUpdateDTO certDTO,
	                                                UriComponentsBuilder ucb) {
		certService.updateCertificate(id, certDTO);
		HttpHeaders headers = new HttpHeaders();
		URI locationUri = ucb.path("/gift-certificates/").path(String.valueOf(id)).build().toUri();
		headers.setLocation(locationUri);
		ResponseEntity<Object> entity = new ResponseEntity<Object>(headers, HttpStatus.NO_CONTENT);
		return entity;
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/{id}")
	public void deleteCertificate(@PathVariable("id") int id) {
		certService.deleteCertificate(id);
	}
}
