package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.UserGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserTest {

    @Test
    void userModelTest() {
        User user1 = UserGenerator.getUser(1L);
        User user2 = UserGenerator.getUser(1L);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertEquals(user1, user2);
        assertEquals(user1, user1);
        assertNotEquals(user1, null);
        user1.setId(null);
        assertNotEquals(user1, user2);
        user2.setId(2L);
        assertNotEquals(user1, user2);
    }

}
