package ru.practicum.shareit.utils;

import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentGenerator {

    public static Comment getComment() {
        return getComment(1L);
    }

    public static Comment getComment(Long id) {
        return Comment.builder()
                .id(id)
                .text("Text comment #" + id)
                .created(LocalDateTime.now())
                .author(UserGenerator.getUser(id))
                .item(ItemGenerator.getItem(id))
                .build();
    }

    public static List<Comment> getComments(int count) {
        var listOfComments = new ArrayList<Comment>();
        for (int i = 0; i < count; i++) {
            Long j = (long) (i + 1);
            listOfComments.add(getComment(j));
        }
        return listOfComments;
    }

    public static CommentDto getCommentDto(Long id) {
        return CommentMapper.toCommentDto(getComment(id));
    }


}
