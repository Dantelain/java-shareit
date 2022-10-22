package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBookingDto;

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
                .item(getItem(booking))
                .booker(getBooker(booking))
                .status(booking.getStatus())
                .build();
    }

    public static ItemBookingDto.BookingItem toBookingItem(Booking booking) {
        return ItemBookingDto.BookingItem.builder()
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

    private static BookingDto.Item getItem(Booking booking) {
        return BookingDto.Item.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .description(booking.getItem().getDescription())
                .available(booking.getItem().getAvailable())
                .build();
    }

    private static BookingDto.Booker getBooker(Booking booking) {
        return BookingDto.Booker.builder()
                .id(booking.getBooker().getId())
                .build();
    }
}
