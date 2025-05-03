package com.airline.view;

import com.airline.dao.BookingDAO;
import com.airline.dao.FlightDAO;
import com.airline.dao.NotificationDAO;
import com.airline.model.Booking;
import com.airline.model.Flight;
import com.airline.model.User;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class AdminCancelFlight extends JFrame {
    private final User adminUser;
    private JTextField flightNumberField;
    private final FlightDAO flightDAO = new FlightDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public AdminCancelFlight(User adminUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
        this.adminUser = adminUser;

        setTitle("Cancel Flight (Admin)");
        loadWindowIcon();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 300);
        setMinimumSize(new Dimension(400, 250));
        setLocationRelativeTo(null);
        initComponents();
    }

    private void loadWindowIcon() {
        String iconPath = "/images/ars.png";
        try {
            URL iconUrl = getClass().getResource(iconPath);
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    setIconImage(icon.getImage());
                } else {
                    System.err.println("Icon load failed for: " + iconPath);
                }
            } else {
                System.err.println("Icon not found: " + iconPath);
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Cancel Flight", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        centerPanel.add(new JLabel("Flight Number:"), gbc);

        flightNumberField = new JTextField(15);
        gbc.gridx = 1;
        centerPanel.add(flightNumberField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel hint = new JLabel("(e.g., AA1111)");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        centerPanel.add(hint, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel Flight");
        JButton backBtn = new JButton("Go Back");
        southPanel.add(cancelBtn);
        southPanel.add(backBtn);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(cancelBtn);

        cancelBtn.addActionListener(e -> cancelFlight());
        backBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new AdminDashboardUI(adminUser).setVisible(true));
        });

        add(mainPanel);
    }

    private void cancelFlight() {
        String flightNumber = flightNumberField.getText().trim().toUpperCase();
        if (flightNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Flight number cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure to cancel flight '" + flightNumber + "'?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Flight flight = flightDAO.getFlightByNumberIgnoreActive(flightNumber);
            if (flight == null) {
                JOptionPane.showMessageDialog(this, "Flight not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!flight.isActive()) {
                JOptionPane.showMessageDialog(this, "Flight is already inactive.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            flight.setActive(false);
            boolean success = flightDAO.updateFlightStatus(flight.getId(), false);

            if (success) {
                List<Booking> bookings = bookingDAO.getBookingsByFlightId(flight.getId());
                for (Booking b : bookings) {
                    if (b.isActive()) {
                        bookingDAO.updateBookingStatus(b.getId(), false);
                        notificationDAO.createNotification(b.getUserId(),
                                "Your booking '" + b.getBookingReference() + "' was cancelled due to flight cancellation.");
                    }
                }

                JOptionPane.showMessageDialog(this, "Flight and related bookings successfully cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                SwingUtilities.invokeLater(() -> new AdminDashboardUI(adminUser).setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel the flight in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "SQL Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
