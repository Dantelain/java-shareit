package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.MyError;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;


    @GetMapping
    public ResponseEntity<Object> getListItemByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getListItemByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            MyError error = new MyError("Название вещи не может быть пустым.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            MyError error = new MyError("Описание вещи не может быть.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        if (itemDto.getAvailable() == null) {
            MyError error = new MyError("Доступность вещи не может быть пустой.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable Long itemId,
                            @RequestBody ItemDto itemDto) {
        return itemClient.editItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam(required = false) String text) {
        return itemClient.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId, @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
