package com.epam.esm.web.assembler;

import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.web.GiftCertificateController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GiftCertificateAssembler extends
                                      ExtendedRepresentationModelAssembler<GiftCertificateOutputDTO> {

	public GiftCertificateAssembler(
			PagedResourcesAssembler<GiftCertificateOutputDTO> pagedResourcesAssembler) {
		super(pagedResourcesAssembler);
	}

	@Override
	public List<Link> singleModelLinks(GiftCertificateOutputDTO giftCertificateOutputDTO) {
		return List.of(
				linkTo(methodOn(GiftCertificateController.class).filteredCertificates(null, null, null, null)).withRel(
						IanaLinkRelations.COLLECTION));
	}

	@Override
	public List<Link> collectionItemModelLinks(GiftCertificateOutputDTO giftCertificateOutputDTO) {
		return List.of(linkTo(methodOn(GiftCertificateController.class).getCertificate(
				giftCertificateOutputDTO.getId())).withSelfRel());
	}
}
