package com.epam.esm.db;

import com.epam.esm.db.fragment.FilteredGiftCertificateRepository;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.GiftCertificateSearchFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * Interface for managing GiftCertificate objects in database
 */
public interface GiftCertificateRepository extends Repository<GiftCertificate, Integer>,
                                                   FilteredGiftCertificateRepository {
	/**
	 * Creates new certificate in database or certificate with given {@link GiftCertificate#getId()} value (if exists)
	 *
	 * @param certificate object containing sources for new certificate, id and date properties are ignored.
	 * @return created certificate
	 */
	GiftCertificate save(GiftCertificate certificate);

	/**
	 * Retrieves certificate with given id
	 *
	 * @param id of certificate to be retrieved
	 * @return {@link Optional} of certificate containing corresponding certificate object, if * certificate with such
	 * id exists in database; empty {@link Optional} otherwise
	 */
	@EntityGraph(value = "full-certificate-entity-graph", type = EntityGraphType.LOAD)
	Optional<GiftCertificate> findById(Integer id);

	/**
	 * Returns whether certificate with the given id exists.
	 *
	 * @param integer must not be {@literal null}.
	 * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
	boolean existsById(Integer integer);

	/**
	 * Retrieves all certificates filtered by provided filter object
	 *
	 * @param giftCertificateSearchFilter filter object used for filtering
	 * @param pageable                    paging info
	 * @return filtered and paged list of certificates
	 */
	Slice<GiftCertificate> getCertificatesByFilter(GiftCertificateSearchFilter giftCertificateSearchFilter,
	                                               Pageable pageable);


	/**
	 * Deletes certificate with given id from database
	 *
	 * @param id id of certificate to be deleted
	 * @return true if certificate with given id successfully deleted; false if certificate with such id does not exist
	 * in database by the time of method invocation
	 */
	void deleteById(Integer id);
}
