package com.airline.dao;

import com.airline.model.Flight;
import com.airline.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {
    
    public boolean createFlight(Flight flight) throws SQLException {
        String sql = "INSERT INTO flights (flight_number, departure_airport, arrival_airport, departure_time, arrival_time, available_seats, airline_name, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, flight.getFlightNumber());
            pstmt.setString(2, flight.getDepartureAirport());
            pstmt.setString(3, flight.getArrivalAirport());
            pstmt.setTimestamp(4, Timestamp.valueOf(flight.getDepartureTime()));
            pstmt.setTimestamp(5, Timestamp.valueOf(flight.getArrivalTime()));
            pstmt.setInt(6, flight.getAvailableSeats());
            pstmt.setString(7, flight.getAirlineName());
            pstmt.setBoolean(8, true);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Flight> searchFlights(String departureAirport, String arrivalAirport, Date date) throws SQLException {
        String sql = "SELECT * FROM flights WHERE departure_airport = ? AND arrival_airport = ? AND DATE(departure_time) = ? AND is_active = true";
        List<Flight> flights = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, departureAirport);
            pstmt.setString(2, arrivalAirport);
            pstmt.setDate(3, date);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Flight flight = new Flight();
                flight.setId(rs.getInt("id"));
                flight.setFlightNumber(rs.getString("flight_number"));
                flight.setDepartureAirport(rs.getString("departure_airport"));
                flight.setArrivalAirport(rs.getString("arrival_airport"));
                flight.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
                flight.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());
                flight.setAvailableSeats(rs.getInt("available_seats"));
                flight.setAirlineName(rs.getString("airline_name"));
                flight.setActive(rs.getBoolean("is_active"));
                flights.add(flight);
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
} 