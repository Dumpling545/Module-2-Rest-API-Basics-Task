package com.epam.esm.service;

import com.epam.esm.model.dto.FilterDTO;
import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.GiftCertificateUpdateDTO;
import com.epam.esm.service.exception.ServiceException;

import java.util.List;

/**
 * Interface of service for manipulating Service DTO objects
 */
public interface GiftCertificateService {
	/**
	 * Creates certificate, corresponding tags and relationship between them in database
	 *
	 * @param dto
	 * 		-- DTO of certificate to be created
	 * @return {@GiftCertificateOutputDTO} object, that contains all input information plus id of tags associated with
	 * newly created certificate
	 */
	GiftCertificateOutputDTO createCertificate(GiftCertificateCreateDTO dto);

	/**
	 * Retrieves certificate with associated tags from database
	 *
	 * @param id
	 * 		id of certificate to be retrieved
	 * @return dto containing certificate with associated tags
	 */
	GiftCertificateOutputDTO getCertificate(int id);

	/**
	 * Updates certificate, corresponding tags and relationship between them in database
	 *
	 * @param dto
	 * 		-- DTO of certificate to be updated. Not null and not default fields will be merged with current state of
	 * 		certificate and then this merged object override existing certificate
	 * @return {@GiftCertificateOutputDTO} object, that contains all input information plus id of tags associated with
	 * updatecertificate
	 */
	GiftCertificateOutputDTO updateCertificate(GiftCertificateUpdateDTO dto);

	/**
	 * Deletes certificate with given id from database
	 *
	 * @param id
	 * 		id of certificate to be deleted
	 */
	void deleteCertificate(int id);

	/**
	 * Retrieves list of certificates that match given filter
	 *
	 * @param filter
	 * 		object that willbe used to filter certificates. Null fields does not participate in filtering
	 * @return list of filtered certificate DTOs
	 */
	List<GiftCertificateOutputDTO> getCertificates(FilterDTO filter);
}
