package ru.practicum.shareit.user;


import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User getUserById(Long userId);

    List<User> getAllUser();

    User createUser(User user);

    User editUser(Long userId, User userNew);

    void deleteUser(Long userId);

}
