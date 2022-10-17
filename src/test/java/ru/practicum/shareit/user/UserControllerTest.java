package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.UserGenerator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class UserControllerTest {

    private UserController userController;
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserByIdOk() {
        when(userService.getUserById(anyLong())).thenAnswer(invocationOnMock -> {
            Long index = invocationOnMock.getArgument(0);
            return UserGenerator.getUserDto(index);
        });
        UserDto userDto = userController.getUserById(2L);
        assertThat(userDto).hasFieldOrPropertyWithValue("id", 2L);
        verify(userService).getUserById(2L);
    }

    @Test
    void getUserByIdFail() {
        doThrow(new NotFoundException("Пользователь не найден")).when(userService).getUserById(anyLong());
        final var ex = new NotFoundException("Пользователь не найден");
        final var thrown = assertThrows(NotFoundException.class, () -> userController.getUserById(4L));
        assertEquals(ex.getMessage(), thrown.getMessage());
        verify(userService).getUserById(4L);
    }

    @Test
    void getAllUserOk() {
        when(userService.getAllUser()).thenReturn(UserGenerator.getUsersDto(4));
        List<UserDto> userDtoList = userController.getAllUser();
        assertEquals(4, userDtoList.size());
        verify(userService, times(1)).getAllUser();
    }

    @Test
    void createUserOk() {
        when(userService.createUser(any(UserDto.class))).thenReturn(
                UserDto.builder()
                        .id(1L)
                        .name("testUserDto")
                        .email("testUserDto@user.com")
                        .build());
        UserDto userDto = UserDto.builder().name("testUserDto").build();
        UserDto userDtoCreate = userController.createUser(userDto);
        assertEquals(1L, userDtoCreate.getId());
        verify(userService).createUser(userDto);
    }

    @Test
    void editUserOk() {
        when(userService.editUser(anyLong(), any(UserDto.class))).thenAnswer(invocationOnMock -> {
            Long index = invocationOnMock.getArgument(0);
            UserDto userDto = invocationOnMock.getArgument(1);
            userDto.setId(index);
            return userDto;
        });
        UserDto userDto = UserGenerator.getUserDto(3L);
        userDto.setName("new Test Name");
        UserDto userDtoEdit = userController.editUser(3L, userDto);
        assertEquals(userDtoEdit.getName(), userDto.getName());
        verify(userService).editUser(3L, userDto);

    }

    @Test
    void deleteUserOk() {
        userController.deleteUser(1L);
        verify(userService).deleteUser(1L);
    }

}