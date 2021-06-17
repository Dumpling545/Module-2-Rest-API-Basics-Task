package com.epam.esm.service.merger.impl;

import com.epam.esm.model.dto.GiftCertificateUpdateDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.service.merger.Merger;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class GiftCertificateUpdateDtoIntoGiftCertificateMergerTest {
	private static final int ENTITY_ID = 1;
	private static final String ENTITY_NAME = "entity_name";
	private static final String ENTITY_DESC = "entity_desc";
	private static final double ENTITY_PRICE = 1.2;
	private static final int ENTITY_DURATION = 2;
	private static final String DTO_DESC = "dto_desc";
	private static final LocalDateTime ENTITY_CREATE_DATE = null;
	private static final LocalDateTime ENTITY_LAST_UPDATE_DATE = null;

	@Test
	public void mergeShouldReturnBaseEntityWhenPassedEmptyDto() {
		GiftCertificateUpdateDTO dto = new GiftCertificateUpdateDTO(null, null, null, null, null);
		GiftCertificate entity = new GiftCertificate(ENTITY_ID, ENTITY_NAME, ENTITY_DESC, ENTITY_PRICE, ENTITY_DURATION,
				ENTITY_CREATE_DATE, ENTITY_LAST_UPDATE_DATE);
		Merger<GiftCertificate, GiftCertificateUpdateDTO> merger =
				new GiftCertificateUpdateDtoIntoGiftCertificateMerger();
		GiftCertificate newEntity = merger.merge(entity, dto);
		assertEquals(entity, newEntity);
	}

	@Test
	public void mergeShouldReturnNewEntityWhenPassedNonEmptyDto() {
		GiftCertificateUpdateDTO dto = new GiftCertificateUpdateDTO(null, DTO_DESC, null, null, null);
		GiftCertificate entity = new GiftCertificate(ENTITY_ID, ENTITY_NAME, ENTITY_DESC, ENTITY_PRICE, ENTITY_DURATION,
				ENTITY_CREATE_DATE, ENTITY_LAST_UPDATE_DATE);
		Merger<GiftCertificate, GiftCertificateUpdateDTO> merger =
				new GiftCertificateUpdateDtoIntoGiftCertificateMerger();
		GiftCertificate newEntity = merger.merge(entity, dto);
		assertNotEquals(entity, newEntity);
		assertEquals(ENTITY_NAME, newEntity.getName());
		assertEquals(DTO_DESC, newEntity.getDescription());
		assertEquals(ENTITY_PRICE, newEntity.getPrice());
		assertEquals(ENTITY_DURATION, newEntity.getDuration());
	}
}
