package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.utils.BookingGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BookingTest {

    @Test
    void bookingModelTest() {
        Booking booking1 = BookingGenerator.getBooking(1L);
        Booking booking2 = BookingGenerator.getBooking(1L);
        assertEquals(booking1.hashCode(), booking2.hashCode());
        assertEquals(booking1, booking2);
        assertEquals(booking1, booking1);
        booking2.setId(2L);
        assertNotEquals(booking1, booking2);
        assertNotEquals(booking1, null);
        booking1.setId(null);
        assertNotEquals(booking1, booking2);
        booking2.setId(1L);
        assertNotEquals(booking1, booking2);
    }
}
