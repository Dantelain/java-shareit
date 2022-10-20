package ru.practicum.shareit.booking;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody CreateBookingDto createBookingDto) {
        return bookingService.createBooking(userId, createBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long bookingId,
                                              @RequestParam Boolean approved) {
        return bookingService.approvedBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(required = false, defaultValue = "ALL") String state,
                                          @RequestParam(required = false) @PositiveOrZero Integer from,
                                          @RequestParam(required = false) @Positive Integer size) {
        if (from == null || size == null) {
            return bookingService.getBookingAll(userId, state, false);
        } else {
            return bookingService.getBookingAll(userId, state, false, from, size);
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingOwnerAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(required = false, defaultValue = "ALL") String state,
                                               @RequestParam(required = false) Integer from,
                                               @RequestParam(required = false) Integer size) {
        if (from != null && from < 0) {
            throw new ValidationException("Параметр from указан не верно", from.toString());
        }
        if (size != null && size <= 0) {
            throw new ValidationException("Параметр size указан не верно", size.toString());
        }
        if (from == null || size == null) {
            return bookingService.getBookingAll(userId, state, true);
        } else {
            return bookingService.getBookingAll(userId, state, true, from, size);
        }
    }
}
