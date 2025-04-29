package com.airline.view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.airline.model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        setTitle("User Dashboard");

        loadWindowIcon(); // Load icon using helper method

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 550);
        setMinimumSize(new Dimension(550, 500)); // Set minimum
        setLocationRelativeTo(null);
        initComponents();
    }

    // Helper method to load the window icon
    private void loadWindowIcon() {
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
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // Adjusted gap
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20)); // Adjusted padding

        // Banner + Header + Help Button
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS)); // Stack vertically
        northPanel.setOpaque(false);

        // Load and add Dashboard Banner
        String bannerPath = "/images/ars_user_dashboard.png";
        try {
            URL bannerUrl = getClass().getResource(bannerPath);
            if (bannerUrl != null) {
                ImageIcon originalBannerIcon = new ImageIcon(bannerUrl);
                if (originalBannerIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    Image originalImage = originalBannerIcon.getImage();
                    int desiredWidth = 480;
                    int desiredHeight = -1; // Keep aspect ratio
                    Image scaledImage = originalImage.getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH);
                    ImageIcon scaledBannerIcon = new ImageIcon(scaledImage);

                    JLabel bannerLabel = new JLabel(scaledBannerIcon);
                    bannerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    bannerLabel.setMaximumSize(new Dimension(desiredWidth, scaledBannerIcon.getIconHeight()));
                    northPanel.add(bannerLabel); // Add banner first
                    northPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing below banner
                } else {
                    System.err.println("Dashboard banner '" + bannerPath + "' found but failed to load.");
                }
            } else {
                System.err.println("Dashboard banner '" + bannerPath + "' not found in resources.");
            }
        } catch (Exception e) {
            System.err.println("Error loading dashboard banner '" + bannerPath + "': " + e.getMessage());
            e.printStackTrace();
        }

        // Header and Help Panel
        JPanel headerHelpPanel = new JPanel(new BorderLayout(10, 0));
        headerHelpPanel.setOpaque(false); // Make transparent

        JLabel headerLabel = new JLabel("User Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerHelpPanel.add(headerLabel, BorderLayout.CENTER); // Header in the middle

        // Create Help Button
        JButton helpButton = new JButton("?");
        helpButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        helpButton.setMargin(new Insets(2, 5, 2, 5));
        helpButton.setFocusable(false);

        // Add action listener to help button
        helpButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(UserDashboardUI.this,
                    "For any inconvenience or issue, please reach out to us using the information below:\n\n" // The message to display
                            + "Phone: +90 216 564 9000\n"
                            + "Email: help@ars.com",
                    "Help Box",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        headerHelpPanel.add(helpButton, BorderLayout.EAST);

        // Add an invisible component to the WEST to balance the help button and keep header centered
        headerHelpPanel.add(Box.createRigidArea(new Dimension(helpButton.getPreferredSize().width, 1)), BorderLayout.WEST);


        // Add the header/help panel
        headerHelpPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        northPanel.add(headerHelpPanel);

        // Add the combined north panel to the main panel's north
        mainPanel.add(northPanel, BorderLayout.NORTH);


        // Dashboard Buttons
        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JButton searchFlightsButton = new JButton("Search Flights");
        JButton settingsButton = new JButton("Settings");
        JButton bookFlightButton = new JButton("Book a Flight");
        JButton viewBookingsButton = new JButton("View My Bookings");
        JButton cancelBookingButton = new JButton("Cancel Booking");
        JButton logoutButton = new JButton("Logout");


        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);
        searchFlightsButton.setFont(buttonFont);
        settingsButton.setFont(buttonFont);
        bookFlightButton.setFont(buttonFont);
        viewBookingsButton.setFont(buttonFont);
        cancelBookingButton.setFont(buttonFont);
        logoutButton.setFont(buttonFont);

        // Add buttons in the order
        centerPanel.add(searchFlightsButton);   // Row 1, Col 1
        centerPanel.add(bookFlightButton);      // Row 1, Col 2
        centerPanel.add(viewBookingsButton);    // Row 2, Col 1
        centerPanel.add(cancelBookingButton);   // Row 2, Col 2
        centerPanel.add(settingsButton);        // Row 3, Col 1
        centerPanel.add(logoutButton);          // Row 3, Col 2

        // Wrap centerPanel in a Scroll Pane
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        //Action Listeners for Center Buttons
        searchFlightsButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                UserSearchFlights searchUI = new UserSearchFlights(currentUser);
                searchUI.setVisible(true);
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
                    bookUI.setVisible(true);
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
                    viewBookingUI.setVisible(true);
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
                    cancelUI.setVisible(true);
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

    //Uncomment this part only if you want to run the UserDashboardUI without main code itself. (Visualization purposes)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // For testing, create a dummy user or pass null
            User testUser = null; // Or create a dummy User object
            UserDashboardUI dashboard = new UserDashboardUI(testUser);
            dashboard.display();
        });
    }
}
