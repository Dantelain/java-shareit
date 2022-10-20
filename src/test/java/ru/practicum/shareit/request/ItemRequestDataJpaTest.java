package ru.practicum.shareit.request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.ItemRequestGenerator;
import ru.practicum.shareit.utils.UserGenerator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestDataJpaTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void beforeEach() {
        List<User> users = UserGenerator.getUsers(5);
        users.forEach(el -> {
            el.setId(null);
            testEntityManager.persist(el);
        });
        List<ItemRequest> itemRequests = ItemRequestGenerator.getItemRequests(5);
        itemRequests.forEach(el -> {
            el.setId(null);
            testEntityManager.persist(el);
        });
        testEntityManager.flush();
    }

    @Test
    void findAllByAuthorRequestOk() {
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByAuthorRequest(UserGenerator.getUser(2L),Sort.by(Sort.Direction.DESC, "created"));
        assertEquals(1, itemRequestList.size());
        assertEquals(2L, itemRequestList.get(0).getId());
        testEntityManager.clear();
    }

}
