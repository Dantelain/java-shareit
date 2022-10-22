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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.UserGenerator;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class UserServiceImplTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserGenerator.getUser()));
        UserDto userDto = userService.getUserById(1L);
        assertThat(userDto).hasFieldOrPropertyWithValue("id", 1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void findByIdFail() {
        when(userRepository.findById(anyLong())).thenThrow(new NotFoundException("Пользователь не найден"));
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void getAllUser() {
        when(userRepository.findAll()).thenReturn(UserGenerator.getUsers(5));
        List<UserDto> userDtoList = userService.getAllUser();
        assertEquals(5, userDtoList.size());
        verify(userRepository).findAll();
    }

    @Test
    void createUserOkWithoutEmail() {
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocationOnMock -> {
            User user = invocationOnMock.getArgument(0);
            user.setId(1L);
            return user;
        });
        UserDto userDto = UserDto.builder().name("test User").build();
        UserDto userDtoCreate = userService.createUser(userDto);
        assertEquals(1L, userDtoCreate.getId());
        assertEquals("test User@user.com", userDtoCreate.getEmail());
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void createUserOkWithEmail() {
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocationOnMock -> {
            User user = invocationOnMock.getArgument(0);
            user.setId(1L);
            return user;
        });
        UserDto userDto = UserDto.builder().name("test User").email("user@user.com").build();
        UserDto userDtoCreate = userService.createUser(userDto);
        assertEquals(1L, userDtoCreate.getId());
        assertEquals("user@user.com", userDtoCreate.getEmail());
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void editUserOk() {
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.<User>getArgument(0));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserGenerator.getUser(2L)));
        UserDto userDto = UserGenerator.getUserDto(2L);
        UserDto userDtoTwo = UserGenerator.getUserDto(2L);
        userDtoTwo.setName("newName");
        userDtoTwo.setEmail("newName@email.com");
        UserDto userDtoEdit = userService.editUser(2L, userDtoTwo);
        assertNotEquals(userDto.getName(), userDtoEdit.getName());
        assertNotEquals(userDto.getEmail(), userDtoEdit.getEmail());
        assertEquals(userDto.getId(), userDtoEdit.getId());
        verify(userRepository).findById(anyLong());
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void editUserWithoutNameAndEmailOk() {
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.<User>getArgument(0));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserGenerator.getUser(2L)));
        UserDto userDto = UserGenerator.getUserDto(2L);
        UserDto userDtoTwo = UserGenerator.getUserDto(2L);
        userDtoTwo.setName(null);
        userDtoTwo.setEmail(null);
        UserDto userDtoEdit = userService.editUser(2L, userDtoTwo);
        assertEquals(userDto.getName(), userDtoEdit.getName());
        assertEquals(userDto.getEmail(), userDtoEdit.getEmail());
        assertEquals(userDto.getId(), userDtoEdit.getId());
        verify(userRepository).findById(anyLong());
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void editUserFail() {
        when(userRepository.findById(anyLong())).thenThrow(new NotFoundException("Пользователь не найден"));
        UserDto userDto = UserGenerator.getUserDto(2L);
        assertThrows(NotFoundException.class, () -> userService.editUser(2L, userDto));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void deleteUserOk() {
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

}