package com.epam.esm.db;

import com.epam.esm.GiftCertificateSystemApplication;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.GiftCertificateSearchFilter;
import com.epam.esm.model.entity.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = GiftCertificateSystemApplication.class)
@AutoConfigureTestDatabase
public class GiftCertificateRepositoryTest {
	private static final int NON_EXISTING_ID = -1;
	private static final Tag existingTag1 = new Tag(1, "tag1");
	private static final Tag existingTag2 = new Tag(2, "tag2");
	private static final Tag existingTag3 = new Tag(3, "tag3");
	private static final Tag existingTag4 = new Tag(4, "tag4");
	private static final Tag existingTag5 = new Tag(5, "tag5");
	private static final Tag existingTag6 = new Tag(6, "tag6");
	private static final Tag existingTag7 = new Tag(7, "tag7");
	private static final Tag existingTag8 = new Tag(8, "tag8");
	private static final Tag existingTag9 = new Tag(9, "tag9");
	private static final Tag existingTag10 = new Tag(10, "tag10");

	private static final GiftCertificate certToBeCreated = GiftCertificate.builder()
			.id(null)
			.name("certificate8")
			.description("description8")
			.duration(10)
			.price(BigDecimal.valueOf(123.42))
			.tags(Set.of(existingTag1, existingTag2))
			.build();
	private static final GiftCertificate existingCert1 = GiftCertificate.builder()
			.id(1)
			.name("certificate1")
			.description("description1")
			.duration(1)
			.price(BigDecimal.valueOf(12.34))
			.tags(Set.of(existingTag8, existingTag9, existingTag10))
			.build();
	private static final GiftCertificate existingCert2 = GiftCertificate.builder()
			.id(2)
			.name("certificate2")
			.description("description2")
			.duration(2)
			.createDate(LocalDate.parse("2021-01-02").atStartOfDay())
			.price(BigDecimal.valueOf(9112.18))
			.tags(Set.of(existingTag3, existingTag6, existingTag7))
			.build();
	private static final GiftCertificate updatedCert2 = GiftCertificate.builder()
			.id(2)
			.name("certificate111")
			.description("description111")
			.duration(234)
			.createDate(LocalDate.parse("2021-01-02").atStartOfDay())
			.price(BigDecimal.valueOf(1.23))
			.tags(Set.of(existingTag4, existingTag6))
			.build();
	private static final int EXISTING_ID_3 = 3;
	private static final GiftCertificateSearchFilter CONDITIONS_THAT_SOME_DATA_MEET =
			GiftCertificateSearchFilter.builder()
					.namePart("ertif")
					.descriptionPart("escriptio")
					.tagNames(Set.of("tag8")).build();
	private static final GiftCertificateSearchFilter CONDITIONS_THAT_NO_DATA_MEET =
			GiftCertificateSearchFilter.builder()
					.tagNames(Set.of("non existent")).build();
	private static final Pageable existingPageable = PageRequest.of(0, 10);
	private static final Pageable unexistingPageable = PageRequest.of(10, 10);

	@Autowired
	GiftCertificateRepository giftCertificateRepository;

	private void assertCertificatesEqual(GiftCertificate c1, GiftCertificate c2) {
		assertEquals(c1.getId(), c2.getId());
		assertEquals(c1.getName(), c2.getName());
		assertEquals(c1.getDescription(), c2.getDescription());
		assertEquals(c1.getDuration(), c2.getDuration());
		assertEquals(c1.getPrice(), c2.getPrice());
		if (c1.getLastUpdateDate() != null && c2.getLastUpdateDate() != null) {
			assertEquals(c1.getLastUpdateDate().get(ChronoField.MINUTE_OF_DAY),
			             c2.getLastUpdateDate().get(ChronoField.MINUTE_OF_DAY));
		}
		if (c1.getCreateDate() != null && c2.getCreateDate() != null) {
			assertEquals(c1.getCreateDate().get(ChronoField.MINUTE_OF_DAY),
			             c2.getCreateDate().get(ChronoField.MINUTE_OF_DAY));
		}
		assertEquals(c1.getTags(), c2.getTags());
	}

	@Test
	public void createCertificateShouldReturnNewEntityWhenPassedCorrectInput() {
		GiftCertificate created = giftCertificateRepository.save(certToBeCreated);
		assertNotNull(created.getCreateDate());
		assertNotNull(created.getLastUpdateDate());
		Optional<GiftCertificate> fetchedAfter = giftCertificateRepository.findById(created.getId());
		assertTrue(fetchedAfter.isPresent());
		assertCertificatesEqual(fetchedAfter.get(), created);
	}

	@Test
	public void getCertificateShouldReturnOptionalWithEntityWhenPassedExistingId() {
		Optional<GiftCertificate> optional = giftCertificateRepository.findById(existingCert1.getId());
		assertTrue(optional.isPresent());
		GiftCertificate fetched = optional.get();
		assertCertificatesEqual(existingCert1, fetched);
	}

	@Test
	public void getCertificateShouldReturnEmptyOptionalWhenPassedNonExistingId() {
		Optional<GiftCertificate> optional = giftCertificateRepository.findById(NON_EXISTING_ID);
		assertTrue(optional.isEmpty());
	}

	@Test
	public void filterCertificatesShouldReturnListWhenPassedConditionsMetByAnyDataInDatabase() {
		Slice<GiftCertificate> giftCertificates =
				giftCertificateRepository.getCertificatesByFilter(CONDITIONS_THAT_SOME_DATA_MEET, existingPageable);
		assertFalse(giftCertificates.isEmpty());
	}

	@Test
	public void filterCertificatesShouldReturnEmptyListWhenPassedConditionsMetByNoDataInDatabase() {
		Slice<GiftCertificate> giftCertificates =
				giftCertificateRepository.getCertificatesByFilter(CONDITIONS_THAT_NO_DATA_MEET, existingPageable);
		assertTrue(giftCertificates.isEmpty());
	}

	@Test
	public void updateCertificateShouldReturnTrueWhenPassedCorrectInput() {
		giftCertificateRepository.save(updatedCert2);
		Optional<GiftCertificate> fetchAfter = giftCertificateRepository.findById(updatedCert2.getId());
		assertTrue(fetchAfter.isPresent());
		assertEquals(updatedCert2.getTags().stream().map(Tag::getName).collect(Collectors.toSet()),
		             fetchAfter.get().getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
	}

	@Test
	public void deleteTagShouldNotThrowExceptionWhenPassedExistingId() {
		assertDoesNotThrow(() -> giftCertificateRepository.deleteById(EXISTING_ID_3));
		Optional<GiftCertificate> optional = giftCertificateRepository.findById(EXISTING_ID_3);
		assertTrue(optional.isEmpty());
	}

	@Test
	public void deleteTagShouldThrowExceptionWhenPassedNonExistingId() {
		assertThrows(EmptyResultDataAccessException.class, () -> giftCertificateRepository.deleteById(NON_EXISTING_ID));
	}
}
