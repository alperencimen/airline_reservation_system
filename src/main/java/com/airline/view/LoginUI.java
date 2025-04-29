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
    private JTextField userField;
    private JPasswordField passField;

    @Override
    public void display() {
        // Ensure UI updates are done on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

    public LoginUI() {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize FlatLaf LaF: " + e.getMessage());
            // Handle error or fallback to default L&F if needed
        } catch (Exception ex) { // Catch other potential exceptions
            System.err.println("An unexpected error occurred during Look and Feel setup: " + ex.getMessage());
        }


        setTitle("ARS - Login Screen");

        setSize(650, 500);
        setMinimumSize(new Dimension(550, 450)); // Adjust minimum size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadWindowIcon();
        initComponents();
    }

    // Helper method to load the window icon
    private void loadWindowIcon() {
        String iconPath = "/images/ars_login.png";//ars_login image is used for the login screen.
        try {
            // Get the URL for the resource
            URL iconUrl = getClass().getResource(iconPath);
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
        // Main panel with border layout and paddings
        JPanel mainPanel = new JPanel(new BorderLayout(10, 15)); // Increased vertical gap
        // Reduced top padding due to logo. increased side padding
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        // Logo + Header Text Panel
        JPanel northPanel = new JPanel();

        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setOpaque(false); //make transparant when true

        String logoPath = "/images/ars_login.png";
        try {
            URL logoUrl = getClass().getResource(logoPath);
            if (logoUrl != null) {
                ImageIcon originalLogoIcon = new ImageIcon(logoUrl);

                // Check if the original image loaded correctly
                if (originalLogoIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    // Scaling the image
                    Image originalImage = originalLogoIcon.getImage();
                    int desiredWidth = 480;
                    int desiredHeight = -1; // Keep aspect ratio (-1)
                    Image scaledImage = originalImage.getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH); // SCALE_SMOOTH provides better scaling
                    ImageIcon scaledLogoIcon = new ImageIcon(scaledImage);

                    // Create JLabel with the icon
                    JLabel logoLabel = new JLabel(scaledLogoIcon);
                    logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    // Set maximum size to prevent excessive vertical stretching if BoxLayout misbehaves
                    logoLabel.setMaximumSize(new Dimension(desiredWidth, scaledLogoIcon.getIconHeight()));
                    northPanel.add(logoLabel);
                    // Add some vertical spacing between logo and text
                    northPanel.add(Box.createRigidArea(new Dimension(0, 15)));

                } else {
                    System.err.println("Logo image '" + logoPath + "' found but failed to load for display.");
                }
            } else {
                System.err.println("Logo image '" + logoPath + "' not found in resources.");
                // add placeholder text if logo fails to be loaded.
                JLabel placeholder = new JLabel("[Logo Not Found at " + logoPath + "]");
                placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
                northPanel.add(placeholder);
                northPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } catch (Exception e) {
            System.err.println("Error loading logo '" + logoPath + "' for display: " + e.getMessage());
            e.printStackTrace();
        }

        // Header Label
        JLabel headerLabel = new JLabel("Login to Your ARS Account", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        northPanel.add(headerLabel); // Add the header text after the logo and spacing

        // Add the combined north panel to the main panel
        mainPanel.add(northPanel, BorderLayout.NORTH);


        // Input Fields
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make components fill horizontally

        // Username Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST; // Align label to the right
        centerPanel.add(new JLabel("Username:"), gbc);

        // Username Field
        userField = new JTextField(20);
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

        // Login Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        // Add padding to the button itself
        loginButton.setMargin(new Insets(5, 15, 5, 15));
        buttonPanel.add(loginButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;   // Don't expand horizontally
        gbc.fill = GridBagConstraints.NONE; // Don't fill space
        gbc.anchor = GridBagConstraints.CENTER;
        // Add extra padding above the button
        gbc.insets = new Insets(20, 8, 10, 8);
        centerPanel.add(buttonPanel, gbc);
        gbc.insets = new Insets(10, 8, 10, 8); // Reset insets for next component
        mainPanel.add(centerPanel, BorderLayout.CENTER);


        // Options (bottom)
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        Font optionsFont = new Font("SansSerif", Font.PLAIN, 14);

        JLabel registerLabel = new JLabel("<HTML><U>Register</U></HTML>");
        registerLabel.setFont(optionsFont);
        registerLabel.setForeground(Color.BLUE.darker());
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel continueLabel = new JLabel("<HTML><U>Continue without logging in</U></HTML>");
        continueLabel.setForeground(Color.BLUE.darker());
        continueLabel.setFont(optionsFont);
        continueLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        optionsPanel.add(registerLabel);
        optionsPanel.add(continueLabel);
        // Add options panel to the south panel
        mainPanel.add(optionsPanel, BorderLayout.SOUTH);
        // Set default button for Enter key
        getRootPane().setDefaultButton(loginButton);


        // Actions
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    RegisterUI registerUI = new RegisterUI();
                    registerUI.display();
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
                    userDashboard.display();
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

        // validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(LoginUI.this,
                    "Username and password fields cannot be empty!",
                    "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Database interaction
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByUsername(username);

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
                dispose();

                // Open the correct dashboard
                SwingUtilities.invokeLater(() -> {
                    if (user.isAdmin()) {
                        AdminDashboardUI adminDashboard = new AdminDashboardUI();
                        adminDashboard.setVisible(true);
                    } else {
                        UserDashboardUI userDashboard = new UserDashboardUI(user);
                        userDashboard.display();
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
            ex.printStackTrace();
        } catch (Exception ex) {
            // Catch unexpected errors
            JOptionPane.showMessageDialog(LoginUI.this,
                    "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

/*
//Uncommand this part only if you want to run the LoginUI without main code itself. (Visualization purposes)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginUI login = new LoginUI();
            login.display();
        });
    }

 */
}
