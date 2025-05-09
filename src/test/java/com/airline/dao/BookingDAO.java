package com.airline.dao;

import com.airline.model.Booking;
import com.airline.util.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingDAOTest {

    private BookingDAO dao;

    @BeforeAll
    void init() {
        // H2 in-memory DB kullanımı
        DatabaseConnection.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        DatabaseConnection.setCredentials("", "");
        dao = new BookingDAO();
    }

    @BeforeEach
    void resetDatabase() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Önce varsa tablolardan kurtul
            stmt.execute("DROP TABLE IF EXISTS bookings");
            stmt.execute("DROP TABLE IF EXISTS flights");

            // flights tablosu
            stmt.execute(
                "CREATE TABLE flights (" +
                  "id INT AUTO_INCREMENT PRIMARY KEY, " +
                  "is_active BOOLEAN" +
                ")"
            );
            // örnek bir uçuş ekle
            stmt.execute("INSERT INTO flights (is_active) VALUES (TRUE)");

            // bookings tablosu
            stmt.execute(
                "CREATE TABLE bookings (" +
                  "id INT AUTO_INCREMENT PRIMARY KEY, " +
                  "booking_reference VARCHAR(50), " +
                  "user_id INT, " +
                  "flight_id INT, " +
                  "booking_date TIMESTAMP, " +
                  "seat_number VARCHAR(5), " +
                  "seat_preference VARCHAR(10), " +
                  "is_active BOOLEAN" +
                ")"
            );
        }
    }

    @Test
    void createAndFetchByReference_shouldSucceed() throws Exception {
        Booking b = new Booking();
        b.setBookingReference("REF001");
        b.setUserId(42);
        b.setFlightId(1);
        b.setBookingDate(LocalDateTime.of(2025, 5, 9, 12, 0));
        b.setSeatNumber("12A");
        b.setSeatPreference("WINDOW");

        assertTrue(dao.createBooking(b), "createBooking dönmeli");

        Booking fetched = dao.getBookingByReference("REF001");
        assertNotNull(fetched);
        assertEquals("REF001", fetched.getBookingReference());
        assertEquals(42, fetched.getUserId());
        assertEquals(1, fetched.getFlightId());
        assertEquals("12A", fetched.getSeatNumber());
        assertEquals("WINDOW", fetched.getSeatPreference());
        assertTrue(fetched.isActive());
    }

    @Test
    void getUserBookings_shouldReturnOnlyActive_andJoinFlights() throws Exception {
        // Aktif booking (userId=10)
        Booking active = new Booking();
        active.setBookingReference("A1");
        active.setUserId(10);
        active.setFlightId(1);
        active.setBookingDate(LocalDateTime.now().minusDays(1));
        active.setSeatNumber("1A");
        active.setSeatPreference("AISLE");
        assertTrue(dao.createBooking(active));

        // Pasif booking (aynı user)
        Booking inactive = new Booking();
        inactive.setBookingReference("A2");
        inactive.setUserId(10);
        inactive.setFlightId(1);
        inactive.setBookingDate(LocalDateTime.now());
        inactive.setSeatNumber("1B");
        inactive.setSeatPreference("WINDOW");
        assertTrue(dao.createBooking(inactive));
        Booking fetched = dao.getBookingByReference("A2");
        assertTrue(dao.updateBookingStatus(fetched.getId(), false));

        List<Booking> list = dao.getUserBookings(10);
        assertEquals(1, list.size());
        assertEquals("A1", list.get(0).getBookingReference());
    }

    @Test
    void updateBookingStatus_shouldToggle() throws Exception {
        Booking b = new Booking();
        b.setBookingReference("B1");
        b.setUserId(20);
        b.setFlightId(1);
        b.setBookingDate(LocalDateTime.now());
        b.setSeatNumber("2A");
        b.setSeatPreference("WINDOW");
        dao.createBooking(b);

        Booking fetched = dao.getBookingByReference("B1");
        assertTrue(fetched.isActive());

        assertTrue(dao.updateBookingStatus(fetched.getId(), false));
        Booking after = dao.getBookingByReference("B1");
        assertFalse(after.isActive());
    }

    @Test
    void getAllBookings_shouldReturnOnlyActive() throws Exception {
        // REF2 aktif
        Booking b2 = new Booking();
        b2.setBookingReference("REF2");
        b2.setUserId(30);
        b2.setFlightId(1);
        b2.setBookingDate(LocalDateTime.now());
        b2.setSeatNumber("3A");
        b2.setSeatPreference("AISLE");
        assertTrue(dao.createBooking(b2));

        // REF3 pasif
        Booking b3 = new Booking();
        b3.setBookingReference("REF3");
        b3.setUserId(31);
        b3.setFlightId(1);
        b3.setBookingDate(LocalDateTime.now());
        b3.setSeatNumber("3B");
        b3.setSeatPreference("WINDOW");
        assertTrue(dao.createBooking(b3));
        Booking f3 = dao.getBookingByReference("REF3");
        assertTrue(dao.updateBookingStatus(f3.getId(), false));

        List<Booking> all = dao.getAllBookings();
        assertEquals(1, all.size());
        assertEquals("REF2", all.get(0).getBookingReference());
    }

    @Test
    void getBookingsByFlightId_shouldFilterCorrectly() throws Exception {
        // test öncesi bir kayıt ekle
        Booking seed = new Booking();
        seed.setBookingReference("F1");
        seed.setUserId(50);
        seed.setFlightId(1);
        seed.setBookingDate(LocalDateTime.now());
        seed.setSeatNumber("X1");
        seed.setSeatPreference("WINDOW");
        assertTrue(dao.createBooking(seed));

        List<Booking> list = dao.getBookingsByFlightId(1);
        assertEquals(1, list.size());
        assertEquals("F1", list.get(0).getBookingReference());
    }

    @Test
    void getBookedSeatNumbers_shouldUppercase() throws Exception {
        // test öncesi bir kayıt ekle (küçük harf)
        Booking seed = new Booking();
        seed.setBookingReference("S1");
        seed.setUserId(60);
        seed.setFlightId(1);
        seed.setBookingDate(LocalDateTime.now());
        seed.setSeatNumber("z9");
        seed.setSeatPreference("AISLE");
        assertTrue(dao.createBooking(seed));

        List<String> seats = dao.getBookedSeatNumbers(1);
        assertEquals(1, seats.size());
        assertEquals("Z9", seats.get(0));
    }

    @Test
    void cancelAllBookingsByFlightId_shouldDeactivate() throws Exception {
        // test öncesi bir kayıt ekle
        Booking seed = new Booking();
        seed.setBookingReference("C1");
        seed.setUserId(70);
        seed.setFlightId(1);
        seed.setBookingDate(LocalDateTime.now());
        seed.setSeatNumber("C1");
        seed.setSeatPreference("WINDOW");
        assertTrue(dao.createBooking(seed));

        assertTrue(dao.cancelAllBookingsByFlightId(1));
        List<Booking> remaining = dao.getBookingsByFlightId(1);
        assertTrue(remaining.isEmpty());
    }

    @AfterAll
    void tearDown() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE bookings");
            stmt.execute("DROP TABLE flights");
        }
    }
}
