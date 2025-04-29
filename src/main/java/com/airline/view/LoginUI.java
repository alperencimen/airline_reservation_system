package com.airline.view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.airline.dao.UserDAO;
import com.airline.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL; // Import URL
import java.sql.SQLException;

public class LoginUI extends JFrame implements ARSView {
    private JTextField userField; // Made instance variable for potential future use
    private JPasswordField passField; // Made instance variable

    @Override
    public void display() {
        // Ensure UI updates are done on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

    public LoginUI() {
        // Apply Look and Feel first
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize FlatLaf LaF: " + e.getMessage());
            // Handle error or fallback to default L&F if needed
        }

        setTitle("ARS - Login"); // Slightly more descriptive title
        // Use a larger default size
        setSize(500, 450); // Increased height slightly more for scaled logo + components
        setMinimumSize(new Dimension(400, 400)); // Adjust minimum height
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Load window icon (this sets the icon in the title bar/taskbar)
        loadWindowIcon();

        initComponents();
    }

    // Helper method to load the window icon
    private void loadWindowIcon() {
        // Correct path for the image inside the 'images' folder within resources
        String iconPath = "/images/ars.png";
        try {
            // Get the URL for the resource
            URL iconUrl = getClass().getResource(iconPath); // Use the correct path
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                // Check if the image loaded correctly (ImageIcon doesn't throw error on failure)
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    setIconImage(icon.getImage());
                } else {
                    System.err.println("Window Icon '" + iconPath + "' found but failed to load.");
                }
            } else {
                System.err.println("Window Icon '" + iconPath + "' not found in resources.");
            }
        } catch (Exception e) { // Catch broader exceptions during resource loading
            System.err.println("Error loading window icon '" + iconPath + "': " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void initComponents() {
        // Main panel with border layout and padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        // Reduced top padding as logo will add space
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        // --- NORTH Panel (Logo + Header Text) ---
        JPanel northPanel = new JPanel();
        // Use BoxLayout for vertical arrangement
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setOpaque(false); // Make panel transparent if needed

        // Correct path for the image inside the 'images' folder within resources
        String logoPath = "/images/ars.png";
        // Load and add Logo Label
        try {
            URL logoUrl = getClass().getResource(logoPath); // Use the correct path
            if (logoUrl != null) {
                ImageIcon originalLogoIcon = new ImageIcon(logoUrl); // Load original icon

                // Check if the original image loaded correctly
                if (originalLogoIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    // --- Scale the image ---
                    Image originalImage = originalLogoIcon.getImage();
                    int desiredWidth = 80; // Set desired width (e.g., 80 pixels)
                    int desiredHeight = -1; // Keep aspect ratio (-1) or set a specific height (e.g., 80)
                    // Use SCALE_SMOOTH for better quality scaling
                    Image scaledImage = originalImage.getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH);
                    // Create a new ImageIcon from the scaled image
                    ImageIcon scaledLogoIcon = new ImageIcon(scaledImage);
                    // --- End Scaling ---

                    // Create JLabel with the *scaled* icon
                    JLabel logoLabel = new JLabel(scaledLogoIcon);
                    logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally
                    northPanel.add(logoLabel); // Add the logo first
                    // Add some vertical spacing between logo and text
                    northPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                } else {
                    System.err.println("Logo image '" + logoPath + "' found but failed to load for display.");
                }
            } else {
                System.err.println("Logo image '" + logoPath + "' not found in resources.");
                // Optionally add placeholder text if logo fails
                JLabel placeholder = new JLabel("[Logo Not Found at " + logoPath + "]");
                placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
                northPanel.add(placeholder);
                northPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } catch (Exception e) {
            System.err.println("Error loading logo '" + logoPath + "' for display: " + e.getMessage());
            e.printStackTrace();
        }


        // Header Label (added *after* the logo)
        JLabel headerLabel = new JLabel("Welcome to Airline Reservation System!", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20)); // Slightly larger font
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally
        northPanel.add(headerLabel); // Add the header text after the logo and spacing

        // Add the combined north panel to the main panel
        mainPanel.add(northPanel, BorderLayout.NORTH);


        // --- CENTER Panel (Input Fields) ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Consistent spacing
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make components fill horizontally

        // Username Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0; // Label doesn't expand
        gbc.anchor = GridBagConstraints.EAST; // Align label to the right
        centerPanel.add(new JLabel("Username:"), gbc);

        // Username Field
        userField = new JTextField(20); // Keep preferred size
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Field expands horizontally
        gbc.anchor = GridBagConstraints.WEST; // Align field to the left
        centerPanel.add(userField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        centerPanel.add(new JLabel("Password:"), gbc);

        // Password Field
        passField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(passField, gbc);

        // Login Button Panel (to center the button)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login \u2705");
        loginButton.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Slightly larger button font
        buttonPanel.add(loginButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span across both columns
        gbc.weightx = 0;   // Don't expand horizontally
        gbc.fill = GridBagConstraints.NONE; // Don't fill space
        gbc.anchor = GridBagConstraints.CENTER; // Center the button panel
        centerPanel.add(buttonPanel, gbc);

        // Add center panel to the main panel
        mainPanel.add(centerPanel, BorderLayout.CENTER);


        // --- SOUTH Panel (Options) ---
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Increased gap
        JLabel registerLabel = new JLabel("<HTML><U>Register</U></HTML>");
        registerLabel.setForeground(Color.BLUE.darker()); // Slightly darker blue
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel continueLabel = new JLabel("<HTML><U>Continue without logging in</U></HTML>");
        continueLabel.setForeground(Color.BLUE.darker());
        continueLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        optionsPanel.add(registerLabel);
        optionsPanel.add(continueLabel);

        // Add options panel to the south
        mainPanel.add(optionsPanel, BorderLayout.SOUTH);


        // Set default button for Enter key
        getRootPane().setDefaultButton(loginButton);

        // --- Action Listeners ---

        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Open Register UI on the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    RegisterUI registerUI = new RegisterUI();
                    registerUI.display(); // Use the display method
                });
            }
        });

        continueLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose(); // Close login window
                // Open User Dashboard (as guest) on the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    UserDashboardUI userDashboard = new UserDashboardUI(null); // Pass null for guest
                    userDashboard.display(); // Use the display method
                });
            }
        });

        loginButton.addActionListener(e -> handleLogin()); // Use separate method for login logic

        // Add main panel to the frame
        add(mainPanel);
    }

    // Method to handle the login logic
    private void handleLogin() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(LoginUI.this,
                    "Username and password cannot be empty!",
                    "Input Error", JOptionPane.WARNING_MESSAGE); // Changed to WARNING
            return;
        }

        // Database interaction
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByUsername(username);

            // IMPORTANT: Use secure password hashing in a real application!
            // This is a placeholder for demonstration.
            if (user != null && user.getPassword().equals(password)) {
                if (!user.isActive()) {
                    JOptionPane.showMessageDialog(LoginUI.this,
                            "Your account is suspended. Please contact your administrator.",
                            "Account Suspended", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Login successful - close this window and open the appropriate dashboard
                JOptionPane.showMessageDialog(LoginUI.this,
                        "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the login window

                // Open the correct dashboard on the EDT
                SwingUtilities.invokeLater(() -> {
                    if (user.isAdmin()) {
                        AdminDashboardUI adminDashboard = new AdminDashboardUI();
                        adminDashboard.setVisible(true); // Assuming AdminDashboard doesn't implement ARSView
                    } else {
                        UserDashboardUI userDashboard = new UserDashboardUI(user);
                        userDashboard.display(); // Use display method
                    }
                });

            } else {
                // Invalid credentials
                JOptionPane.showMessageDialog(LoginUI.this,
                        "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            // Database connection or query error
            JOptionPane.showMessageDialog(LoginUI.this,
                    "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Log the full stack trace for debugging
        } catch (Exception ex) {
            // Catch unexpected errors
            JOptionPane.showMessageDialog(LoginUI.this,
                    "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


     public static void main(String[] args) {
         // Ensure Look and Feel is set before creating components
         SwingUtilities.invokeLater(() -> {
             LoginUI login = new LoginUI();
             login.display();
         });
     }
}
