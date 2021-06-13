package com.epam.esm.db;

import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.GiftCertificate;

import java.util.List;
import java.util.Optional;

/**
 * Interface for managing GiftCertificate objects in database
 */
public interface GiftCertificateRepository {
	/**
	 * Creates new certificate in database. Properties {@link GiftCertificate#getId()}, {@link
	 * GiftCertificate#getCreateDate()} and {@link GiftCertificate#getLastUpdateDate()} will be changed during
	 * execution
	 *
	 * @param certificate
	 * 		object containing sources for new certificate, id field is ignored
	 */
	void createCertificate(GiftCertificate certificate);

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
	 * Retrieves all certificates filtered by provided filter object
	 *
	 * @param filter
	 * 		filter object used for filtering
	 * @return filtered list of certificates
	 */
	List<GiftCertificate> getCertificatesByFilter(Filter filter);

	/**
	 * Updates certificate with given {@link GiftCertificate#getId()} value
	 *
	 * @param certificate
	 * 		object that used as filter through {@link GiftCertificate#getId()} property and as update source through other
	 * 		properties (dates are ignored). Property {@link GiftCertificate#getLastUpdateDate()} will be changed during
	 * 		execution
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

	/**
	 * Adds provided tag to provided certificate
	 *
	 * @param certificateId
	 * 		id of provided certificate
	 * @param tagId
	 * 		id of provided tag
	 */
	void addTag(int certificateId, int tagId);

	/**
	 * Removes provided tag from provided certificate
	 *
	 * @param certificateId
	 * 		id of provided certificate
	 * @param tagId
	 * 		id of provided tag
	 */
	void removeTag(int certificateId, int tagId);
}
