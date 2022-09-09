package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService{

    private final Map<Long, User> userMap;
    private final Set<String> emailSet;
    private Long counter;

    public UserServiceImpl() {
        this.userMap = new HashMap<>();
        this.emailSet = new HashSet<>();
        this.counter = 1L;
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userMap.get(userId);
        if (user != null) {
            return UserMapper.toUserDto(user);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public List<UserDto> getAllUser() {
        return userMap.values().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null)
            userDto.setEmail(userDto.getName() + "@user.com");
        if (emailSet.contains(userDto.getEmail()))
            throw new BadRequestException("почта уже используется", userDto.getEmail());
        userDto.setId(counter);
        User user = UserMapper.toUser(userDto);
        userMap.put(counter,user);
        counter++;
        emailSet.add(user.getEmail());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto editUser(Long userId, UserDto userDto) {
        if (emailSet.contains(userDto.getEmail()))
            throw new BadRequestException("почта уже используется", userDto.getEmail());
        User user = userMap.get(userId);
        String email = user.getEmail();
        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
            emailSet.remove(email);
            emailSet.add(user.getEmail());
        }
        userMap.put(userId,user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        emailSet.remove(userMap.get(userId).getEmail());
        userMap.remove(userId);
    }
}
