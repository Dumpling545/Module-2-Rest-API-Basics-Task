package com.epam.esm.db.fragment;

import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.GiftCertificateSearchFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FilteredGiftCertificateRepository {
	Slice<GiftCertificate> getCertificatesByFilter(GiftCertificateSearchFilter giftCertificateSearchFilter,
	                                               Pageable pageable);
}
