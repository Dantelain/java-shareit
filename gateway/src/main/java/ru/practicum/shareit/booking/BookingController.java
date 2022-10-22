package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.MyError;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody CreateBookingDto createBookingDto) {
        log.info("Creating booking {}, userId={}", createBookingDto, userId);
        if (createBookingDto.getStart().isAfter(createBookingDto.getEnd())) {
            MyError error = new MyError("Дата начала больше даты окончания. " + createBookingDto.getStart());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        return bookingClient.createBooking(userId, createBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvedBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId,
                                      @RequestParam Boolean approved) {
        log.info("Approved Booking {}, userId={}, approved={}", bookingId, userId, approved);
        return bookingClient.approvedBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(required = false, defaultValue = "ALL") String state,
                                          @RequestParam(required = false) @PositiveOrZero Integer from,
                                          @RequestParam(required = false) @Positive Integer size) {
        return getBookingAll(userId, state, false, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingOwnerAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(required = false, defaultValue = "ALL") String state,
                                               @RequestParam(required = false) @PositiveOrZero Integer from,
                                               @RequestParam(required = false) @Positive Integer size) {
        return getBookingAll(userId, state, true, from, size);
    }

    private ResponseEntity<Object> getBookingAll(Long userId, String state, Boolean owner, Integer from, Integer size) {
        try {
            State stateValue = State.valueOf(state);
            if (from == null || size == null) {
                return bookingClient.getBookingAll(userId, stateValue, owner);
            } else {
                return bookingClient.getBookingAll(userId, stateValue, owner, from, size);
            }
        } catch (IllegalArgumentException e) {
            MyError error = new MyError("Unknown state: " + state);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

    }
}
