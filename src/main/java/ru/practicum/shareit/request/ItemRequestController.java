package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;


    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getItemRequestByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(required = false) @PositiveOrZero Integer from,
                                                  @RequestParam(required = false) @Positive Integer size) {
        if (from == null || size == null) {
            return new ArrayList<>();
        }
        return itemRequestService.getAllItemRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }

}
