package com.epam.esm.service.impl;

import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.db.TagRepository;
import com.epam.esm.model.dto.GiftCertificateDTO;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.exception.GiftCertificateNotFoundException;
import com.epam.esm.service.exception.InvalidCertificateDescriptionException;
import com.epam.esm.service.exception.InvalidTagNameException;
import com.epam.esm.service.validator.GiftCertificateValidator;
import com.epam.esm.service.validator.TagValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestInstance(PER_CLASS)
public class GiftCertificateServiceImplTest {

	private Stream<Arguments> createTestSources() {
		List<Tag> existingTags = new ArrayList<>();
		existingTags.add(new Tag(1, "existing tag #1"));
		existingTags.add(new Tag(2, "existing tag #2"));
		existingTags.add(new Tag(3, "existing tag #3"));
		GiftCertificateDTO noTags = new GiftCertificateDTO(new ArrayList<>());
		noTags.setName("name1");
		List<String> allTagsExistsList = new ArrayList<>();
		allTagsExistsList.add(existingTags.get(0).getName());
		allTagsExistsList.add(existingTags.get(2).getName());
		GiftCertificateDTO allTagsExists = new GiftCertificateDTO(allTagsExistsList);
		allTagsExists.setName("name2");
		String newTagName1 = "new tag #1";
		String newTagName2 = "new tag #2";
		List<String> allTagsDoNotExistList = new ArrayList<>();
		allTagsDoNotExistList.add(newTagName1);
		allTagsDoNotExistList.add(newTagName2);
		GiftCertificateDTO allTagsDoNotExist = new GiftCertificateDTO(allTagsDoNotExistList);
		allTagsDoNotExist.setName("name3");
		List<String> mixedList = new ArrayList<>();
		mixedList.add(existingTags.get(1).getName());
		mixedList.add(newTagName1);
		GiftCertificateDTO mixed = new GiftCertificateDTO(mixedList);
		mixed.setName("name4");
		return Stream.of(arguments(new ArrayList<>(existingTags), noTags),
				arguments(new ArrayList<>(existingTags), allTagsExists),
				arguments(new ArrayList<>(existingTags), allTagsDoNotExist),
				arguments(new ArrayList<>(existingTags), mixed));
	}

