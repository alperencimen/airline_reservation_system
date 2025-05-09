package com.airline.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Production defaults (replace as needed)
    private static String url = "jdbc:mysql://localhost:3306/airline_db";
    private static String username = "root";
    private static String password = "alperen8367";

    /**
     * Returns a connection using the current URL, username, and password.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        }
    }

    /**
     * Overrides the JDBC URL for testing (e.g., H2 in-memory).
     */
    public static void setUrl(String newUrl) {
        url = newUrl;
    }

    /**
     * Overrides the credentials for testing.
     */
    public static void setCredentials(String user, String pass) {
        username = user;
        password = pass;
    }
}
