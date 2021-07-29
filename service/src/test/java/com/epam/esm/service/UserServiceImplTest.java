package com.epam.esm.service;

import com.epam.esm.db.UserRepository;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.User;
import com.epam.esm.service.converter.PagedResultConverter;
import com.epam.esm.service.converter.UserConverter;
import com.epam.esm.service.exception.InvalidUserException;
import com.epam.esm.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceImplTest {
    private static final int ID = -1;
    private static final UserDTO existingUserDto1 = UserDTO.builder()
            .id(999)
            .userName("user").build();
    private static final User existingUser1 = User.builder()
            .id(existingUserDto1.getId())
            .userName(existingUserDto1.getUserName()).build();
    private static final UserDTO existingUserDto2 = UserDTO.builder()
            .id(919)
            .userName("user2").build();
    private static final User existingUser2 = User.builder()
            .id(existingUserDto2.getId())
            .userName(existingUserDto2.getUserName()).build();
    private static final List<User> allExistingUserList = List.of(existingUser1, existingUser2);
    private static final PagedResult<User> userPage = PagedResult.<User>builder()
            .page(allExistingUserList).build();
    private static final List<UserDTO> allExistingUserDtoList = List.of(existingUserDto1, existingUserDto2);
    private static final PagedResultDTO<UserDTO> userPageDto = PagedResultDTO.<UserDTO>builder()
			.page(allExistingUserDtoList).build();
    private static final UserDTO nonExistingUserDto = UserDTO.builder()
            .id(-10)
            .userName("non existent").build();
    private static final String USER_NAME_2 = "user name 2";
    private static final String ALREADY_EXISTS_MESSAGE_FIELD_NAME = "alreadyExistsExceptionTemplate";
    private static final String NOT_FOUND_MESSAGE_FIELD_NAME = "notFoundExceptionTemplate";
    private static final String MOCK_EX_MESSAGE = "test";
    private static final int EXISTING_CERT_ID = 1;
    private static final int NON_EXISTING_CERT_ID = 1;
    private static final PageDTO pageDTO = PageDTO.builder().pageNumber(1).pageSize(5).build();
    private final UserConverter userConverter = Mappers.getMapper(UserConverter.class);
    private final PagedResultConverter pagedResultConverter = Mappers.getMapper(PagedResultConverter.class);

    @Test
    public void getUserShouldReturnDtoWhenPassedExistingUserId() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.getUserById(Mockito.eq(existingUserDto1.getId()))).thenReturn(Optional.of(existingUser1));
        UserService userService = new UserServiceImpl(userRepository, userConverter, pagedResultConverter);
        assertDoesNotThrow(() -> {
            UserDTO res = userService.getUser(existingUserDto1.getId());
            assertEquals(existingUserDto1, res);
        });
    }

    @Test
    public void getUserShouldThrowExceptionWhenPassedNonExistingUserId() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.getUserById(Mockito.eq(nonExistingUserDto.getId()))).thenReturn(Optional.empty());
        UserService userService = new UserServiceImpl(userRepository, userConverter, pagedResultConverter);
        ReflectionTestUtils.setField(userService, NOT_FOUND_MESSAGE_FIELD_NAME, MOCK_EX_MESSAGE, String.class);
        InvalidUserException ex =
				assertThrows(InvalidUserException.class, () -> userService.getUser(existingUserDto1.getId()));
        assertEquals(InvalidUserException.Reason.NOT_FOUND, ex.getReason());
    }

    @Test
    public void getAllUsersShouldNotThrowExceptionAndReturnList() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.getAllUsers(Mockito.eq(pageDTO.getOffset()), Mockito.eq(pageDTO.getPageSize())))
				.thenReturn(userPage);
        UserService userService = new UserServiceImpl(userRepository, userConverter, pagedResultConverter);
        assertDoesNotThrow(() -> {
            PagedResultDTO<UserDTO> userDTOPagedResult = userService.getAllUsers(pageDTO);
            assertEquals(userPageDto, userDTOPagedResult);
        });
    }
}
