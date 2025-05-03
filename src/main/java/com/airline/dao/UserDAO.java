package com.airline.dao;

import com.airline.model.User;
import com.airline.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public boolean createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, gender, age, country, is_admin, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getGender());
            pstmt.setInt(4, user.getAge());
            pstmt.setString(5, user.getCountry());
            pstmt.setBoolean(6, user.isAdmin());
            pstmt.setBoolean(7, true);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setGender(rs.getString("gender"));
                user.setAge(rs.getInt("age"));
                user.setCountry(rs.getString("country"));
                user.setAdmin(rs.getBoolean("is_admin"));
                user.setActive(rs.getBoolean("is_active"));
                return user;
            }
        }
        return null;
    }
    public boolean updateUserPreference(User user) throws SQLException {
        String sql = "UPDATE users SET default_seat_preference = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getDefaultSeatPreference());
            stmt.setInt(2, user.getId());
            return stmt.executeUpdate() > 0;
        }
    }


    public boolean updateUserStatus(int userId, boolean isActive) throws SQLException {
        String sql = "UPDATE users SET is_active = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, isActive);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM users WHERE is_admin = false";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setGender(rs.getString("gender"));
                user.setAge(rs.getInt("age"));
                user.setCountry(rs.getString("country"));
                user.setAdmin(rs.getBoolean("is_admin"));
                user.setActive(rs.getBoolean("is_active"));
                users.add(user);
            }
        }
        return users;
    }
} 