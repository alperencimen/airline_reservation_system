package com.airline.view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.airline.model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder; // Import EmptyBorder
import java.awt.*;
import java.awt.event.ActionEvent; // Import ActionEvent
import java.net.URL; // Import URL

public class UserDashboardUI extends JFrame implements ARSView {
    private User currentUser;

    public UserDashboardUI(User currentUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch(Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
        this.currentUser = currentUser;
        setTitle("User Dashboard"); // Updated title

        loadWindowIcon(); // Load icon using helper method

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 550); // Adjusted size for banner
        setMinimumSize(new Dimension(550, 500)); // Set minimum
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
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // Adjusted gap
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20)); // Adjusted padding

        // --- North Panel (Banner + Header + Help/Notification Buttons) ---
        JPanel northPanel = new JPanel(); // Changed to hold banner and header/help combo
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS)); // Stack vertically
        northPanel.setOpaque(false);

        // Load and add Dashboard Banner
        String bannerPath = "/images/ars_user_dashboard.png"; // Path to dashboard banner
        try {
            URL bannerUrl = getClass().getResource(bannerPath);
            if (bannerUrl != null) {
                ImageIcon originalBannerIcon = new ImageIcon(bannerUrl);
                if (originalBannerIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    Image originalImage = originalBannerIcon.getImage();
                    int desiredWidth = 480; // Same width as LoginUI logo
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

        // --- Header and Top-Right Buttons Panel (Horizontal) ---
        JPanel headerButtonsPanel = new JPanel(new BorderLayout(10, 0));
        headerButtonsPanel.setOpaque(false); // Make transparent

        // --- Panel for Title and Greeting (Vertical) ---
        JPanel titleGreetingPanel = new JPanel();
        titleGreetingPanel.setLayout(new BoxLayout(titleGreetingPanel, BoxLayout.Y_AXIS));
        titleGreetingPanel.setOpaque(false);

        // Create Header Label (Always visible)
        JLabel headerLabel = new JLabel("User Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align header text
        titleGreetingPanel.add(headerLabel); // Add main title

        // Create and add Greeting Label (Conditional)
        if (currentUser != null && currentUser.getUsername() != null) {
            JLabel greetingLabel = new JLabel("Welcome back, " + currentUser.getUsername() + "!", SwingConstants.CENTER);
            greetingLabel.setFont(new Font("SansSerif", Font.PLAIN, 16)); // Slightly smaller font for greeting
            greetingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleGreetingPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Small space below title
            titleGreetingPanel.add(greetingLabel); // Add greeting label
        }

        headerButtonsPanel.add(titleGreetingPanel, BorderLayout.CENTER); // Add Title+Greeting panel in the middle

        // --- Panel for Top-Right Buttons (Help + Notification) ---
        JPanel topRightButtonsPanel = new JPanel();
        topRightButtonsPanel.setLayout(new BoxLayout(topRightButtonsPanel, BoxLayout.Y_AXIS)); // Stack vertically
        topRightButtonsPanel.setOpaque(false);

        // Create Help Button
        JButton helpButton = new JButton("?");
        helpButton.setFont(new Font("SansSerif", Font.BOLD, 12)); // Smaller font
        helpButton.setMargin(new Insets(2, 5, 2, 5)); // Smaller padding
        helpButton.setFocusable(false); // Optional: remove focus border
        helpButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Align center within BoxLayout

        // Add action listener to help button
        helpButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(UserDashboardUI.this,
                    "For any inconvenience or issue, please reach out to us using the information below:\n\n" // The message to display
                            + "Phone: +90 216 564 9000\n"
                            + "Email: help@ars.com",
                    "Help Box",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        topRightButtonsPanel.add(helpButton); // Add help button first

        // Add small space between buttons
        topRightButtonsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Create Notification Button (using a bell icon/emoji if possible, else text)
        // Note: Emoji rendering depends on OS and font support
        JButton notificationButton = new JButton("\uD83D\uDD14"); // Bell emoji (🔔)
        // JButton notificationButton = new JButton("N"); // Alternative text
        notificationButton.setFont(new Font("SansSerif", Font.PLAIN, 16)); // Increased font size
        notificationButton.setMargin(new Insets(2, 7, 2, 7)); // Adjusted margin slightly
        notificationButton.setFocusable(false);
        notificationButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Align center within BoxLayout

        // Add action listener to notification button
        notificationButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(UserDashboardUI.this,
                        "Please log in to view notifications.",
                        "Login Required",
                        JOptionPane.INFORMATION_MESSAGE);
                // Redirect to login after showing message
                dispose();
                SwingUtilities.invokeLater(() -> new LoginUI().display());
            } else {
                // Placeholder for actual notification display logic
                JOptionPane.showMessageDialog(UserDashboardUI.this,
                        "You have no new notifications.", // Example message
                        "Notifications",
                        JOptionPane.INFORMATION_MESSAGE);
                // Eventually, this might open UserNotificationUI
                // dispose();
                // SwingUtilities.invokeLater(() -> new UserNotificationUI(currentUser).display());
            }
        });
        topRightButtonsPanel.add(notificationButton); // Add notification button below help

        headerButtonsPanel.add(topRightButtonsPanel, BorderLayout.EAST); // Add the button panel to the right

        // Add an invisible component to the WEST to balance the buttons panel and keep header centered
        // Adjust width to match the button panel's preferred width for better centering
        headerButtonsPanel.add(Box.createRigidArea(new Dimension(topRightButtonsPanel.getPreferredSize().width, 1)), BorderLayout.WEST);


        // Add the header/buttons panel below the banner in the main north panel
        headerButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center this sub-panel
        northPanel.add(headerButtonsPanel);

        // Add the combined north panel (banner + header/buttons) to the main panel's north
        mainPanel.add(northPanel, BorderLayout.NORTH);


        // --- Center Panel (Dashboard Buttons) ---
        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 15, 15)); // Increased gaps
        // Add padding inside the grid panel itself
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
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

        // Add buttons in the desired order
        centerPanel.add(searchFlightsButton);   // Row 1, Col 1
        centerPanel.add(bookFlightButton);      // Row 1, Col 2
        centerPanel.add(viewBookingsButton);    // Row 2, Col 1
        centerPanel.add(cancelBookingButton);   // Row 2, Col 2
        centerPanel.add(settingsButton);        // Row 3, Col 1
        centerPanel.add(logoutButton);          // Row 3, Col 2

        // --- Wrap centerPanel in a Scroll Pane ---
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border if desired
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Adjust scroll speed (optional)
        // scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add the scroll pane (containing the buttons) to the main panel's center
        mainPanel.add(scrollPane, BorderLayout.CENTER);
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

    //Uncomment this part only if you want to run the UserDashboardUI without main code itself. (Visualization purposes)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // For testing, create a dummy user or pass null
            // User testUser = null; // Or create a dummy User object
            User testUser = new User(); // Example logged-in user for testing greeting
            testUser.setUsername("TestUser123");

            UserDashboardUI dashboard = new UserDashboardUI(testUser);
            dashboard.display();
        });
    }
}
