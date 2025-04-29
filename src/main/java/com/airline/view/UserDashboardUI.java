package com.airline.view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.airline.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

public class UserDashboardUI extends JFrame implements ARSView {
    private User currentUser;

    public UserDashboardUI(User currentUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch(Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
        this.currentUser = currentUser;
        setTitle("ARS - User Dashboard");

        loadWindowIcon(); // Load icon using helper method

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 400); // Adjusted size slightly
        setMinimumSize(new Dimension(500, 350)); // Set minimum
        setLocationRelativeTo(null);
        initComponents();
    }

    // Helper method to load the window icon
    private void loadWindowIcon() {
        // Assuming logo is in /images/ars_login.png or similar
        String iconPath = "/images/ars_login.png";
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
        JPanel mainPanel = new JPanel(new BorderLayout(10, 15)); // Increased vertical gap
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20)); // Adjusted padding

        // --- North Panel (Header + Help Button) ---
        JPanel northAreaPanel = new JPanel(new BorderLayout(10, 0)); // Use BorderLayout for north area
        northAreaPanel.setOpaque(false); // Make transparent

        JLabel headerLabel = new JLabel("User Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        // Add header to the center of the north area panel
        northAreaPanel.add(headerLabel, BorderLayout.CENTER);

        // Create Help Button
        JButton helpButton = new JButton("?");
        helpButton.setFont(new Font("SansSerif", Font.BOLD, 12)); // Smaller font
        helpButton.setMargin(new Insets(2, 5, 2, 5)); // Smaller padding
        helpButton.setFocusable(false); // Optional: remove focus border

        // Add action listener to help button
        helpButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(UserDashboardUI.this,
                    "For any inconvenience or issue, please reach out to us using the information below:\n\n" // The message to display
                    + "Phone: +90 216 564 9000\n"
                    + "Email: help@ars.com",
                    "Help Box",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Add help button to the east (right) side of the north area panel
        northAreaPanel.add(helpButton, BorderLayout.EAST);

        // Add the combined north area panel to the main panel's north
        mainPanel.add(northAreaPanel, BorderLayout.NORTH);


        // --- Center Panel (Dashboard Buttons) ---
        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 15, 15)); // Increased gaps
        JButton searchFlightsButton = new JButton("Search Flights");
        JButton settingsButton = new JButton("Settings"); // Renamed from viewByPreferenceButton
        JButton bookFlightButton = new JButton("Book a Flight");
        JButton viewBookingsButton = new JButton("View My Bookings");
        JButton cancelBookingButton = new JButton("Cancel Booking");
        JButton logoutButton = new JButton("Logout");

        // Increase button font size slightly
        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);
        searchFlightsButton.setFont(buttonFont);
        settingsButton.setFont(buttonFont);
        bookFlightButton.setFont(buttonFont);
        viewBookingsButton.setFont(buttonFont);
        cancelBookingButton.setFont(buttonFont);
        logoutButton.setFont(buttonFont);

        centerPanel.add(searchFlightsButton);
        centerPanel.add(settingsButton);
        centerPanel.add(bookFlightButton);
        centerPanel.add(viewBookingsButton);
        centerPanel.add(cancelBookingButton);
        centerPanel.add(logoutButton);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);

        // --- Action Listeners for Center Buttons ---
        searchFlightsButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                UserSearchFlights searchUI = new UserSearchFlights(currentUser);
                searchUI.setVisible(true); // Assuming display() or setVisible()
            });
        });

        settingsButton.addActionListener(e ->
                JOptionPane.showMessageDialog(UserDashboardUI.this, "Settings section is opened."));

        bookFlightButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(UserDashboardUI.this, "Please log in to book a flight!", "Login Required", JOptionPane.ERROR_MESSAGE);
                dispose();
                SwingUtilities.invokeLater(() -> new LoginUI().display());
            } else {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    UserBookFlights bookUI = new UserBookFlights(currentUser);
                    bookUI.setVisible(true); // Assuming display() or setVisible()
                });
            }
        });

        viewBookingsButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(UserDashboardUI.this, "Please log in to view bookings!", "Login Required", JOptionPane.ERROR_MESSAGE);
                dispose();
                SwingUtilities.invokeLater(() -> new LoginUI().display());
            } else {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    UserViewBooking viewBookingUI = new UserViewBooking(currentUser);
                    viewBookingUI.setVisible(true); // Assuming display() or setVisible()
                });
            }
        });

        cancelBookingButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(UserDashboardUI.this, "Please log in to cancel bookings!", "Login Required", JOptionPane.ERROR_MESSAGE);
                dispose();
                SwingUtilities.invokeLater(() -> new LoginUI().display());
            } else {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    UserCancelFlights cancelUI = new UserCancelFlights(currentUser);
                    cancelUI.setVisible(true); // Assuming display() or setVisible()
                });
            }
        });

        logoutButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginUI().display());
        });
    }

    @Override
    public void display() {
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // For testing, create a dummy user or pass null
            User testUser = null; // Or create a dummy User object
            UserDashboardUI dashboard = new UserDashboardUI(testUser);
            dashboard.display();
        });
    }
}
