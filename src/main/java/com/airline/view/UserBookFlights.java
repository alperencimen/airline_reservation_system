package com.airline.view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.airline.dao.FlightDAO;
import com.airline.dao.BookingDAO;
import com.airline.model.Flight;
import com.airline.model.Booking;
import com.airline.model.User;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javax.swing.*;

public class UserBookFlights extends JFrame {
    private User currentUser;
    private JTextField flightNumberField;
    private FlightDAO flightDAO = new FlightDAO();
    private BookingDAO bookingDAO = new BookingDAO();
    
    public UserBookFlights(User currentUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch(Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
        this.currentUser = currentUser;
        if (this.currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please log in to book a flight!", "Login Required", JOptionPane.ERROR_MESSAGE);
            dispose();
            new LoginUI().setVisible(true);
            return;
        }
        setTitle("Book a Flight - Airline Reservation System");
        setIconImage(new ImageIcon(getClass().getResource("ars.png")).getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel headerLabel = new JLabel("Book a Flight", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel flightNumberLabel = new JLabel("Enter Flight Number:");
        flightNumberField = new JTextField(15);
        centerPanel.add(flightNumberLabel);
        centerPanel.add(flightNumberField);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton bookButton = new JButton("Book Flight");
        JButton goBackButton = new JButton("Go Back");
        southPanel.add(bookButton);
        southPanel.add(goBackButton);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(bookButton);
        bookButton.addActionListener(e -> bookFlight());
        goBackButton.addActionListener(e -> {
            dispose();
            new UserDashboardUI(currentUser).setVisible(true);
        });
        add(mainPanel);
    }
    
    private void bookFlight() {
        String flightNumber = flightNumberField.getText().trim();
        if (flightNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Flight number cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Flight selectedFlight = flightDAO.getFlightByNumber(flightNumber);
            if (selectedFlight == null) {
                JOptionPane.showMessageDialog(this, "Flight not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (selectedFlight.getAvailableSeats() <= 0) {
                JOptionPane.showMessageDialog(this, "No seats available on this flight!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int available = selectedFlight.getAvailableSeats();
            int row = (available % 30) + 1;
            String[] seatLetters = {"A", "B", "C", "D"};
            String seatLetter = seatLetters[available % 4];
            String seatNumber = row + seatLetter;
            String bookingRef = "BK" + (100000 + (int)(Math.random() * 900000));
            Booking booking = new Booking();
            booking.setBookingReference(bookingRef);
            booking.setUserId(currentUser.getId());
            booking.setFlightId(selectedFlight.getId());
            booking.setBookingDate(LocalDateTime.now());
            booking.setSeatNumber(seatNumber);
            booking.setSeatPreference("WINDOW");
            booking.setActive(true);
            if (bookingDAO.createBooking(booking)) {
                selectedFlight.setAvailableSeats(selectedFlight.getAvailableSeats() - 1);
                flightDAO.updateFlight(selectedFlight);
                JOptionPane.showMessageDialog(this, "Booking successful!\nBooking Reference: " + bookingRef +
                        "\nFlight: " + selectedFlight.getFlightNumber() +
                        "\nSeat: " + seatNumber, "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create booking!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //dummy user---
            User testUser = new User();
            testUser.setId(1);
            testUser.setUsername("testuser");
            testUser.setActive(true);
            testUser.setAdmin(false);
            // End Dummy User ---

            UserBookFlights bookFlightsUI = new UserBookFlights(testUser);

            bookFlightsUI.setVisible(true);

        });
    }
}
