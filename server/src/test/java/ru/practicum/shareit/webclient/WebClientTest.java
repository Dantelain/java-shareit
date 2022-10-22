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

        RequestEntity<UserDto> requestUser = new RequestEntity<>(UserGenerator.getUserDto(1L), HttpMethod.POST, URI.create("http://localhost:" + port + "/users"));
        ResponseEntity<UserDto> exchangeUser = this.restTemplate.exchange(requestUser, UserDto.class);
        assertEquals(1L, Objects.requireNonNull(exchangeUser.getBody()).getId());
    }


}
