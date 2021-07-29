package com.epam.esm.web;

import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.GiftCertificateSearchFilterDTO;
import com.epam.esm.model.dto.GiftCertificateUpdateDTO;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.web.assembler.ExtendedRepresentationModelAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Set;

import static com.epam.esm.model.dto.ValidationConstraints.MIN_PAGE_NUMBER;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_PAGE_SIZE;

/**
 * Controller handling requests to 'gift certificate' resource
 */
@RestController
@RequestMapping("/gift-certificates")
@RequiredArgsConstructor
public class GiftCertificateController {
    private final GiftCertificateService certService;
    private final ExtendedRepresentationModelAssembler<GiftCertificateOutputDTO, GiftCertificateController>
            assembler;

    @GetMapping(value = "/{id}")
    public ResponseEntity<EntityModel> getCertificate(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(assembler.toModel(certService.getCertificate(id)));
    }

	@GetMapping
	public ResponseEntity<CollectionModel> filteredCertificates(@RequestParam(required = false) String namePart,
	                                                            @RequestParam(required = false) String descriptionPart,
	                                                            @RequestParam(required = false) Set<String> tagNames,
	                                                            @RequestParam(required = false) String sortOption,
	                                                            @RequestParam(defaultValue = MIN_PAGE_NUMBER + "")
			                                                            Integer pageNumber,
	                                                            @RequestParam(defaultValue = MIN_PAGE_SIZE + "")
			                                                            Integer pageSize) {
		GiftCertificateSearchFilterDTO giftCertificateSearchFilterDTO = GiftCertificateSearchFilterDTO.builder()
				.namePart(namePart)
				.descriptionPart(descriptionPart)
				.tagNames(tagNames)
				.sortBy(sortOption).build();
		PageDTO pageDTO = PageDTO.builder()
				.pageNumber(pageNumber)
				.pageSize(pageSize)
				.build();
		PagedResultDTO<GiftCertificateOutputDTO> pagedResultDTO = certService.getCertificates(
				giftCertificateSearchFilterDTO, pageDTO);
		CollectionModel<EntityModel<GiftCertificateOutputDTO>> model =
				assembler.toPagedCollectionModel(pageNumber, pagedResultDTO,
				                                 (c, p) -> c.filteredCertificates(namePart, descriptionPart, tagNames,
				                                                                  sortOption, p, pageSize));
		return ResponseEntity.ok(model);
	}

	@PostMapping
	public ResponseEntity createCertificate(@RequestBody @Valid GiftCertificateCreateDTO certDTO,
	                                        UriComponentsBuilder ucb) {
		GiftCertificateOutputDTO dto = certService.createCertificate(certDTO);
		URI locationUri = ucb.path("/gift-certificates/").path(String.valueOf(dto.getId())).build().toUri();
		return ResponseEntity.created(locationUri).build();
	}

    @PutMapping(value = "/{id}")
    public ResponseEntity updateCertificate(@PathVariable("id") Integer id,
                                            @RequestBody @Valid GiftCertificateUpdateDTO certDTO,
                                            UriComponentsBuilder ucb) {
        certService.updateCertificate(id, certDTO);
        URI locationUri = ucb.path("/gift-certificates/").path(String.valueOf(id)).build().toUri();
        return ResponseEntity.noContent().location(locationUri).build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteCertificate(@PathVariable("id") Integer id) {
        certService.deleteCertificate(id);
        return ResponseEntity.noContent().build();
    }
}
