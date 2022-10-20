package ru.practicum.shareit.item;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.ItemGenerator;
import ru.practicum.shareit.utils.UserGenerator;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemDataJpaTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void beforeEach() {
        List<User> users = UserGenerator.getUsers(5);
        users.forEach(el -> {
            el.setId(null);
            testEntityManager.persist(el);
        });
        List<Item> items = ItemGenerator.getItems(5);
        items.forEach(el -> {
            el.setId(null);
            testEntityManager.persist(el);
        });
        testEntityManager.flush();
    }

    @Test
    void searchItemOk() {
        List<Item> itemList = itemRepository.searchText("Вещь #2");
        assertEquals(1, itemList.size());
        assertEquals(2L, itemList.get(0).getId());
        itemList = itemRepository.searchText("Описание вещи #3");
        assertEquals(1, itemList.size());
        assertEquals(3L, itemList.get(0).getId());
        testEntityManager.clear();
    }


    @Test
    void findByOwner() {
        List<Item> itemList = itemRepository.findByOwner(UserGenerator.getUser(2L), Sort.by(Sort.Direction.ASC, "id"));
        assertEquals(1, itemList.size());
        assertEquals(2L, itemList.get(0).getId());
    }

}
