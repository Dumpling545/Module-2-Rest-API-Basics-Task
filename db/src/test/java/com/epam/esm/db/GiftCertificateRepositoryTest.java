package com.epam.esm.db;

import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.SortOption;
import com.epam.esm.model.entity.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@JdbcTest
@Sql({"schema.sql", "test-data.sql"})
public class GiftCertificateRepositoryTest {
	private static final int NON_EXISTING_ID = -1;
	private static final int EXISTING_ID_1 = 1;
	private static final String EXISTING_NAME_1 = "certificate1";
	private static final String EXISTING_DESC_1 = "description1";
	private static final int EXISTING_DURATION_1 = 1;
	private static final BigDecimal EXISTING_PRICE_1 = BigDecimal.valueOf(12.34);
	private static final String NAME_TO_BE_CREATED = "certificate6";
	private static final String DESCRIPTION_TO_BE_CREATED = "description6";
	private static final BigDecimal PRICE_TO_BE_CREATED = BigDecimal.valueOf(123.42);
	private static final int DURATION_TO_BE_CREATED = 6;
	private static final int EXISTING_ID_2 = 2;
	private static final String NEW_NAME_2 = "certificate111";
	private static final String NEW_DESC_2 = "description111";
	private static final BigDecimal NEW_PRICE_2 = BigDecimal.valueOf(123.42);
	private static final int NEW_DURATION_2 = 60;
	private static final int EXISTING_ID_3 = 3;
	private static final int NOT_ASSOCIATED_CERTIFICATE_ID = 5;
	private static final int NOT_ASSOCIATED_TAG_ID = 5;
	private static final int ASSOCIATED_CERTIFICATE_ID = 2;
	private static final int ASSOCIATED_TAG_ID = 7;
	private static final Filter CONDITIONS_THAT_SOME_DATA_MEET =
			new Filter("ertif", "escriptio", "tag8", new SortOption(SortOption.Field.NAME, SortOption.Direction.DESC));
	private static final Filter CONDITIONS_THAT_NO_DATA_MEET =
			new Filter("wfwf", "wef", "tagr8", new SortOption(SortOption.Field.NAME, SortOption.Direction.DESC));
	@Autowired
	GiftCertificateRepository giftCertificateRepository;
	@Autowired
	TagRepository tagRepository;

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
	}

	@Test
	public void createCertificateShouldReturnNewEntityWhenPassedCorrectInput() {
		GiftCertificate certificate = new GiftCertificate(NON_EXISTING_ID, NAME_TO_BE_CREATED,
				DESCRIPTION_TO_BE_CREATED, PRICE_TO_BE_CREATED, DURATION_TO_BE_CREATED, null, null);
		GiftCertificate created = giftCertificateRepository.createCertificate(certificate);
		assertNotEquals(EXISTING_ID_1, created.getId());
		assertNotNull(created.getCreateDate());
		assertNotNull(created.getLastUpdateDate());
		Optional<GiftCertificate> fetchedAfter = giftCertificateRepository.getCertificateById(created.getId());
		assertTrue(fetchedAfter.isPresent());
		assertCertificatesEqual(fetchedAfter.get(), created);
	}

	@Test
	public void getCertificateShouldReturnOptionalWithEntityWhenPassedExistingId() {
		Optional<GiftCertificate> optional = giftCertificateRepository.getCertificateById(EXISTING_ID_1);
		assertTrue(optional.isPresent());
		GiftCertificate fetched = optional.get();
		assertEquals(EXISTING_NAME_1, fetched.getName());
		assertEquals(EXISTING_DESC_1, fetched.getDescription());
		assertEquals(EXISTING_DURATION_1, fetched.getDuration());
		assertEquals(EXISTING_PRICE_1, fetched.getPrice());
	}

	@Test
	public void getCertificateShouldReturnEmptyOptionalWhenPassedNonExistingId() {
		Optional<GiftCertificate> optional = giftCertificateRepository.getCertificateById(NON_EXISTING_ID);
		assertTrue(optional.isEmpty());
	}

	@Test
	public void filterCertificatesShouldReturnListWhenPassedConditionsMetByAnyDataInDatabase() {
		List<GiftCertificate> giftCertificates =
				giftCertificateRepository.getCertificatesByFilter(CONDITIONS_THAT_SOME_DATA_MEET);
		assertFalse(giftCertificates.isEmpty());
		for (GiftCertificate gc : giftCertificates) {
			assertTrue(gc.getName().contains(CONDITIONS_THAT_SOME_DATA_MEET.getNamePart()));
			assertTrue(gc.getDescription().contains(CONDITIONS_THAT_SOME_DATA_MEET.getDescriptionPart()));
		}
	}

	@Test
	public void filterCertificatesShouldReturnEmptyListWhenPassedConditionsMetByNoDataInDatabase() {
		List<GiftCertificate> giftCertificates =
				giftCertificateRepository.getCertificatesByFilter(CONDITIONS_THAT_NO_DATA_MEET);
		assertTrue(giftCertificates.isEmpty());
	}

	@Test
	public void updateCertificateShouldReturnTrueWhenPassedCorrectInput() {
		GiftCertificate giftCertificate = new GiftCertificate(EXISTING_ID_2, NEW_NAME_2, NEW_DESC_2, NEW_PRICE_2,
				NEW_DURATION_2, null, null);
		boolean updated = giftCertificateRepository.updateCertificate(giftCertificate);
		assertTrue(updated);
		Optional<GiftCertificate> fetchAfter = giftCertificateRepository.getCertificateById(EXISTING_ID_2);
		assertTrue(fetchAfter.isPresent());
		assertCertificatesEqual(fetchAfter.get(), giftCertificate);
	}

	@Test
	public void deleteTagShouldReturnTrueWhenPassedExistingId() {
		boolean deleted = giftCertificateRepository.deleteCertificate(EXISTING_ID_3);
		assertTrue(deleted);
		Optional<GiftCertificate> optional = giftCertificateRepository.getCertificateById(EXISTING_ID_3);
		assertTrue(optional.isEmpty());
	}

	@Test
	public void deleteTagShouldReturnFalseWhenPassedNonExistingId() {
		boolean deleted = giftCertificateRepository.deleteCertificate(NON_EXISTING_ID);
		assertFalse(deleted);
	}

	@Test
	public void addTagShouldNotThrowExceptionWhenPassedNotAssociatedIds() {
		assertDoesNotThrow(() -> {
			giftCertificateRepository.addTagToCertificate(NOT_ASSOCIATED_CERTIFICATE_ID, NOT_ASSOCIATED_TAG_ID);
			List<Tag> tags = tagRepository.getTagsByCertificate(NOT_ASSOCIATED_CERTIFICATE_ID);
			assertTrue(tags.stream().anyMatch(t -> t.getId() == NOT_ASSOCIATED_TAG_ID));
		});
	}

	@Test
	public void removeTagShouldNotThrowExceptionWhenPassedNotAssociatedIds() {
		assertDoesNotThrow(() -> {
			giftCertificateRepository.removeTagFromCertificate(ASSOCIATED_CERTIFICATE_ID, ASSOCIATED_TAG_ID);
			List<Tag> tags = tagRepository.getTagsByCertificate(ASSOCIATED_CERTIFICATE_ID);
			assertTrue(tags.stream().noneMatch(t -> t.getId() == ASSOCIATED_TAG_ID));
		});
	}

}
