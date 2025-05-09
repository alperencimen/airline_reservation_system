package com.airline.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        Booking b = new Booking();

        // Default değerler (primitive 0/false, nesneler null)
        assertEquals(0, b.getId());
        assertNull(b.getBookingReference());
        assertEquals(0, b.getUserId());
        assertEquals(0, b.getFlightId());
        assertNull(b.getBookingDate());
        assertNull(b.getSeatNumber());
        assertNull(b.getSeatPreference());
        assertFalse(b.isActive());

        // Test verisi
        LocalDateTime now = LocalDateTime.of(2025, 5, 8, 12, 30);
        b.setId(42);
        b.setBookingReference("REF123");
        b.setUserId(7);
        b.setFlightId(99);
        b.setBookingDate(now);
        b.setSeatNumber("12A");
        b.setSeatPreference("WINDOW");
        b.setActive(true);

        // Getter’larla kontrol
        assertEquals(42, b.getId());
        assertEquals("REF123", b.getBookingReference());
        assertEquals(7, b.getUserId());
        assertEquals(99, b.getFlightId());
        assertEquals(now, b.getBookingDate());
        assertEquals("12A", b.getSeatNumber());
        assertEquals("WINDOW", b.getSeatPreference());
        assertTrue(b.isActive());
    }

    @Test
    void implementsARSModel_contract() {
        // ARSModel arayüzündeki metodların Booking'de de çalıştığını test edelim
        ARSModel model = new Booking();
        model.setId(5);
        model.setActive(false);

        assertEquals(5, model.getId());
        assertFalse(model.isActive());
    }
}
