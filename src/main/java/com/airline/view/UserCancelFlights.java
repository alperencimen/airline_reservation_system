package com.airline.view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.airline.dao.BookingDAO;
import com.airline.dao.FlightDAO;
import com.airline.model.Booking;
import com.airline.model.Flight;
import com.airline.model.User;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class UserCancelFlights extends JFrame {
    private User currentUser;
    private JTextField bookingRefField;
    private BookingDAO bookingDAO = new BookingDAO();

    public UserCancelFlights(User currentUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch(Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
        this.currentUser = currentUser;
        if (this.currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please log in to cancel a booking!", "Login Required", JOptionPane.ERROR_MESSAGE);
            dispose();
            new LoginUI().setVisible(true);
            return;
        }
        setTitle("Cancel Booking - Airline Reservation System");
        setIconImage(new ImageIcon(getClass().getResource("ars.png")).getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel headerLabel = new JLabel("Cancel Booking", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel bookingRefLabel = new JLabel("Enter Booking Reference:");
        bookingRefField = new JTextField(15);
        centerPanel.add(bookingRefLabel);
        centerPanel.add(bookingRefField);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton cancelButton = new JButton("Cancel Booking");
        JButton goBackButton = new JButton("Go Back");
        southPanel.add(cancelButton);
        southPanel.add(goBackButton);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(cancelButton);
        cancelButton.addActionListener(e -> cancelBooking());
        goBackButton.addActionListener(e -> {
            dispose();
            new UserDashboardUI(currentUser).setVisible(true);
        });
        add(mainPanel);
    }

    private void cancelBooking() {
        String bookingRef = bookingRefField.getText().trim();
        if (bookingRef.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Booking reference cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Booking booking = bookingDAO.getBookingByReference(bookingRef);
            if (booking != null && booking.getUserId() == currentUser.getId()) {
                Flight flight = new FlightDAO().getFlightById(booking.getFlightId());
                if (flight != null) {
                    flight.setAvailableSeats(flight.getAvailableSeats() + 1);
                    new FlightDAO().updateFlight(flight);
                }
                if (bookingDAO.updateBookingStatus(booking.getId(), false)) {
                    JOptionPane.showMessageDialog(this, "Booking cancelled successfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel booking!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Booking not found or unauthorized!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserCancelFlights(new User()).setVisible(true));
    }
}
