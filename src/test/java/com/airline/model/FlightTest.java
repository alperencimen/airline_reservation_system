package com.airline.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FlightTest {

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        Flight f = new Flight();

        // Varsayılan değerler
        assertEquals(0, f.getId());
        assertNull(f.getFlightNumber());
        assertNull(f.getDepartureAirport());
        assertNull(f.getArrivalAirport());
        assertNull(f.getDepartureTime());
        assertNull(f.getArrivalTime());
        assertEquals(0, f.getAvailableSeats());
        assertEquals(0, f.getAvailablePreferredSeats());
        assertNull(f.getAirlineName());
        assertEquals(0, f.getTotalSeats());
        assertFalse(f.isActive());

        // Test verisi
        LocalDateTime dep = LocalDateTime.of(2025, 6, 1, 14, 0);
        LocalDateTime arr = LocalDateTime.of(2025, 6, 1, 16, 30);

        f.setId(101);
        f.setFlightNumber("TK1234");
        f.setDepartureAirport("IST");
        f.setArrivalAirport("JFK");
        f.setDepartureTime(dep);
        f.setArrivalTime(arr);
        f.setAvailableSeats(180);
        f.setAvailablePreferredSeats(20);
        f.setAirlineName("Turkish Airlines");
        f.setTotalSeats(200);
        f.setActive(true);

        // Kontrol
        assertEquals(101, f.getId());
        assertEquals("TK1234", f.getFlightNumber());
        assertEquals("IST", f.getDepartureAirport());
        assertEquals("JFK", f.getArrivalAirport());
        assertEquals(dep, f.getDepartureTime());
        assertEquals(arr, f.getArrivalTime());
        assertEquals(180, f.getAvailableSeats());
        assertEquals(20, f.getAvailablePreferredSeats());
        assertEquals("Turkish Airlines", f.getAirlineName());
        assertEquals(200, f.getTotalSeats());
        assertTrue(f.isActive());
    }

    @Test
    void implementsARSModel_contract() {
        // ARSModel arayüzü metodlarının da çalıştığını kontrol edelim
        ARSModel m = new Flight();
        
        m.setId(555);
        m.setActive(false);

        assertEquals(555, m.getId());
        assertFalse(m.isActive());
    }
}
