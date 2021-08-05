package com.epam.esm.service.converter;

import com.epam.esm.model.dto.GiftCertificateSearchFilterDTO;
import com.epam.esm.model.entity.GiftCertificateSearchFilter;
import org.mapstruct.Mapper;

/**
 * Interface mapping certificate filter entities and DTOs. Used by MapStruct to generate actual mapper class
 *
 * @see <a href="https://mapstruct.org/">MapStruct library</a>
 */
@Mapper(componentModel = "spring")
public interface GiftCertificateSearchFilterConverter {
	GiftCertificateSearchFilter convert(GiftCertificateSearchFilterDTO dto);
}
