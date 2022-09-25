package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItem;

import java.util.List;

@Data
@Builder
public class ItemBookingDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available; //статус о том, доступна или нет вещь для аренды
    private Long request; //ссылка на запрос другого пользователя по которому была создана вещь
    private BookingItem lastBooking;
    private BookingItem nextBooking;
    private List<CommentDto> comments;

}
