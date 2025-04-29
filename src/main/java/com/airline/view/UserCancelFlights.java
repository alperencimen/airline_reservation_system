package com.airline.view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.airline.dao.BookingDAO;
import com.airline.dao.FlightDAO;
import com.airline.model.Booking;
import com.airline.model.Flight;
import com.airline.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.SQLException;

public class UserCancelFlights extends JFrame {
    private User currentUser;
    private JTextField bookingRefField;
    private BookingDAO bookingDAO = new BookingDAO();
    private FlightDAO flightDAO = new FlightDAO();

    public UserCancelFlights(User currentUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch(Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
        this.currentUser = currentUser;
        if (this.currentUser == null) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Please log in to cancel a booking!", "Login Required", JOptionPane.ERROR_MESSAGE);
                dispose();
                new LoginUI().display();
            });

            return;
        }
        setTitle("Cancel Booking");

        loadWindowIcon();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 350);
        setMinimumSize(new Dimension(400, 300));
        setLocationRelativeTo(null);
        initComponents();
    }

    // Helper method to load the window icon
    private void loadWindowIcon() {
        String iconPath = "/images/ars.png"; // Assuming same logo
        try {
            URL iconUrl = getClass().getResource(iconPath);
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    setIconImage(icon.getImage());
                } else {
                    System.err.println("Window Icon '" + iconPath + "' found but failed to load.");
                }
            } else {
                System.err.println("Window Icon '" + iconPath + "' not found in resources.");
            }
        } catch (Exception e) {
            System.err.println("Error loading window icon '" + iconPath + "': " + e.getMessage());
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Cancel Booking", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST; // Default anchor

        // Label for Booking Reference
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        centerPanel.add(new JLabel("Enter Booking Reference:"), gbc);

        // Text Field for Booking Reference
        bookingRefField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(bookingRefField, gbc);

        // Hint Label for the booking entry.
        JLabel hintLabel = new JLabel("(Booking number starts with 'BK')");
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11)); // Smaller, italic font for convinence
        hintLabel.setForeground(Color.GRAY);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 5, 5);
        centerPanel.add(hintLabel, gbc);

        // Reset insets for subsequent components if any
        gbc.insets = new Insets(5, 5, 5, 5);

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

            SwingUtilities.invokeLater(() -> {
                UserDashboardUI dashboard = new UserDashboardUI(currentUser);
                dashboard.display();
            });
        });
        add(mainPanel);

        // Disable cancelling if user is null (relevant if opened via main method)
        if (currentUser == null) {
            cancelButton.setEnabled(false);
            bookingRefField.setEnabled(false);
        }
    }

    private void cancelBooking() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Error: No user logged in.", "Cancellation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String bookingRef = bookingRefField.getText().trim();
        if (bookingRef.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Booking reference cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Basic check if the input format looks plausible
        if (!bookingRef.toUpperCase().startsWith("BK") || bookingRef.length() < 3) {
             JOptionPane.showMessageDialog(this, "Invalid booking reference format.", "Entry Error", JOptionPane.WARNING_MESSAGE);
             return;
         }


        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel booking '" + bookingRef + "'?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Booking booking = bookingDAO.getBookingByReference(bookingRef);

            if (booking != null && booking.getUserId() == currentUser.getId()) {
                if (!booking.isActive()) {
                    JOptionPane.showMessageDialog(this, "Booking '" + bookingRef + "' has already been cancelled.", "Already Cancelled", JOptionPane.INFORMATION_MESSAGE);
                    bookingRefField.setText("");
                    return;
                }

                if (bookingDAO.updateBookingStatus(booking.getId(), false)) {
                    Flight flight = flightDAO.getFlightById(booking.getFlightId());
                    boolean flightUpdated = false;
                    if (flight != null) {
                        flight.setAvailableSeats(flight.getAvailableSeats() + 1);
                        flightUpdated = flightDAO.updateFlight(flight);
                    }

                    JOptionPane.showMessageDialog(this, "Booking cancelled successfully!", "Cancellation Complete", JOptionPane.INFORMATION_MESSAGE);

                    // Return to Dashboard
                    dispose(); // Close this cancellation window
                    SwingUtilities.invokeLater(() -> {
                        UserDashboardUI dashboard = new UserDashboardUI(currentUser);
                        dashboard.display(); // Show the dashboard
                    });

                    if (flight != null && !flightUpdated) {
                        // Log inconsistency if flight existed but update failed
                        System.err.println("CRITICAL: Booking ID " + booking.getId() + " cancelled, but failed to increment seats for Flight ID " + flight.getId());
                        // Optionally inform user of partial success/issue
                    } else if (flight == null) {
                        // Log warning if flight couldn't be found
                        System.err.println("WARNING: Booking ID " + booking.getId() + " cancelled, but associated Flight ID " + booking.getFlightId() + " not found.");
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel booking in database!", "Cancellation Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Booking not found or you are not authorized to cancel this booking.", "Cancellation Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error during cancellation: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
/*
    //Uncomment this part only if you want to run the LoginUI without main code itself. (Visualization purposes)
    // Main method for testing remains, but needs a valid User and existing Booking Ref in DB to fully test cancellation
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User testUser = new User();
            testUser.setId(1); // Example ID - MUST match a user in your DB for testing
            testUser.setUsername("testuser");
            // Set other fields if needed by logic

            UserCancelFlights cancelUI = new UserCancelFlights(testUser);
            cancelUI.setVisible(true);
        });

    }
 */
}