	@ParameterizedTest
	@MethodSource("createTestSources")
	public void createCertificatePositiveTest(List<Tag> existingTags, GiftCertificateDTO dto) {
		int newCertId = 1;
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		GiftCertificateRepository giftCertificateRepository = Mockito.mock(GiftCertificateRepository.class);
		GiftCertificateValidator giftCertificateValidator = Mockito.mock(GiftCertificateValidator.class);
		Mockito.doAnswer(invocation -> {
			Tag tag = ((Tag) invocation.getArgument(0));
			existingTags.add(tag);
			tag.setId(existingTags.size());
			return null;
		}).when(tagRepository).createTag(Mockito.argThat(
				newTag -> existingTags.stream().noneMatch(exTag -> exTag.getName().equals(newTag.getName()))));
		Mockito.doThrow(DuplicateKeyException.class).when(tagRepository).createTag(Mockito.argThat(
				newTag -> existingTags.stream().anyMatch(exTag -> exTag.getName().equals(newTag.getName()))));
		Mockito.when(tagRepository.getAllTags()).thenReturn(existingTags);
		Mockito.doAnswer(invocation -> {
			GiftCertificate giftCertificate = ((GiftCertificate) invocation.getArgument(0));
			giftCertificate.setId(newCertId);
			return null;
		}).when(giftCertificateRepository).createCertificate(Mockito.argThat(t -> t.getName().equals(dto.getName())));
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagRepository, giftCertificateRepository, tagValidator,
						giftCertificateValidator);
		assertDoesNotThrow(() -> {
			GiftCertificateOutputDTO outputDTO = service.createCertificate(dto);
			assertEquals(outputDTO.getId(), newCertId);
			assertEquals(outputDTO.getTags().stream().anyMatch(t -> t.getId() == -1), false);
		});

	}

	@Test
	public void createCertificateInvalidTagTest() {
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		String newTagName1 = "new tag #1";
		String newTagName2 = "new tag #2";
		List<String> tagNames = new ArrayList<>();
		tagNames.add(newTagName1);
		tagNames.add(newTagName2);
		GiftCertificateDTO dto = new GiftCertificateDTO(tagNames);
		GiftCertificateRepository giftCertificateRepository = Mockito.mock(GiftCertificateRepository.class);
		GiftCertificateValidator giftCertificateValidator = Mockito.mock(GiftCertificateValidator.class);
		Mockito.doThrow(InvalidTagNameException.class).when(tagValidator).validateTagName(Mockito.eq(newTagName2));
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagRepository, giftCertificateRepository, tagValidator,
						giftCertificateValidator);
		assertThrows(InvalidTagNameException.class, () -> service.createCertificate(dto));

	}

	@Test
	public void createCertificateInvalidCertificateTest() {
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		GiftCertificateDTO dto = new GiftCertificateDTO(new ArrayList<>());
		GiftCertificateRepository giftCertificateRepository = Mockito.mock(GiftCertificateRepository.class);
		GiftCertificateValidator giftCertificateValidator = Mockito.mock(GiftCertificateValidator.class);
		Mockito.doThrow(InvalidCertificateDescriptionException.class).when(giftCertificateValidator)
				.validateCertificate(Mockito.same(dto), Mockito.eq(false));
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagRepository, giftCertificateRepository, tagValidator,
						giftCertificateValidator);
		assertThrows(InvalidCertificateDescriptionException.class, () -> service.createCertificate(dto));

	}

	@ParameterizedTest
	@MethodSource("createTestSources")
	public void updateCertificatePositiveTest(List<Tag> existingTags, GiftCertificateDTO dto) {
		int certId = 1;
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		GiftCertificateRepository giftCertificateRepository = Mockito.mock(GiftCertificateRepository.class);
		GiftCertificateValidator giftCertificateValidator = Mockito.mock(GiftCertificateValidator.class);
		GiftCertificate existing = new GiftCertificate(certId, "old name", "old description", 3.2, 4, null, null);
		List<Tag> tags = new ArrayList<>(existingTags);
		Optional opt = Optional.of(existing);
		Mockito.when(giftCertificateRepository.getCertificateById(Mockito.eq(certId))).thenReturn(opt);
		Mockito.when(tagRepository.getTagsByCertificate(Mockito.eq(certId))).thenReturn(tags);
		GiftCertificateDTO update =
				new GiftCertificateDTO(certId, "new name", null, 30.4, 40, null, null, dto.getTags());

		Mockito.doAnswer(invocation -> {
			Tag tag = ((Tag) invocation.getArgument(0));
			existingTags.add(tag);
			tag.setId(existingTags.size());
			return null;
		}).when(tagRepository).createTag(Mockito.argThat(
				newTag -> existingTags.stream().noneMatch(exTag -> exTag.getName().equals(newTag.getName()))));
		Mockito.doThrow(DuplicateKeyException.class).when(tagRepository).createTag(Mockito.argThat(
				newTag -> existingTags.stream().anyMatch(exTag -> exTag.getName().equals(newTag.getName()))));
		Mockito.when(tagRepository.getAllTags()).thenReturn(existingTags);
		Mockito.doAnswer(invocation -> {
			GiftCertificate giftCertificate = ((GiftCertificate) invocation.getArgument(0));
			giftCertificate.setId(certId);
			return null;
		}).when(giftCertificateRepository).createCertificate(Mockito.argThat(t -> t.getName().equals(dto.getName())));
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagRepository, giftCertificateRepository, tagValidator,
						giftCertificateValidator);
		assertDoesNotThrow(() -> {
			GiftCertificateOutputDTO outputDTO = service.updateCertificate(update);
			assertEquals(outputDTO.getId(), certId);
			assertEquals(outputDTO.getName(), "new name");
			assertEquals(outputDTO.getDescription(), existing.getDescription());
			assertEquals(outputDTO.getDuration(), 40);
			assertEquals(outputDTO.getPrice(), 30.4);
			assertEquals(outputDTO.getTags().stream().allMatch(t -> update.getTags().contains(t.getName())), true);
		});

	}

	@Test
	public void updateCertificateNotFoundTest() {
		int certId = 1;
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		GiftCertificateRepository giftCertificateRepository = Mockito.mock(GiftCertificateRepository.class);
		GiftCertificateValidator giftCertificateValidator = Mockito.mock(GiftCertificateValidator.class);
		GiftCertificate existing = new GiftCertificate(certId, "old name", "old description", 3.2, 4, null, null);
		Optional opt = Optional.of(existing);
		Mockito.when(giftCertificateRepository.getCertificateById(Mockito.eq(certId))).thenReturn(Optional.empty());
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagRepository, giftCertificateRepository, tagValidator,
						giftCertificateValidator);
		GiftCertificateDTO dto = new GiftCertificateDTO(new ArrayList<>());
		dto.setId(certId);
		GiftCertificateNotFoundException ex =
				assertThrows(GiftCertificateNotFoundException.class, () -> service.updateCertificate(dto));
		assertEquals(ex.getId(), certId);
	}

	@Test
	public void getCertificatePositiveTest() {
		int certId = 1;
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		GiftCertificateRepository giftCertificateRepository = Mockito.mock(GiftCertificateRepository.class);
		GiftCertificateValidator giftCertificateValidator = Mockito.mock(GiftCertificateValidator.class);
		GiftCertificate existing = new GiftCertificate(certId, "old name", "old description", 3.2, 4, LocalDateTime.MIN,
				LocalDateTime.MAX);
		Mockito.when(giftCertificateRepository.getCertificateById(Mockito.eq(certId)))
				.thenReturn(Optional.of(existing));
		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag(1, "tagName"));
		Mockito.when(tagRepository.getTagsByCertificate(Mockito.eq(certId))).thenReturn(tags);
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagRepository, giftCertificateRepository, tagValidator,
						giftCertificateValidator);
		assertDoesNotThrow(() -> {
			GiftCertificateOutputDTO res = service.getCertificate(certId);
			assertEquals(res.getId(), existing.getId());
			assertEquals(res.getName(), existing.getName());
			assertEquals(res.getPrice(), existing.getPrice());
			assertEquals(res.getDuration(), existing.getDuration());
			assertEquals(res.getCreateDate(), existing.getCreateDate());
			assertEquals(res.getLastUpdateDate(), existing.getLastUpdateDate());
			assertEquals(res.getTags().size(), tags.size());
			for (int i = 0; i < tags.size(); i++) {
				assertEquals(res.getTags().get(i).getId(), tags.get(i).getId());
				assertEquals(res.getTags().get(i).getName(), tags.get(i).getName());
			}
		});
	}

	@Test
	public void getUnexistingTagTest() {
		int certId = 1;
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		GiftCertificateRepository giftCertificateRepository = Mockito.mock(GiftCertificateRepository.class);
		GiftCertificateValidator giftCertificateValidator = Mockito.mock(GiftCertificateValidator.class);
		Mockito.when(giftCertificateRepository.getCertificateById(Mockito.eq(certId))).thenReturn(Optional.empty());
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagRepository, giftCertificateRepository, tagValidator,
						giftCertificateValidator);
		GiftCertificateNotFoundException ex =
				assertThrows(GiftCertificateNotFoundException.class, () -> service.getCertificate(certId));
		assertEquals(ex.getId(), certId);
	}

	@Test
	public void deleteTagPositiveTest() {
		int certId = 1;
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		GiftCertificateRepository giftCertificateRepository = Mockito.mock(GiftCertificateRepository.class);
		GiftCertificateValidator giftCertificateValidator = Mockito.mock(GiftCertificateValidator.class);
		Mockito.when(giftCertificateRepository.getCertificateById(Mockito.eq(certId))).thenReturn(Optional.empty());
		Mockito.when(giftCertificateRepository.deleteCertificate(Mockito.eq(certId))).thenReturn(true);
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagRepository, giftCertificateRepository, tagValidator,
						giftCertificateValidator);
		assertDoesNotThrow(() -> service.deleteCertificate(certId));
	}

	@Test
	public void deleteUnexistingTagTest() {
		int certId = 1;
		TagValidator tagValidator = Mockito.mock(TagValidator.class);
		TagRepository tagRepository = Mockito.mock(TagRepository.class);
		GiftCertificateRepository giftCertificateRepository = Mockito.mock(GiftCertificateRepository.class);
		GiftCertificateValidator giftCertificateValidator = Mockito.mock(GiftCertificateValidator.class);
		Mockito.when(giftCertificateRepository.getCertificateById(Mockito.eq(certId))).thenReturn(Optional.empty());
		Mockito.when(giftCertificateRepository.deleteCertificate(Mockito.eq(certId))).thenReturn(false);
		GiftCertificateService service =
				new GiftCertificateServiceImpl(tagRepository, giftCertificateRepository, tagValidator,
						giftCertificateValidator);
		GiftCertificateNotFoundException ex =
				assertThrows(GiftCertificateNotFoundException.class, () -> service.deleteCertificate(certId));
		assertEquals(ex.getId(), certId);
	}
}
