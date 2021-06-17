package com.epam.esm.service.impl;


import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.model.dto.FilterDTO;
import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.GiftCertificateUpdateDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.Filter;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.converter.Converter;
import com.epam.esm.service.exception.InvalidCertificateException;
import com.epam.esm.service.exception.InvalidTagException;
import com.epam.esm.service.merger.Merger;
import com.epam.esm.service.validator.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;

@TestInstance(Lifecycle.PER_CLASS)
public class GiftCertificateServiceImplTest {
	private static final int DEFAULT_ID = -1;
	private static final int NON_EXISTING_ID = -1;
	private static final String DB_TAG_NAME_1 = "tag name 1";
	private static final String DB_TAG_NAME_2 = "tag name 2";
	private static final String NO_DB_TAG_NAME_3 = "tag name 3";
	private static final String NO_DB_TAG_NAME_4 = "tag name 4";
	private static final int DB_ID_1 = 1;
	private static final int DB_ID_2 = 2;
	private static final int NO_DB_ID_3 = 3;
	private static final int NO_DB_ID_4 = 4;
	private static final int CERT_ID = 1;
	private static final String CERT_NAME = "cert name";
	private static final String INVALID_CERT_NAME = "invalid cert name";
	private static final String CERT_DESC = "cert desc";
	private static final double CERT_PRICE = 12.3;
	private static final int CERT_DURATION = 3;
	private static final LocalDateTime CERT_CREATE_DATE = LocalDateTime.MIN;
	private static final LocalDateTime CERT_LAST_UPDATE_DATE = LocalDateTime.MAX;
	private static final String NOT_FOUND_MESSAGE_FIELD_NAME = "notFoundExceptionTemplate";
	private static final String MOCK_EX_MESSAGE = "test";
	private static final String CERT_NEW_NAME = "new cert name";
	private TagService tagService = Mockito.mock(TagService.class);
	private GiftCertificateRepository giftCertificateRepository = Mockito.mock(GiftCertificateRepository.class);
	private Validator<GiftCertificateCreateDTO> certCreateValidator = Mockito.mock(Validator.class);
	private Validator<GiftCertificateUpdateDTO> certUpdateValidator = Mockito.mock(Validator.class);
	private Merger<GiftCertificate, GiftCertificateUpdateDTO> updateDtoIntoCertMerger = Mockito.mock(Merger.class);
	private Converter<FilterDTO, Filter> dtoToFilterConverter = Mockito.mock(Converter.class);
	private Converter<GiftCertificateCreateDTO, GiftCertificate> createDtoToCertConverter =
			Mockito.mock(Converter.class);
	private Converter<GiftCertificate, GiftCertificateOutputDTO> certToOutputDtoConverter =
			Mockito.mock(Converter.class);

	@BeforeAll
	public void init() {
		initTagServiceForCreateTagMethod();
		initTagServiceForGetTagsFromNameSetMethod();
		initTagServiceForGetTagsByCertificateMethod();
		initCertificateRepository();
		initValidators();
		initConverters();
		initMergers();
	}

	private void initTagServiceForCreateTagMethod() {
		Mockito.when(tagService.createTag(Mockito.any())).then(answer -> {
			TagDTO tagDTO = answer.getArgument(0);
			int id = -1;
			switch (tagDTO.getName()) {
				case DB_TAG_NAME_1 -> throw new InvalidTagException("", InvalidTagException.Reason.ALREADY_EXISTS,
						DB_TAG_NAME_1);
				case DB_TAG_NAME_2 -> throw new InvalidTagException("", InvalidTagException.Reason.ALREADY_EXISTS,
						DB_TAG_NAME_2);
				case NO_DB_TAG_NAME_3 -> id = NO_DB_ID_3;
				case NO_DB_TAG_NAME_4 -> id = NO_DB_ID_4;
			}
			TagDTO output = new TagDTO(id, tagDTO.getName());
			return output;
		});

	}

