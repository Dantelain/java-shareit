package ru.practicum.shareit.webclient;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.UserGenerator;

import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
@Rollback
public class WebClientTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void controllerTest() {
        RequestEntity<UserDto> requestEntity = new RequestEntity<>(HttpMethod.GET, URI.create("http://localhost:" + port + "/users/1"));
        ResponseEntity<String> exchange = this.restTemplate.exchange(requestEntity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, exchange.getStatusCode());

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", "1");
        RequestEntity<BookingDto> requestBooking = new RequestEntity<>(headers, HttpMethod.GET, URI.create("http://localhost:" + port + "/bookings?state=all&from=-1&size=10"));
        ResponseEntity<String> exchangeBooking = this.restTemplate.exchange(requestBooking, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, exchangeBooking.getStatusCode());

        RequestEntity<UserDto> requestUser = new RequestEntity<>(UserGenerator.getUserDto(1L), HttpMethod.POST, URI.create("http://localhost:" + port + "/users"));
        ResponseEntity<UserDto> exchangeUser = this.restTemplate.exchange(requestUser, UserDto.class);
        assertEquals(1L, Objects.requireNonNull(exchangeUser.getBody()).getId());
        RequestEntity<BookingDto> requestBooking2 = new RequestEntity<>(headers, HttpMethod.GET, URI.create("http://localhost:" + port + "/bookings?state=fail"));
        ResponseEntity<String> exchangeBooking2 = this.restTemplate.exchange(requestBooking2, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, exchangeBooking2.getStatusCode());
    }


}
