package com.epam.esm.service;

import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.GiftCertificateSearchFilterDTO;
import com.epam.esm.model.dto.GiftCertificateUpdateDTO;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * Interface of service for manipulating Service DTO objects
 */
@Validated
public interface GiftCertificateService {
    /**
     * Creates certificate, corresponding tags and relationship between them in database
     *
     * @param dto -- DTO of certificate to be created
     * @return {@GiftCertificateOutputDTO} object, that contains all input information plus id of tags associated with
     * newly created certificate
     */
    GiftCertificateOutputDTO createCertificate(GiftCertificateCreateDTO dto);

    /**
     * Retrieves certificate with associated tags from database
     *
     * @param id id of certificate to be retrieved
     * @return dto containing certificate with associated tags
     */
    GiftCertificateOutputDTO getCertificate(int id);

    /**
     * Updates certificate, corresponding tags and relationship between them in database
     *
     * @param id  id of certificate to update
     * @param dto DTO of certificate to be updated. Not null and not default fields will be merged with current state of
     *            certificate and then this merged object override existing certificate
     */
    void updateCertificate(int id, GiftCertificateUpdateDTO dto);

    /**
     * Deletes certificate with given id from database
     *
     * @param id id of certificate to be deleted
     */
    void deleteCertificate(int id);

    /**
     * Retrieves list of certificates that match given filter
     *
     * @param filter object that will be used to filter certificates. Null fields does not participate in filtering
     * @param page   paging info
     * @return list of filtered and paged certificate DTOs
     */
    PagedResultDTO<GiftCertificateOutputDTO> getCertificates(@Valid GiftCertificateSearchFilterDTO filter,
	                                                         @Valid PageDTO page);
}
