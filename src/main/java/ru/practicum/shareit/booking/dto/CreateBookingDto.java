package ru.practicum.shareit.booking.dto;


import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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
