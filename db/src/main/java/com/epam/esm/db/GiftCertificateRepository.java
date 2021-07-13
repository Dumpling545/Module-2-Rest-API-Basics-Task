package com.epam.esm.db;

import com.epam.esm.model.entity.GiftCertificateSearchFilter;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.PagedResult;

import java.util.Optional;

/**
 * Interface for managing GiftCertificate objects in database
 */
public interface GiftCertificateRepository {
	/**
	 * Creates new certificate in database
	 *
	 * @param certificate
	 * 		object containing sources for new certificate, id and date properties are ignored.
	 * @return created certificate
	 */
	GiftCertificate createCertificate(GiftCertificate certificate);

	/**
	 * Retrieves certificate with given id
	 *
	 * @param id
	 * 		of certificate to be retrieved
	 * @return {@link Optional} of certificate containing corresponding certificate object, if * certificate with such
	 * id exists in database; empty {@link Optional} otherwise
	 */
	Optional<GiftCertificate> getCertificateById(int id);

	/**
	 * 	Retrieves all certificates filtered by provided filter object
	 * @param giftCertificateSearchFilter
	 *      filter object used for filtering
	 * @param offset how many elements to skip
	 * @param limit how many elements to retrieve
	 * @return filtered and paged list of certificates
	 */
	PagedResult<GiftCertificate> getCertificatesByFilter(GiftCertificateSearchFilter giftCertificateSearchFilter, int offset, int limit);

	/**
	 * Updates certificate with given {@link GiftCertificate#getId()} value
	 *
	 * @param certificate
	 * 		object that used as filter through {@link GiftCertificate#getId()} property and as update source through other
	 * 		properties (dates are ignored).
	 */
	void updateCertificate(GiftCertificate certificate);

	/**
	 * Deletes certificate with given id from database
	 *
	 * @param id
	 * 		id of certificate to be deleted
	 * @return true if certificate with given id successfully deleted; false if certificate with such id does not exist
	 * in database by the time of method invocation
	 */
	boolean deleteCertificate(int id);
}
