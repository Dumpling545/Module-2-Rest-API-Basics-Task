package com.epam.esm.db;

import com.epam.esm.db.config.DBConfig;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.GiftCertificate;

import static org.junit.jupiter.api.Assertions.*;

import com.epam.esm.model.entity.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DBConfig.class)
@ActiveProfiles("test")
public class GiftCertificateRepositoryTest {
	@Autowired
	GiftCertificateRepository giftCertificateRepository;
	@Autowired
	TagRepository tagRepository;
	double epsilon = 1e-5;

	private void assertCertificatesEqual(GiftCertificate c1,
	                                     GiftCertificate c2)
	{
		assertEquals(c1.getId(), c2.getId());
		assertEquals(c1.getName(), c2.getName());
		assertEquals(c1.getDescription(), c2.getDescription());
		assertEquals(c1.getDuration(), c2.getDuration());
		assertEquals(c1.getPrice(), c2.getPrice(), epsilon);
		assertEquals(
				c1.getLastUpdateDate().get(ChronoField.MINUTE_OF_DAY),
				c2.getLastUpdateDate().get(ChronoField.MINUTE_OF_DAY));
		if (c1.getCreateDate() != null && c2.getCreateDate() != null) {
			assertEquals(
					c1.getCreateDate().get(ChronoField.MINUTE_OF_DAY),
					c2.getCreateDate().get(ChronoField.MINUTE_OF_DAY));
		}
	}

	@Test
	public void createCertificatePositiveTest() {
		GiftCertificate certificate =
				new GiftCertificate(-1, "certificate6", "description6",
						123.42, 6, null, null);
		giftCertificateRepository.createCertificate(certificate);
		assertNotEquals(certificate.getId(), -1);
		assertNotNull(certificate.getCreateDate());
		assertNotNull(certificate.getLastUpdateDate());
		Optional<GiftCertificate> copy = giftCertificateRepository
				.getCertificateById(certificate.getId());
		assertFalse(copy.isEmpty());
		assertCertificatesEqual(copy.get(), certificate);
	}

	@Test
	public void getCertificatePositiveTest() {
		Optional<GiftCertificate> optional =
				giftCertificateRepository.getCertificateById(1);
		assertFalse(optional.isEmpty());
		assertEquals(optional.get().getName(), "certificate1");
		assertEquals(optional.get().getDescription(), "description1");
		assertEquals(optional.get().getPrice(), 12.34, epsilon);
		assertEquals(optional.get().getDuration(), 1);
	}

	@Test
	public void getCertificateNegativeTest() {
		Optional<GiftCertificate> optional =
				giftCertificateRepository.getCertificateById(-1);
		assertTrue(optional.isEmpty());
	}

	@Test
	public void filterCertificatesPositiveTest() {
		Filter filter = new Filter();
		filter.setSortBy(Filter.SortOption.CREATE_DATE_DESC);
		filter.setNamePart("certif");
		filter.setDescriptionPart("escriptio");
		filter.setTagId(8);
		List<GiftCertificate> giftCertificates =
				giftCertificateRepository.getCertificatesByFilter(filter);
		assertEquals(giftCertificates.size(), 2);
		assertTrue(giftCertificates.get(0).getCreateDate()
				.isAfter(giftCertificates.get(1).getCreateDate()));
		giftCertificates.removeIf(c -> c.getId() == 1);
		giftCertificates.removeIf(c -> c.getId() == 3);
		assertEquals(giftCertificates.size(), 0);
	}

	@Test
	public void filterCertificatesNegativeTest() {
		Filter filter = new Filter();
		filter.setTagId(4);
		List<GiftCertificate> giftCertificates =
				giftCertificateRepository.getCertificatesByFilter(filter);
		assertEquals(giftCertificates.size(), 0);
	}

	@Test
	public void updateCertificateTest() {
		GiftCertificate giftCertificate =
				new GiftCertificate(1, "certificate111",
						"description111",
						123.42, 60,
						null, null);
		giftCertificateRepository.updateCertificate(giftCertificate);
		Optional<GiftCertificate> copyAfter =
				giftCertificateRepository.getCertificateById(1);
		assertFalse(copyAfter.isEmpty());
		assertCertificatesEqual(copyAfter.get(), giftCertificate);
	}

	@Test
	public void deleteTagPositiveTest() {
		Optional<GiftCertificate> optional =
				giftCertificateRepository.getCertificateById(3);
		assertEquals(optional.isEmpty(), false);
		assertNotEquals(optional.get().getId(), -1);
		boolean deleted = giftCertificateRepository.deleteCertificate(3);
		assertTrue(deleted);
		optional = giftCertificateRepository.getCertificateById(3);
		assertEquals(optional.isEmpty(), true);
	}

	@Test
	public void deleteCertificateNegativeTest() {
		boolean deleted = giftCertificateRepository.deleteCertificate(-1);
		assertFalse(deleted);
	}

	@Test
	public void addTagToCertificateTest() {
		List<Tag> tags = tagRepository.getTagsByCertificate(5);
		assertTrue(tags.stream().noneMatch(t -> t.getId() == 5));
		giftCertificateRepository.addTag(5, 5);
		tags = tagRepository.getTagsByCertificate(5);
		assertTrue(tags.stream().anyMatch(t -> t.getId() == 5));
	}

	@Test
	public void removeTagFromCertificateTest() {
		List<Tag> tags = tagRepository.getTagsByCertificate(3);
		assertTrue(tags.stream().anyMatch(t -> t.getId() == 3));
		giftCertificateRepository.removeTag(3, 3);
		tags = tagRepository.getTagsByCertificate(3);
		assertTrue(tags.stream().noneMatch(t -> t.getId() == 3));
	}

}
