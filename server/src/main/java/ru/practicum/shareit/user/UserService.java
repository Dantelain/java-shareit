package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long userId);

    List<UserDto> getAllUser();

    UserDto createUser(UserDto userDto);

    UserDto editUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);

}
