package com.airline.dao;

import com.airline.model.Flight;
import com.airline.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {

    public boolean createFlight(Flight flight) throws SQLException {
        String sql = "INSERT INTO flights (flight_number, departure_airport, arrival_airport, departure_time, arrival_time, available_seats, total_seats, airline_name, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, flight.getFlightNumber());
            pstmt.setString(2, flight.getDepartureAirport());
            pstmt.setString(3, flight.getArrivalAirport());
            pstmt.setTimestamp(4, Timestamp.valueOf(flight.getDepartureTime()));
            pstmt.setTimestamp(5, Timestamp.valueOf(flight.getArrivalTime()));
            pstmt.setInt(6, flight.getAvailableSeats());
            pstmt.setInt(7, flight.getTotalSeats());
            pstmt.setString(8, flight.getAirlineName());
            pstmt.setBoolean(9, true);

            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Flight> searchFlights(String departureAirport, String arrivalAirport, Date date) throws SQLException {
        // H2 ve MySQL uyumlu tarih kontrolü
        String sql = "SELECT * FROM flights WHERE departure_airport = ? "
                   + "AND arrival_airport = ? "
                   + "AND CAST(departure_time AS DATE) = ? "
                   + "AND is_active = true";
        List<Flight> flights = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, departureAirport);
            pstmt.setString(2, arrivalAirport);
            pstmt.setDate(3, date);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                flights.add(mapResultSetToFlight(rs));
            }
        }
        return flights;
    }

    public boolean updateFlightStatus(int flightId, boolean isActive) throws SQLException {
        String sql = "UPDATE flights SET is_active = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, isActive);
            pstmt.setInt(2, flightId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateFlight(Flight flight) throws SQLException {
        String sql = "UPDATE flights SET available_seats = ?, total_seats = ?, is_active = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, flight.getAvailableSeats());
            pstmt.setInt(2, flight.getTotalSeats());
            pstmt.setBoolean(3, flight.isActive());
            pstmt.setInt(4, flight.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public Flight getFlightByNumber(String flightNumber) throws SQLException {
        String sql = "SELECT * FROM flights WHERE flight_number = ? AND is_active = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, flightNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToFlight(rs);
            }
        }
        return null;
    }

    public Flight getFlightById(int flightId) throws SQLException {
        String sql = "SELECT * FROM flights WHERE id = ? AND is_active = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, flightId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToFlight(rs);
            }
        }
        return null;
    }

    public Flight getFlightByNumberIgnoreActive(String flightNumber) throws SQLException {
        String sql = "SELECT * FROM flights WHERE flight_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, flightNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToFlight(rs);
            }
        }
        return null;
    }

    public List<Flight> getAllFlights() throws SQLException {
        String sql = "SELECT * FROM flights WHERE is_active = true";
        List<Flight> flights = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                flights.add(mapResultSetToFlight(rs));
            }
        }
        return flights;
    }

    public List<Flight> getFlightsWithPreferredSeats(String seatPreference) throws SQLException {
        String sql = "SELECT f.*, "
                   + "COUNT(CASE WHEN s.seat_type = ? AND s.is_available = true THEN 1 END) as available_preferred_seats "
                   + "FROM flights f "
                   + "LEFT JOIN seats s ON f.id = s.flight_id "
                   + "WHERE s.seat_type = ? AND s.is_available = true "
                   + "GROUP BY f.id "
                   + "HAVING COUNT(CASE WHEN s.seat_type = ? AND s.is_available = true THEN 1 END) > 0";
        List<Flight> flights = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, seatPreference);
            pstmt.setString(2, seatPreference);
            pstmt.setString(3, seatPreference);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                flights.add(mapResultSetToFlight(rs));
            }
        }
        return flights;
    }

    private Flight mapResultSetToFlight(ResultSet rs) throws SQLException {
        Flight flight = new Flight();
        flight.setId(rs.getInt("id"));
        flight.setFlightNumber(rs.getString("flight_number"));
        flight.setDepartureAirport(rs.getString("departure_airport"));
        flight.setArrivalAirport(rs.getString("arrival_airport"));
        flight.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
        flight.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());
        flight.setAvailableSeats(rs.getInt("available_seats"));
        flight.setTotalSeats(rs.getInt("total_seats"));
        flight.setAirlineName(rs.getString("airline_name"));
        flight.setActive(rs.getBoolean("is_active"));
        return flight;
    }
}
