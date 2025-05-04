package com.airline.dao;

import com.airline.model.Booking;
import com.airline.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public boolean createBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (booking_reference, user_id, flight_id, booking_date, seat_number, seat_preference, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, booking.getBookingReference());
            pstmt.setInt(2, booking.getUserId());
            pstmt.setInt(3, booking.getFlightId());
            pstmt.setTimestamp(4, Timestamp.valueOf(booking.getBookingDate()));
            pstmt.setString(5, booking.getSeatNumber());
            pstmt.setString(6, booking.getSeatPreference());
            pstmt.setBoolean(7, true); // New bookings are active

            return pstmt.executeUpdate() > 0;
        }
    }

    public Booking getBookingByReference(String bookingReference) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE booking_reference = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookingReference);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("id"));
                booking.setBookingReference(rs.getString("booking_reference"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setFlightId(rs.getInt("flight_id"));
                booking.setBookingDate(rs.getTimestamp("booking_date").toLocalDateTime());
                booking.setSeatNumber(rs.getString("seat_number"));
                booking.setSeatPreference(rs.getString("seat_preference"));
                booking.setActive(rs.getBoolean("is_active"));
                return booking;
            }
        }
        return null;
    }

    public List<Booking> getUserBookings(int userId) throws SQLException {
        String sql = "SELECT b.* FROM bookings b JOIN flights f ON b.flight_id = f.id "
                + "WHERE b.user_id = ? AND b.is_active = true AND f.is_active = true "
                + "ORDER BY b.booking_date DESC";


        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("id"));
                booking.setBookingReference(rs.getString("booking_reference"));
                booking.setFlightId(rs.getInt("flight_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setSeatNumber(rs.getString("seat_number"));
                booking.setSeatPreference(rs.getString("seat_preference"));
                booking.setBookingDate(rs.getTimestamp("booking_date").toLocalDateTime());
                booking.setActive(rs.getBoolean("is_active"));
                bookings.add(booking);
            }
        }

        return bookings;
    }


    public boolean updateBookingStatus(int bookingId, boolean isActive) throws SQLException {
        String sql = "UPDATE bookings SET is_active = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, isActive);
            pstmt.setInt(2, bookingId);

            return pstmt.executeUpdate() > 0;
        }
    }
    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE is_active = true ORDER BY booking_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setFlightId(rs.getInt("flight_id"));
                booking.setSeatNumber(rs.getString("seat_number"));
                booking.setSeatPreference(rs.getString("seat_preference"));
                booking.setBookingReference(rs.getString("booking_reference"));
                booking.setBookingDate(rs.getTimestamp("booking_date").toLocalDateTime());
                booking.setActive(rs.getBoolean("is_active"));

                bookings.add(booking);
            }
        }
        return bookings;
    }


    public List<Booking> getBookingsByFlightId(int flightId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE flight_id = ? AND is_active = true";
        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, flightId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("id"));
                booking.setBookingReference(rs.getString("booking_reference"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setFlightId(rs.getInt("flight_id"));
                booking.setBookingDate(rs.getTimestamp("booking_date").toLocalDateTime());
                booking.setSeatNumber(rs.getString("seat_number"));
                booking.setSeatPreference(rs.getString("seat_preference"));
                booking.setActive(rs.getBoolean("is_active"));
                bookings.add(booking);
            }
        }
        return bookings;
    }
    /**
     * Retrieves a list of active booked seat numbers for a specific flight.
     * @param flightId The ID of the flight.
     * @return A list of booked seat numbers (e.g., "1A", "12C").
     * @throws SQLException If a database access error occurs.
     */
    public List<String> getBookedSeatNumbers(int flightId) throws SQLException {
        String sql = "SELECT seat_number FROM bookings WHERE flight_id = ? AND is_active = true";
        List<String> bookedSeats = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, flightId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                bookedSeats.add(rs.getString("seat_number").toUpperCase()); // Store in uppercase for consistency
            }
        }
        return bookedSeats;
    }
    public boolean cancelAllBookingsByFlightId(int flightId) throws SQLException {
        String sql = "UPDATE bookings SET is_active = false WHERE flight_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, flightId);
            return pstmt.executeUpdate() > 0;
        }
    }


}