	private void initTagServiceForGetTagsFromNameSetMethod() {
		Mockito.when(tagService.getTagsFromNameSet(Mockito.any())).then(answer -> {
			Set<String> nameSet = answer.getArgument(0);
			Set<TagDTO> tagSet = new HashSet<>();
			if (nameSet.contains(DB_TAG_NAME_1)) {
				tagSet.add(new TagDTO(DB_ID_1, DB_TAG_NAME_1));
			}
			if (nameSet.contains(DB_TAG_NAME_2)) {
				tagSet.add(new TagDTO(DB_ID_2, DB_TAG_NAME_2));
			}
			if (tagSet.size() == 0) {
				tagSet = Collections.EMPTY_SET;
			}
			return tagSet;
		});
	}

	private void initTagServiceForGetTagsByCertificateMethod() {
		Set<TagDTO> tagDTOs = new HashSet<>();
		tagDTOs.add(new TagDTO(DB_ID_1, DB_TAG_NAME_1));
		tagDTOs.add(new TagDTO(DB_ID_2, DB_TAG_NAME_2));
		Mockito.when(tagService.getTagsByCertificate(Mockito.eq(CERT_ID))).thenReturn(tagDTOs);
	}

	private void initCertificateRepository() {
		Mockito.when(giftCertificateRepository.createCertificate(Mockito.any())).then(answer -> {
			GiftCertificate cert = answer.getArgument(0);
			GiftCertificate output =
					new GiftCertificate(CERT_ID, cert.getName(), cert.getDescription(), cert.getPrice(),
							cert.getDuration(), CERT_CREATE_DATE, CERT_LAST_UPDATE_DATE);
			return output;
		});
		Mockito.when(giftCertificateRepository.getCertificateById(Mockito.eq(CERT_ID))).thenReturn(Optional.of(
				new GiftCertificate(CERT_ID, CERT_NAME, CERT_DESC, CERT_PRICE, CERT_DURATION, CERT_CREATE_DATE,
						CERT_LAST_UPDATE_DATE)));
		Mockito.when(giftCertificateRepository.getCertificateById(Mockito.eq(NON_EXISTING_ID)))
				.thenReturn(Optional.empty());
		Mockito.when(giftCertificateRepository.deleteCertificate(Mockito.eq(CERT_ID))).thenReturn(true);
		Mockito.when(giftCertificateRepository.deleteCertificate(Mockito.eq(NON_EXISTING_ID))).thenReturn(false);
		Mockito.when(giftCertificateRepository.updateCertificate(Mockito.argThat(a -> a.getId() == CERT_ID)))
				.thenReturn(true);
	}

	private void initValidators() {
		Mockito.doAnswer(answer -> {
			throw new InvalidCertificateException("", InvalidCertificateException.Reason.INVALID_NAME);
		}).when(certCreateValidator).validate(Mockito.argThat(arg -> arg.getName().equals(INVALID_CERT_NAME)));
		Mockito.doAnswer(answer -> {
			throw new InvalidCertificateException("", InvalidCertificateException.Reason.INVALID_NAME);
		}).when(certUpdateValidator).validate(Mockito.argThat(arg -> arg.getName().equals(INVALID_CERT_NAME)));
	}

	private void initConverters() {
		Mockito.when(createDtoToCertConverter.convert(Mockito.any())).then(answer -> {
			GiftCertificateCreateDTO dto = (GiftCertificateCreateDTO) answer.getArgument(0);
			return new GiftCertificate(DEFAULT_ID, dto.getName(), dto.getDescription(), dto.getPrice(),
					dto.getDuration(), null, null);
		});
		Mockito.when(certToOutputDtoConverter.convert(Mockito.any())).then(answer -> {
			GiftCertificate entity = (GiftCertificate) answer.getArgument(0);
			return new GiftCertificateOutputDTO(entity.getId(), entity.getName(), entity.getDescription(),
					entity.getPrice(), entity.getDuration(), entity.getCreateDate(), entity.getLastUpdateDate(),
					new HashSet<>());
		});
	}

