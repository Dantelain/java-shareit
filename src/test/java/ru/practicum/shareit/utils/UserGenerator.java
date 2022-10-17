package ru.practicum.shareit.utils;

import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserGenerator {

    public static User getUser() {
        return getUser(1L);
    }

    public static User getUser(Long userId) {
        return User.builder()
                .id(userId)
                .name("test Name" + userId)
                .email("test" + userId + "@email.com")
                .build();
    }

    public static List<User> getUsers(int count) {
        var listOfUsers = new ArrayList<User>();
        for (int i = 0; i < count; i++) {
            Long j = (long) (i + 1);
            listOfUsers.add(getUser(j));
        }
        return listOfUsers;
    }

    public static UserDto getUserDto() {
        return UserMapper.toUserDto(getUser(1L));
    }

    public static UserDto getUserDto(Long userId) {
        return UserMapper.toUserDto(getUser(userId));
    }

    public static List<UserDto> getUsersDto(int count) {
        return getUsers(count).stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

}
