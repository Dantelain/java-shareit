package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingAll(Long userId, State state, boolean b) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        if (b) {
            return get("/owner?state={state}", userId, parameters);
        } else {
            return get("?state={state}", userId, parameters);
        }
    }

    public ResponseEntity<Object> getBookingAll(Long userId, State state, boolean b, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        if (b) {
            return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
        } else {
            return get("?state={state}&from={from}&size={size}", userId, parameters);
        }

    }

    public ResponseEntity<Object> createBooking(Long userId, CreateBookingDto createBookingDto) {
        return post("", userId, createBookingDto);
    }

    public ResponseEntity<Object> approvedBooking(Long userId, Long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, new BookingDto());
    }
}
