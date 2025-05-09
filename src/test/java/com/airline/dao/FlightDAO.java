package com.airline.dao;

import com.airline.model.Flight;
import com.airline.util.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FlightDAOTest {

    private FlightDAO dao;
    private final LocalDateTime depTime = LocalDateTime.of(2025, 5, 10, 10, 0);
    private final LocalDateTime arrTime = LocalDateTime.of(2025, 5, 10, 14, 0);

    @BeforeAll
    void init() {
        // H2 in-memory DB kullan
        DatabaseConnection.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        DatabaseConnection.setCredentials("", "");
        dao = new FlightDAO();
    }

    @BeforeEach
    void resetDatabase() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Tabloları sil ve yeniden oluştur
            stmt.execute("DROP TABLE IF EXISTS seats");
            stmt.execute("DROP TABLE IF EXISTS flights");

            stmt.execute(
                "CREATE TABLE flights (" +
                  "id INT AUTO_INCREMENT PRIMARY KEY, " +
                  "flight_number VARCHAR(20), " +
                  "departure_airport VARCHAR(10), " +
                  "arrival_airport VARCHAR(10), " +
                  "departure_time TIMESTAMP, " +
                  "arrival_time TIMESTAMP, " +
                  "available_seats INT, " +
                  "total_seats INT, " +
                  "airline_name VARCHAR(100), " +
                  "is_active BOOLEAN" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE seats (" +
                  "id INT AUTO_INCREMENT PRIMARY KEY, " +
                  "flight_id INT, " +
                  "seat_type VARCHAR(10), " +
                  "is_available BOOLEAN" +
                ")"
            );

            // Baz uçuşu ekle
            Flight base = new Flight();
            base.setFlightNumber("TK123");
            base.setDepartureAirport("IST");
            base.setArrivalAirport("LHR");
            base.setDepartureTime(depTime);
            base.setArrivalTime(arrTime);
            base.setAvailableSeats(150);
            base.setTotalSeats(180);
            base.setAirlineName("THY");
            dao.createFlight(base);
        }
    }

    @Test
    void createAndRetrieveByNumber_shouldSucceed() throws Exception {
        Flight f = new Flight();
        f.setFlightNumber("AA100");
        f.setDepartureAirport("AAA");
        f.setArrivalAirport("BBB");
        f.setDepartureTime(depTime.plusDays(1));
        f.setArrivalTime(arrTime.plusDays(1));
        f.setAvailableSeats(100);
        f.setTotalSeats(120);
        f.setAirlineName("TestAir");

        assertTrue(dao.createFlight(f));

        Flight retrieved = dao.getFlightByNumber("AA100");
        assertNotNull(retrieved);
        assertEquals("AAA", retrieved.getDepartureAirport());
        assertEquals("BBB", retrieved.getArrivalAirport());
        assertTrue(retrieved.isActive());
    }

    @Test
    void searchFlights_filtersByDateAndAirports() throws Exception {
        // Yeni uçuş ekle
        Flight extra = new Flight();
        extra.setFlightNumber("BB200");
        extra.setDepartureAirport("IST");
        extra.setArrivalAirport("LHR");
        LocalDate target = LocalDate.of(2025, 5, 10);
        extra.setDepartureTime(target.atTime(16, 0));
        extra.setArrivalTime(target.atTime(20, 0));
        extra.setAvailableSeats(80);
        extra.setTotalSeats(100);
        extra.setAirlineName("ExtraAir");
        dao.createFlight(extra);

        List<Flight> list = dao.searchFlights("IST", "LHR", Date.valueOf(target));
        assertEquals(2, list.size());
    }

    @Test
    void updateStatus_andUpdateFlight_shouldReflectChanges() throws Exception {
        Flight base = dao.getFlightByNumber("TK123");
        int id = base.getId();

        assertTrue(dao.updateFlightStatus(id, false));
        assertNull(dao.getFlightByNumber("TK123"));

        assertTrue(dao.updateFlightStatus(id, true));

        // Seats güncelle, aktif kalmalı
        base.setId(id);
        base.setAvailableSeats(50);
        base.setTotalSeats(60);
        base.setActive(true);
        assertTrue(dao.updateFlight(base));

        Flight updated = dao.getFlightById(id);
        assertNotNull(updated);
        assertEquals(50, updated.getAvailableSeats());
        assertEquals(60, updated.getTotalSeats());
        assertTrue(updated.isActive());
    }

    @Test
    void getAllFlights_andIgnoreActive_shouldWork() throws Exception {
        Flight extra = new Flight();
        extra.setFlightNumber("CC300");
        extra.setDepartureAirport("IST");
        extra.setArrivalAirport("LHR");
        extra.setDepartureTime(depTime);
        extra.setArrivalTime(arrTime);
        extra.setAvailableSeats(70);
        extra.setTotalSeats(90);
        extra.setAirlineName("NoAir");
        dao.createFlight(extra);

        Flight e = dao.getFlightByNumber("CC300");
        dao.updateFlightStatus(e.getId(), false);

        List<Flight> active = dao.getAllFlights();
        assertTrue(active.stream().noneMatch(f -> "CC300".equals(f.getFlightNumber())));
        assertNotNull(dao.getFlightByNumberIgnoreActive("CC300"));
    }

    @Test
    void getFlightsWithPreferredSeats_shouldCountCorrectly() throws Exception {
        Flight base = dao.getFlightByNumber("TK123");
        int fid = base.getId();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(
              "INSERT INTO seats (flight_id, seat_type, is_available) VALUES (" +
              fid + ", 'WINDOW', TRUE), (" +
              fid + ", 'AISLE', TRUE), (" +
              fid + ", 'WINDOW', FALSE)"
            );
        }

        List<Flight> windows = dao.getFlightsWithPreferredSeats("WINDOW");
        assertTrue(windows.stream().anyMatch(f -> f.getFlightNumber().equals("TK123")));

        List<Flight> aisles = dao.getFlightsWithPreferredSeats("AISLE");
        assertTrue(aisles.stream().anyMatch(f -> f.getFlightNumber().equals("TK123")));
    }

    @AfterAll
    void tearDown() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE seats");
            stmt.execute("DROP TABLE flights");
        }
    }
}
