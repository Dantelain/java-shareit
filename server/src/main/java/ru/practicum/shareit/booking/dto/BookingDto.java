package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Booker booker;
    private Status status;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Booker {
        private Long id;
    }
}
