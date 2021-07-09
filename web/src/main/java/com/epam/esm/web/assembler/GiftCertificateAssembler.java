package com.epam.esm.web.assembler;

import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.web.GiftCertificateController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GiftCertificateAssembler extends
                                      ExtendedRepresentationModelAssembler<GiftCertificateOutputDTO, GiftCertificateController> {

	public GiftCertificateAssembler() {
		super(GiftCertificateController.class, GiftCertificateOutputDTO.class);
	}

	@Override
	public EntityModel<GiftCertificateOutputDTO> createModel(GiftCertificateOutputDTO entity) {
		return instantiateModel(entity).add(linkTo(methodOn(GiftCertificateController.class)
				.filteredCertificates(null, null, null, null, null, null))
				.withRel(IanaLinkRelations.COLLECTION));
	}
}
