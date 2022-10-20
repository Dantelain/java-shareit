package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ItemBookingDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available; //статус о том, доступна или нет вещь для аренды
    private Long request; //ссылка на запрос другого пользователя по которому была создана вещь
    private BookingItem lastBooking;
    private BookingItem nextBooking;
    private List<Comment> comments;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingItem {
        private Long id;
        private Long bookerId;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Comment {
        private Long id;
        private String text;
        private String authorName;
        private LocalDateTime created;
    }
}
