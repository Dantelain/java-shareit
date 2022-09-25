package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


@Data
@Builder
public class ApprovedBookingDto {

    private Long id;
    private Item item;
    private User booker;
    private Status status;

}
