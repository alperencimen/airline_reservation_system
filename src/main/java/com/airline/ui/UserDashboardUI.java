package com.airline.ui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import javax.swing.*;
import java.awt.*;

public class UserDashboardUI extends JFrame {

    public UserDashboardUI() {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
        
        setTitle("User Dashboard - Airline Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null); 
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("User Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JButton searchFlightsButton = new JButton("Search Flights");
        JButton viewByPreferenceButton = new JButton("View Flights by Seat Preference");
        JButton bookFlightButton = new JButton("Book a Flight");
        JButton viewBookingsButton = new JButton("View My Bookings");
        JButton cancelBookingButton = new JButton("Cancel Booking");
        JButton logoutButton = new JButton("Logout");
        
        centerPanel.add(searchFlightsButton);
        centerPanel.add(viewByPreferenceButton);
        centerPanel.add(bookFlightButton);
        centerPanel.add(viewBookingsButton);
        centerPanel.add(cancelBookingButton);
        centerPanel.add(logoutButton);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        
        searchFlightsButton.addActionListener(e -> {
            dispose(); 
            SwingUtilities.invokeLater(() -> new UserSearchFlights().setVisible(true));
        });
        viewByPreferenceButton.addActionListener(e -> 
            JOptionPane.showMessageDialog(UserDashboardUI.this, "View Flights by Seat Preference clicked."));
        bookFlightButton.addActionListener(e -> 
            JOptionPane.showMessageDialog(UserDashboardUI.this, "Book a Flight clicked."));
        viewBookingsButton.addActionListener(e -> 
            JOptionPane.showMessageDialog(UserDashboardUI.this, "View My Bookings clicked."));
        cancelBookingButton.addActionListener(e -> 
            JOptionPane.showMessageDialog(UserDashboardUI.this, "Cancel Booking clicked."));
        
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginUI().setVisible(true);
        });
    }
    
}
