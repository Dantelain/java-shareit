package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> userMap;
    private final Set<String> emailSet;
    private Long counter;

    public UserRepositoryImpl() {
        this.userMap = new HashMap<>();
        this.emailSet = new HashSet<>();
        this.counter = 1L;
    }

    @Override
    public User getUserById(Long userId) {
        User user = userMap.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }

    @Override
    public List<User> getAllUser() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User createUser(User user) {
        if (user.getEmail() == null) {
            user.setEmail(user.getName() + "@user.com");
        }
        if (emailSet.contains(user.getEmail())) {
            throw new BadRequestException("почта уже используется", user.getEmail());
        }
        user.setId(counter);
        userMap.put(counter, user);
        counter++;
        emailSet.add(user.getEmail());
        return user;
    }

    @Override
    public User editUser(Long userId, User userNew) {
        if (emailSet.contains(userNew.getEmail())) {
            throw new BadRequestException("почта уже используется", userNew.getEmail());
        }
        User user = userMap.get(userId);
        String email = user.getEmail();
        if (userNew.getName() != null) {
            user.setName(userNew.getName());
        }
        if (userNew.getEmail() != null) {
            user.setEmail(userNew.getEmail());
            emailSet.remove(email);
            emailSet.add(userNew.getEmail());
        }
        userMap.put(userId, user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        emailSet.remove(userMap.get(userId).getEmail());
        userMap.remove(userId);
    }

}
