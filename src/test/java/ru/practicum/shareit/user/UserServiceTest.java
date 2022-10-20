package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.UserGenerator;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Rollback
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void getByIdFail() {
        UserDto userDto = UserGenerator.getUserDto(2L);
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
        assertThrows(NotFoundException.class, () -> userService.editUser(2L, userDto));
    }

}
