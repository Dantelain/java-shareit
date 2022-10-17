package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.utils.CommentGenerator;
import ru.practicum.shareit.utils.ItemGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ItemTest {

    @Test
    void itemModelTest() {
        Item item1 = ItemGenerator.getItem(1L);
        Item item2 = ItemGenerator.getItem(1L);
        assertEquals(item1.hashCode(), item2.hashCode());
        assertEquals(item1, item2);
        assertEquals(item1, item1);
        item2.setId(2L);
        assertNotEquals(item1, item2);
        assertNotEquals(item1, null);
        item1.setId(null);
        assertNotEquals(item1, item2);
        item2.setId(1L);
        assertNotEquals(item1, item2);
    }

    @Test
    void commentModelTest() {
        Comment comment1 = CommentGenerator.getComment(1L);
        Comment comment2 = CommentGenerator.getComment(1L);
        assertEquals(comment1.hashCode(), comment2.hashCode());
        assertEquals(comment1, comment2);
        assertEquals(comment1, comment1);
        comment2.setId(2L);
        assertNotEquals(comment1, comment2);
        assertNotEquals(comment1, null);
        comment1.setId(null);
        assertNotEquals(comment1, comment2);
        comment2.setId(1L);
        assertNotEquals(comment1, comment2);
    }

}
