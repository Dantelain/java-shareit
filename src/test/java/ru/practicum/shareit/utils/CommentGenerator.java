package ru.practicum.shareit.utils;

import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class CommentGenerator {

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
        AtomicLong id = new AtomicLong(0L);
        return Arrays.stream(new Integer[count])
                .map((i) -> getComment(id.incrementAndGet()))
                .collect(Collectors.toList());
    }

    public static CommentDto getCommentDto(Long id) {
        return CommentMapper.toCommentDto(getComment(id));
    }


}
