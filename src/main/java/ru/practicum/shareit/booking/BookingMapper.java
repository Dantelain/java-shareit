package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItem;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {

    public static Booking toBooking(CreateBookingDto createBookingDto) {
        return Booking.builder()
                .id(createBookingDto.getId())
                .dateStart(createBookingDto.getStart())
                .dateEnd(createBookingDto.getEnd())
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getDateStart())
                .end(booking.getDateEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static BookingItem toBookingItem(Booking booking) {
        return BookingItem.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static CreateBookingDto toCreateBookingDto(Booking booking) {
        return CreateBookingDto.builder()
                .id(booking.getId())
                .start(booking.getDateStart())
                .end(booking.getDateEnd())
                .itemId(booking.getItem().getId())
                .build();
    }

}
