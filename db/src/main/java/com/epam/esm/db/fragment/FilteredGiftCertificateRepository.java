package com.epam.esm.db.fragment;

import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.GiftCertificateSearchFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

/**
 * Fragment interface for Spring Data based {@link com.epam.esm.db.GiftCertificateRepository}
 * interface. Required since internal implementation of
 * {@link #getCertificatesByFilter(GiftCertificateSearchFilter, Pageable)} is too advanced
 * for Spring Data JPA Query Methods and for Spring Data @Query annotation.
 */
public interface FilteredGiftCertificateRepository {
	Slice<GiftCertificate> getCertificatesByFilter(GiftCertificateSearchFilter giftCertificateSearchFilter,
	                                               Pageable pageable);
}
