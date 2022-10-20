package ru.practicum.shareit.utils;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class BookingGenerator {

    public static Booking getBooking(Long id) {
        return Booking.builder()
                .id(id)
                .item(ItemGenerator.getItem(id))
                .booker(UserGenerator.getUser(id + 10))
                .status(Status.WAITING)
                .dateStart(LocalDateTime.now().plusDays(4))
                .dateEnd(LocalDateTime.now().plusDays(5))
                .build();
    }

    public static List<Booking> getBookings(int count) {
        AtomicLong id = new AtomicLong(0L);
        return Arrays.stream(new Integer[count])
                .map((i) -> getBooking(id.incrementAndGet()))
                .collect(Collectors.toList());
    }

    public static CreateBookingDto getCreateBookingDto(Long id) {
        return BookingMapper.toCreateBookingDto(getBooking(id));
    }

    public static BookingDto getBookingDto(Long id) {
        return BookingMapper.toBookingDto(getBooking(id));
    }

    public static List<BookingDto> getBookingsDto(int i) {
        return getBookings(i).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}
