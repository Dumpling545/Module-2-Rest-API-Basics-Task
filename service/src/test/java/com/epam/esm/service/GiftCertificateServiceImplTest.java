package com.epam.esm.service;


import com.epam.esm.db.GiftCertificateRepository;
import com.epam.esm.model.dto.GiftCertificateCreateDTO;
import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.GiftCertificateSearchFilterDTO;
import com.epam.esm.model.dto.GiftCertificateUpdateDTO;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.service.converter.GiftCertificateConverter;
import com.epam.esm.service.converter.GiftCertificateSearchFilterConverter;
import com.epam.esm.service.converter.PagedResultConverter;
import com.epam.esm.service.converter.TagConverter;
import com.epam.esm.service.exception.InvalidCertificateException;
import com.epam.esm.service.impl.GiftCertificateServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.EMPTY_SET;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    private static final BigDecimal CERT_PRICE = BigDecimal.valueOf(12.3);
    private static final int CERT_DURATION = 3;
    private static final LocalDateTime CERT_CREATE_DATE = LocalDateTime.MIN;
    private static final LocalDateTime CERT_LAST_UPDATE_DATE = LocalDateTime.MAX;
    private static final String NOT_FOUND_MESSAGE_FIELD_NAME = "notFoundExceptionTemplate";
    private static final String MOCK_EX_MESSAGE = "test";
    private static final String CERT_NEW_NAME = "new cert name";

    private static final Tag newTag1 = Tag.builder().name("tag 1000").build();
    private static final Tag newTag2 = Tag.builder().name("tag 2000").build();
    private static final TagDTO newTagDTO1 = TagDTO.builder()
            .name(newTag1.getName()).build();
    private static final TagDTO newTagDTO2 = TagDTO.builder()
            .name(newTag2.getName()).build();

    private static final Tag existingTag1 = Tag.builder().id(1).name("tag 1").build();
    private static final Tag existingTag2 = Tag.builder().id(2).name("tag 2").build();
    private static final TagDTO existingTagDTO1 = TagDTO.builder()
            .id(existingTag1.getId())
            .name(existingTag1.getName()).build();
    private static final TagDTO existingTagDTO2 = TagDTO.builder()
            .id(existingTag2.getId())
            .name(existingTag2.getName()).build();

    private static final GiftCertificate existingCert = GiftCertificate.builder()
            .id(1)
            .name("name")
            .description("desc")
            .duration(4)
            .price(BigDecimal.valueOf(1.1))
            .createDate(LocalDateTime.MIN)
            .lastUpdateDate(LocalDateTime.MAX)
            .tags(EMPTY_SET).build();
    private static final GiftCertificateOutputDTO existingCertOutputDto = GiftCertificateOutputDTO.builder()
            .id(existingCert.getId())
            .name(existingCert.getName())
            .description(existingCert.getDescription())
            .duration(existingCert.getDuration())
            .price(existingCert.getPrice())
            .createDate(existingCert.getCreateDate())
            .lastUpdateDate(existingCert.getLastUpdateDate())
            .tags(EMPTY_SET).build();
    private static final GiftCertificateUpdateDTO certUpdateDto = GiftCertificateUpdateDTO.builder()
            .name("cert name")
            .description("cert desc")
            .duration(3)
            .price(BigDecimal.valueOf(12.3))
            .tagNames(null).build();

    private static final GiftCertificateCreateDTO certCreateDtoWithoutTags = GiftCertificateCreateDTO.builder()
            .name("cert name")
            .description("cert desc")
            .duration(3)
            .price(BigDecimal.valueOf(12.3))
            .tagNames(null).build();
    private static final long EXISTING_TAGS_FROM_CERT_CREATE_DTO_WITHOUT_TAGS = 0;
    private static final long NEW_TAGS_FROM_CERT_CREATE_DTO_WITHOUT_TAGS = 0;
    private static final GiftCertificateCreateDTO certCreateDtoWithEmptyTagSet = GiftCertificateCreateDTO.builder()
            .name("cert name2")
            .description("cert desc2")
            .duration(321)
            .price(BigDecimal.valueOf(1.23))
            .tagNames(EMPTY_SET).build();
    private static final long EXISTING_TAGS_FROM_CERT_CREATE_DTO_WITH_EMPTY_TAG_SET = 0;
    private static final long NEW_TAGS_FROM_CERT_CREATE_DTO_WITH_EMPTY_TAG_SET = 0;
    private static final GiftCertificateCreateDTO certCreateDtoWithTwoExistingTags = GiftCertificateCreateDTO.builder()
            .name("cert name3")
            .description("cert desc3")
            .duration(32)
            .price(BigDecimal.valueOf(11221.3))
            .tagNames(Set.of(existingTag1.getName(), existingTag2.getName())).build();
    private static final long EXISTING_TAGS_FROM_CERT_CREATE_DTO_WITH_TWO_EXISTING_TAGS = 2;
    private static final long NEW_TAGS_FROM_CERT_CREATE_DTO_WITH_TWO_EXISTING_TAGS = 0;
    private static final GiftCertificateCreateDTO certCreateDtoWithTwoNewTags = GiftCertificateCreateDTO.builder()
            .name("cert name4")
            .description("cert desc4")
            .duration(310)
            .price(BigDecimal.valueOf(221.31))
            .tagNames(Set.of(newTag1.getName(), newTag2.getName())).build();
    private static final long EXISTING_TAGS_FROM_CERT_CREATE_DTO_WITH_TWO_NEW_TAGS = 0;
    private static final long NEW_TAGS_FROM_CERT_CREATE_DTO_WITH_TWO_NEW_TAGS = 2;
    private static final GiftCertificateCreateDTO certCreateDtoWithTwoExistingAndTwoNewTags =
            GiftCertificateCreateDTO.builder()
                    .name("cert name4")
                    .description("cert desc4")
                    .duration(310)
                    .price(BigDecimal.valueOf(221.31))
                    .tagNames(Set.of(newTag1.getName(), newTag2.getName(), existingTag1.getName(),
                            existingTag2.getName())).build();
    private static final long EXISTING_TAGS_FROM_CERT_CREATE_DTO_WITH_TWO_EXISTING_AND_TWO_NEW_TAGS = 2;
    private static final long NEW_TAGS_FROM_CERT_CREATE_DTO_WITH_TWO_EXISTING_AND_TWO_NEW_TAGS = 2;
    private static final GiftCertificateSearchFilterDTO giftCertificateSearchFilterDTO = GiftCertificateSearchFilterDTO
            .builder().build();
    private static final PagedResult<GiftCertificate> pagedResult = PagedResult.<GiftCertificate>builder()
            .page(List.of(existingCert))
            .first(false)
            .last(true).build();
    private static final PagedResultDTO<GiftCertificateOutputDTO> pagedResultDto =
            PagedResultDTO.<GiftCertificateOutputDTO>builder()
                    .page(List.of(existingCertOutputDto))
                    .first(pagedResult.isFirst())
                    .last(pagedResult.isLast()).build();
    private static final PageDTO pageDTO = PageDTO.builder().pageNumber(1).pageSize(5).build();
    private final GiftCertificateConverter giftCertificateConverter =
            Mappers.getMapper(GiftCertificateConverter.class);
    private final GiftCertificateSearchFilterConverter filterConverter =
            Mappers.getMapper(GiftCertificateSearchFilterConverter.class);
    private final TagConverter tagConverter = Mappers.getMapper(TagConverter.class);
    private final PagedResultConverter pagedResultConverter = Mappers.getMapper(PagedResultConverter.class);

    {
        ReflectionTestUtils.setField(giftCertificateConverter, "tagConverter", tagConverter, TagConverter.class);
    }

    public Stream<Arguments> createTestSources() {
        TagService tagServiceWithEmptyNameSet = Mockito.mock(TagService.class);
        TagService tagServiceReturningTwoExistingTagsSet = Mockito.mock(TagService.class);
        Mockito.when(tagServiceReturningTwoExistingTagsSet.getTagsFromNameSet(Mockito.anySet()))
                .thenReturn(Set.of(existingTagDTO1, existingTagDTO2));
        return Stream.<Arguments>of(Arguments.of(tagServiceWithEmptyNameSet,
                certCreateDtoWithoutTags,
                EXISTING_TAGS_FROM_CERT_CREATE_DTO_WITHOUT_TAGS,
                NEW_TAGS_FROM_CERT_CREATE_DTO_WITHOUT_TAGS),
                Arguments.of(tagServiceWithEmptyNameSet,
                        certCreateDtoWithEmptyTagSet,
                        EXISTING_TAGS_FROM_CERT_CREATE_DTO_WITH_EMPTY_TAG_SET,
                        NEW_TAGS_FROM_CERT_CREATE_DTO_WITH_EMPTY_TAG_SET),
                Arguments.of(tagServiceReturningTwoExistingTagsSet,
                        certCreateDtoWithTwoExistingTags,
                        EXISTING_TAGS_FROM_CERT_CREATE_DTO_WITH_TWO_EXISTING_TAGS,
                        NEW_TAGS_FROM_CERT_CREATE_DTO_WITH_TWO_EXISTING_TAGS),
                Arguments.of(tagServiceWithEmptyNameSet,
                        certCreateDtoWithTwoNewTags,
                        EXISTING_TAGS_FROM_CERT_CREATE_DTO_WITH_TWO_NEW_TAGS,
                        NEW_TAGS_FROM_CERT_CREATE_DTO_WITH_TWO_NEW_TAGS),
                Arguments.of(tagServiceReturningTwoExistingTagsSet,
                        certCreateDtoWithTwoExistingAndTwoNewTags,
                        EXISTING_TAGS_FROM_CERT_CREATE_DTO_WITH_TWO_EXISTING_AND_TWO_NEW_TAGS,
                        NEW_TAGS_FROM_CERT_CREATE_DTO_WITH_TWO_EXISTING_AND_TWO_NEW_TAGS));
    }

    public Stream<Arguments> updateTestSources() {
        Stream<Arguments> stream = createTestSources();
        return stream.peek(args -> {
            Object[] array = args.get();
            GiftCertificateCreateDTO createDTO = (GiftCertificateCreateDTO) array[1];
            array[1] = GiftCertificateUpdateDTO.builder()
                    .name(createDTO.getName())
                    .description(createDTO.getDescription())
                    .duration(createDTO.getDuration())
                    .price(createDTO.getPrice())
                    .tagNames(createDTO.getTagNames()).build();
        });
    }

    @ParameterizedTest
    @MethodSource("createTestSources")
    public void createCertificateShouldNotThrowExceptionWhenPassedCorrectDto(TagService tagService,
                                                                             GiftCertificateCreateDTO dto,
                                                                             long expectedExistingTagsAdded,
                                                                             long expectedNewTagsAdded) {
        GiftCertificateRepository gcRepository = Mockito.mock(GiftCertificateRepository.class);
        Mockito.when(gcRepository.createCertificate(Mockito.any())).then(answer -> {
            GiftCertificate newCert = answer.getArgument(0);
            Random random = new Random();
            return GiftCertificate.builder()
                    .id(random.nextInt())
                    .name(newCert.getName())
                    .description(newCert.getDescription())
                    .duration(newCert.getDuration())
                    .price(newCert.getPrice())
                    .tags(newCert.getTags()).build();
        });
        GiftCertificateService service = new GiftCertificateServiceImpl(tagService,
                gcRepository, giftCertificateConverter, tagConverter, filterConverter, pagedResultConverter);
        assertDoesNotThrow(() -> {
            GiftCertificateOutputDTO outputDTO = service.createCertificate(dto);
            if (dto.getTagNames() != null) {
                long actualExistingTagsAdded = outputDTO.getTags().stream().filter(t -> t.getId() != null).count();
                long actualNewTagsAdded = outputDTO.getTags().stream().filter(t -> t.getId() == null).count();
                assertEquals(expectedExistingTagsAdded, actualExistingTagsAdded);
                assertEquals(expectedNewTagsAdded, actualNewTagsAdded);
            }
        });
    }

    @ParameterizedTest
    @MethodSource("updateTestSources")
    public void updateCertificateShouldNotThrowExceptionWhenPassedCorrectDto(TagService tagService,
                                                                             GiftCertificateUpdateDTO dto,
                                                                             long expectedExistingTagsAdded,
                                                                             long expectedNewTagsAdded) {
        GiftCertificateRepository gcRepository = Mockito.mock(GiftCertificateRepository.class);
        Mockito.when(gcRepository.getCertificateById(Mockito.eq(existingCert.getId())))
                .thenReturn(Optional.of(existingCert));
        Mockito.doAnswer(ans -> {
            GiftCertificate gc = ans.getArgument(0);
            long actualExistingTagsAdded = gc.getTags().stream().filter(t -> t.getId() != null).count();
            long actualNewTagsAdded = gc.getTags().stream().filter(t -> t.getId() == null).count();
            assertEquals(expectedExistingTagsAdded, actualExistingTagsAdded);
            assertEquals(expectedNewTagsAdded, actualNewTagsAdded);
            return null;
        }).when(gcRepository).updateCertificate(Mockito.any());
        GiftCertificateService service = new GiftCertificateServiceImpl(tagService,
                gcRepository, giftCertificateConverter, tagConverter, filterConverter, pagedResultConverter);
        ReflectionTestUtils.setField(service, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
        assertDoesNotThrow(() -> {
            service.updateCertificate(existingCert.getId(), dto);
        });
    }

    @Test
    public void updateCertificateShouldThrowExceptionWhenPassedNonExistingId() {
        GiftCertificateRepository gcRepository = Mockito.mock(GiftCertificateRepository.class);
        TagService tagService = Mockito.mock(TagService.class);
        Mockito.when(gcRepository.getCertificateById(Mockito.eq(NON_EXISTING_ID))).thenReturn(Optional.empty());
        GiftCertificateService service = new GiftCertificateServiceImpl(tagService,
                gcRepository, giftCertificateConverter, tagConverter, filterConverter, pagedResultConverter);
        ReflectionTestUtils.setField(service, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
        InvalidCertificateException ex = assertThrows(InvalidCertificateException.class,
                () -> service.updateCertificate(NON_EXISTING_ID, certUpdateDto));
        assertEquals(InvalidCertificateException.Reason.NOT_FOUND, ex.getReason());
    }

    @Test
    public void getCertificateShouldReturnDtoWhenPassedExistingId() {
        GiftCertificateRepository gcRepository = Mockito.mock(GiftCertificateRepository.class);
        TagService tagService = Mockito.mock(TagService.class);
        Mockito.when(gcRepository.getCertificateById(Mockito.eq(existingCert.getId())))
                .thenReturn(Optional.of(existingCert));
        GiftCertificateService service = new GiftCertificateServiceImpl(tagService,
                gcRepository, giftCertificateConverter, tagConverter, filterConverter, pagedResultConverter);
        assertDoesNotThrow(() -> {
            GiftCertificateOutputDTO outputDTO = service.getCertificate(existingCert.getId());
            assertEquals(existingCertOutputDto, outputDTO);
        });
    }

    @Test
    public void getCertificateShouldThrowExceptionWhenPassedNonExistingId() {
        GiftCertificateRepository gcRepository = Mockito.mock(GiftCertificateRepository.class);
        TagService tagService = Mockito.mock(TagService.class);
        Mockito.when(gcRepository.getCertificateById(Mockito.eq(NON_EXISTING_ID))).thenReturn(Optional.empty());
        GiftCertificateService service = new GiftCertificateServiceImpl(tagService,
                gcRepository, giftCertificateConverter, tagConverter, filterConverter, pagedResultConverter);
        ReflectionTestUtils.setField(service, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
        InvalidCertificateException ex = assertThrows(InvalidCertificateException.class,
                () -> service.getCertificate(NON_EXISTING_ID));
        assertEquals(InvalidCertificateException.Reason.NOT_FOUND, ex.getReason());
    }

    @Test
    public void deleteCertificateShouldNotThrowExceptionWhenPassedExistingId() {
        GiftCertificateRepository gcRepository = Mockito.mock(GiftCertificateRepository.class);
        TagService tagService = Mockito.mock(TagService.class);
        Mockito.when(gcRepository.deleteCertificate(Mockito.eq(existingCert.getId()))).thenReturn(true);
        GiftCertificateService service = new GiftCertificateServiceImpl(tagService,
                gcRepository, giftCertificateConverter, tagConverter, filterConverter, pagedResultConverter);
        assertDoesNotThrow(() -> service.deleteCertificate(existingCert.getId()));
    }

    @Test
    public void deleteCertificateShouldThrowExceptionWhenPassedNonExistingId() {
        GiftCertificateRepository gcRepository = Mockito.mock(GiftCertificateRepository.class);
        TagService tagService = Mockito.mock(TagService.class);
        Mockito.when(gcRepository.deleteCertificate(Mockito.eq(NON_EXISTING_ID))).thenReturn(false);
        GiftCertificateService service = new GiftCertificateServiceImpl(tagService,
                gcRepository, giftCertificateConverter, tagConverter, filterConverter, pagedResultConverter);
        ReflectionTestUtils.setField(service, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
        InvalidCertificateException ex = assertThrows(InvalidCertificateException.class,
                () -> service.deleteCertificate(NON_EXISTING_ID));
        assertEquals(InvalidCertificateException.Reason.NOT_FOUND, ex.getReason());
    }

    @Test
    public void getCertificatesShouldReturnPagedResultWhenPassedCorrectFilter() {
        GiftCertificateRepository gcRepository = Mockito.mock(GiftCertificateRepository.class);
        TagService tagService = Mockito.mock(TagService.class);
        Mockito.when(gcRepository.getCertificatesByFilter(Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(pagedResult);
        GiftCertificateService service = new GiftCertificateServiceImpl(tagService,
                gcRepository, giftCertificateConverter, tagConverter, filterConverter, pagedResultConverter);
        assertDoesNotThrow(() -> {
            PagedResultDTO<GiftCertificateOutputDTO> outputDTO = service.getCertificates(giftCertificateSearchFilterDTO,
                    pageDTO);
            assertEquals(pagedResultDto, outputDTO);
        });
    }
}
