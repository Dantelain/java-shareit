package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null) {
            userDto.setEmail(userDto.getName() + "@user.com");
        }
        User user = userRepository.saveAndFlush(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto editUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        User userNew = UserMapper.toUser(userDto);
        if (userNew.getName() != null) user.setName(userNew.getName());
        if (userNew.getEmail() != null) user.setEmail(userNew.getEmail());
        return UserMapper.toUserDto(userRepository.saveAndFlush(user));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}
