package ru.practicum.shareit.booking.dto;


import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@Builder
public class CreateBookingDto {

    private Long id;
    @NonNull
    @FutureOrPresent
    private LocalDateTime start;
    @NonNull
    private LocalDateTime end;
    private Long itemId;

}