	private void initMergers() {
		Mockito.when(updateDtoIntoCertMerger.merge(Mockito.any(), Mockito.any())).then(answer -> {
			GiftCertificate cert = answer.getArgument(0);
			GiftCertificateUpdateDTO updateDTO = answer.getArgument(1);
			String name = updateDTO.getName() == null ? cert.getName() : updateDTO.getName();
			String description =
					updateDTO.getDescription() == null ? cert.getDescription() : updateDTO.getDescription();
			Integer duration = updateDTO.getDuration() == null ? cert.getDuration() : updateDTO.getDuration();
			Double price = updateDTO.getPrice() == null ? cert.getPrice() : updateDTO.getPrice();
			GiftCertificate certificate =
					new GiftCertificate(cert.getId(), name, description, price, duration, cert.getCreateDate(),
							cert.getLastUpdateDate());
			return certificate;
		});
	}

	private Stream<GiftCertificateCreateDTO> createTestSources() {
		GiftCertificateCreateDTO withoutTags =
				new GiftCertificateCreateDTO(CERT_NAME, CERT_DESC, CERT_PRICE, CERT_DURATION, null);
		GiftCertificateCreateDTO withEmptyTagSet =
				new GiftCertificateCreateDTO(CERT_NAME, CERT_DESC, CERT_PRICE, CERT_DURATION, Collections.EMPTY_SET);
		Set<String> setOnlyWithExistingTags = new HashSet<>();
		setOnlyWithExistingTags.add(DB_TAG_NAME_1);
		setOnlyWithExistingTags.add(DB_TAG_NAME_2);
		GiftCertificateCreateDTO onlyWithExistingTags =
				new GiftCertificateCreateDTO(CERT_NAME, CERT_DESC, CERT_PRICE, CERT_DURATION, setOnlyWithExistingTags);
		Set<String> setOnlyWithNewTags = new HashSet<>();
		setOnlyWithNewTags.add(NO_DB_TAG_NAME_3);
		setOnlyWithNewTags.add(NO_DB_TAG_NAME_4);
		GiftCertificateCreateDTO onlyWithNewTags =
				new GiftCertificateCreateDTO(CERT_NAME, CERT_DESC, CERT_PRICE, CERT_DURATION, setOnlyWithNewTags);
		Set<String> setWithBothExistingAndNewTags = new HashSet<>();
		setWithBothExistingAndNewTags.add(DB_TAG_NAME_1);
		setWithBothExistingAndNewTags.add(NO_DB_TAG_NAME_4);
		GiftCertificateCreateDTO withBothExistingAndNewTags =
				new GiftCertificateCreateDTO(CERT_NAME, CERT_DESC, CERT_PRICE, CERT_DURATION,
						setWithBothExistingAndNewTags);
		return Stream.of(withoutTags, onlyWithExistingTags, onlyWithNewTags, withBothExistingAndNewTags);
	}

	@ParameterizedTest
	@MethodSource("createTestSources")
	public void createCertificateShouldNotThrowExceptionWhenPassedCorrectDto(GiftCertificateCreateDTO dto) {
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagService, giftCertificateRepository, certCreateValidator,
						certUpdateValidator, updateDtoIntoCertMerger, dtoToFilterConverter, createDtoToCertConverter,
						certToOutputDtoConverter);
		assertDoesNotThrow(() -> {
			GiftCertificateOutputDTO outputDTO = service.createCertificate(dto);
			if (dto.getTagNames() != null) {
				Set<String> outputTagNames =
						outputDTO.getTags().stream().map(t -> t.getName()).collect(Collectors.toSet());
				assertEquals(dto.getTagNames(), outputTagNames);
			}
		});
	}

	@Test
	public void createCertificateShouldThrowExceptionWhenPassedInvalidDto() {
		GiftCertificateCreateDTO dto =
				new GiftCertificateCreateDTO(INVALID_CERT_NAME, CERT_DESC, CERT_PRICE, CERT_DURATION,
						Collections.EMPTY_SET);
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagService, giftCertificateRepository, certCreateValidator,
						certUpdateValidator, updateDtoIntoCertMerger, dtoToFilterConverter, createDtoToCertConverter,
						certToOutputDtoConverter);
		InvalidCertificateException ex =
				assertThrows(InvalidCertificateException.class, () -> service.createCertificate(dto));
		assertEquals(InvalidCertificateException.Reason.INVALID_NAME, ex.getReason());
	}

	private Stream<Arguments> updateTestSources() {
		GiftCertificateUpdateDTO tagsNotChanged =
				new GiftCertificateUpdateDTO(CERT_NEW_NAME, CERT_DESC, CERT_PRICE, CERT_DURATION, null);
		int tagsNotChangedAddedTagsCount = 0;
		int tagsNotChangedRemovedTagsCount = 0;


		GiftCertificateUpdateDTO allTagsRemoved =
				new GiftCertificateUpdateDTO(CERT_NEW_NAME, CERT_DESC, CERT_PRICE, CERT_DURATION,
						Collections.EMPTY_SET);
		int allTagsRemovedAddedTagsCount = 0;
		int allTagsRemovedRemovedTagsCount = 2;
		Set<String> setOnlyAddNewTags = new HashSet<>();
		setOnlyAddNewTags.add(DB_TAG_NAME_1);
		setOnlyAddNewTags.add(DB_TAG_NAME_2);
		setOnlyAddNewTags.add(NO_DB_TAG_NAME_3);
		GiftCertificateUpdateDTO onlyAddNewTags =
				new GiftCertificateUpdateDTO(CERT_NEW_NAME, CERT_DESC, CERT_PRICE, CERT_DURATION, setOnlyAddNewTags);
		int onlyAddNewTagsAddedTagsCount = 1;
		int onlyAddNewTagsRemovedTagsCount = 0;
		Set<String> setOnlyRemoveOldTags = new HashSet<>();
		setOnlyRemoveOldTags.add(DB_TAG_NAME_1);
		GiftCertificateUpdateDTO onlyRemoveOldTags =
				new GiftCertificateUpdateDTO(CERT_NEW_NAME, CERT_DESC, CERT_PRICE, CERT_DURATION, setOnlyRemoveOldTags);
		int onlyRemoveOldTagsAddedTagsCount = 0;
		int onlyRemoveOldTagsRemovedTagsCount = 1;
		Set<String> setBothAddAndRemoveTags = new HashSet<>();
		setBothAddAndRemoveTags.add(DB_TAG_NAME_1);
		setBothAddAndRemoveTags.add(NO_DB_TAG_NAME_3);
		GiftCertificateUpdateDTO bothAddAndRemoveTags =
				new GiftCertificateUpdateDTO(CERT_NEW_NAME, CERT_DESC, CERT_PRICE, CERT_DURATION,
						setBothAddAndRemoveTags);
		int bothAddAndRemoveTagsAddedTagsCount = 1;
		int bothAddAndRemoveTagsRemovedTagsCount = 1;
		return Stream.of(arguments(tagsNotChanged, tagsNotChangedAddedTagsCount, tagsNotChangedRemovedTagsCount),
				arguments(allTagsRemoved, allTagsRemovedAddedTagsCount, allTagsRemovedRemovedTagsCount),
				arguments(onlyAddNewTags, onlyAddNewTagsAddedTagsCount, onlyAddNewTagsRemovedTagsCount),
				arguments(onlyRemoveOldTags, onlyRemoveOldTagsAddedTagsCount, onlyRemoveOldTagsRemovedTagsCount),
				arguments(bothAddAndRemoveTags, bothAddAndRemoveTagsAddedTagsCount,
						bothAddAndRemoveTagsRemovedTagsCount));
	}

	@ParameterizedTest
	@MethodSource("updateTestSources")
	public void updateCertificateShouldNotThrowExceptionWhenPassedCorrectDto(GiftCertificateUpdateDTO dto,
	                                                                         int addedTagsCount, int removedTagsCount) {
		Mockito.clearInvocations(giftCertificateRepository);
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagService, giftCertificateRepository, certCreateValidator,
						certUpdateValidator, updateDtoIntoCertMerger, dtoToFilterConverter, createDtoToCertConverter,
						certToOutputDtoConverter);
		assertDoesNotThrow(() -> {
			service.updateCertificate(CERT_ID, dto);
			Mockito.verify(giftCertificateRepository, Mockito.times(addedTagsCount))
					.addTagToCertificate(eq(CERT_ID), anyInt());
			Mockito.verify(giftCertificateRepository, Mockito.times(removedTagsCount))
					.removeTagFromCertificate(eq(CERT_ID), anyInt());
		});
	}

	@Test
	public void updateCertificateShouldThrowExceptionWhenPassedNonExistingId() {
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagService, giftCertificateRepository, certCreateValidator,
						certUpdateValidator, updateDtoIntoCertMerger, dtoToFilterConverter, createDtoToCertConverter,
						certToOutputDtoConverter);
		ReflectionTestUtils.setField(service, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		GiftCertificateUpdateDTO dto = new GiftCertificateUpdateDTO(CERT_NEW_NAME, CERT_DESC, CERT_PRICE, CERT_DURATION,
				null);
		InvalidCertificateException ex =
				assertThrows(InvalidCertificateException.class, () -> service.updateCertificate(NON_EXISTING_ID, dto));
		assertEquals(InvalidCertificateException.Reason.NOT_FOUND, ex.getReason());
	}

	@Test
	public void getCertificateShouldReturnDtoWhenPassedExistingId() {
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagService, giftCertificateRepository, certCreateValidator,
						certUpdateValidator, updateDtoIntoCertMerger, dtoToFilterConverter, createDtoToCertConverter,
						certToOutputDtoConverter);
		assertDoesNotThrow(() -> {
			GiftCertificateOutputDTO outputDTO = service.getCertificate(CERT_ID);
			assertEquals(CERT_ID, outputDTO.getId());
			assertEquals(CERT_NAME, outputDTO.getName());
			assertEquals(CERT_DESC, outputDTO.getDescription());
			assertEquals(CERT_PRICE, outputDTO.getPrice());
			assertEquals(CERT_DURATION, outputDTO.getDuration());
			assertEquals(CERT_CREATE_DATE, outputDTO.getCreateDate());
			assertEquals(CERT_LAST_UPDATE_DATE, outputDTO.getLastUpdateDate());
			Set<String> outputTagNames = outputDTO.getTags().stream().map(t -> t.getName()).collect(Collectors.toSet());
			assertTrue(outputTagNames.contains(DB_TAG_NAME_1));
			assertTrue(outputTagNames.contains(DB_TAG_NAME_2));

		});
	}

	@Test
	public void getCertificateShouldThrowExceptionWhenPassedNonExistingId() {
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagService, giftCertificateRepository, certCreateValidator,
						certUpdateValidator, updateDtoIntoCertMerger, dtoToFilterConverter, createDtoToCertConverter,
						certToOutputDtoConverter);
		ReflectionTestUtils.setField(service, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		InvalidCertificateException ex =
				assertThrows(InvalidCertificateException.class, () -> service.getCertificate(NON_EXISTING_ID));
		assertEquals(InvalidCertificateException.Reason.NOT_FOUND, ex.getReason());
	}

	@Test
	public void deleteCertificateShouldNotThrowExceptionWhenPassedExistingId() {
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagService, giftCertificateRepository, certCreateValidator,
						certUpdateValidator, updateDtoIntoCertMerger, dtoToFilterConverter, createDtoToCertConverter,
						certToOutputDtoConverter);
		assertDoesNotThrow(() -> service.deleteCertificate(CERT_ID));
	}

	@Test
	public void deleteCertificateShouldThrowExceptionWhenPassedNonExistingId() {
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagService, giftCertificateRepository, certCreateValidator,
						certUpdateValidator, updateDtoIntoCertMerger, dtoToFilterConverter, createDtoToCertConverter,
						certToOutputDtoConverter);
		ReflectionTestUtils.setField(service, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
		InvalidCertificateException ex =
				assertThrows(InvalidCertificateException.class, () -> service.deleteCertificate(NON_EXISTING_ID));
		assertEquals(InvalidCertificateException.Reason.NOT_FOUND, ex.getReason());
	}
}
