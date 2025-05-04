package com.airline.view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.airline.dao.UserDAO;
import com.airline.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.SQLException;

public class AdminLoginUI extends JFrame implements ARSView {
    private JTextField adminUserField;
    private JPasswordField adminPassField;

    @Override
    public void display() {
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

    public AdminLoginUI() {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize FlatLaf LaF: " + e.getMessage());
        } catch (Exception ex) {
            System.err.println("An unexpected error occurred during Look and Feel setup: " + ex.getMessage());
        }

        setTitle("ARS - Admin Login Screen");

        // Increased default and minimum height
        setSize(650, 600); // Increased height
        setMinimumSize(new Dimension(600, 550)); // Increased minimum height
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose, don't exit app
        setLocationRelativeTo(null);

        loadWindowIcon();
        initComponents();
    }

    private void loadWindowIcon() {
        // Use the same login logo for consistency, or create a specific admin logo
        String iconPath = "/images/ars_login_admin.png";
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
            e.printStackTrace();
        }
    }


    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setOpaque(false); //make transparant when true

        String logoPath = "/images/ars_login_admin.png"; // Using same logo
        try {
            URL logoUrl = getClass().getResource(logoPath);
            if (logoUrl != null) {
                ImageIcon originalLogoIcon = new ImageIcon(logoUrl);

                if (originalLogoIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    Image originalImage = originalLogoIcon.getImage();
                    int desiredWidth = 480; // Same width as LoginUI
                    int desiredHeight = -1;
                    Image scaledImage = originalImage.getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH); // SCALE_SMOOTH provides better scaling
                    ImageIcon scaledLogoIcon = new ImageIcon(scaledImage);

                    JLabel logoLabel = new JLabel(scaledLogoIcon);
                    logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    logoLabel.setMaximumSize(new Dimension(desiredWidth, scaledLogoIcon.getIconHeight()));
                    northPanel.add(logoLabel);
                    northPanel.add(Box.createRigidArea(new Dimension(0, 15)));

                } else {
                    System.err.println("Logo image '" + logoPath + "' found but failed to load for display.");
                }
            } else {
                System.err.println("Logo image '" + logoPath + "' not found in resources.");
                JLabel placeholder = new JLabel("[Logo Not Found at " + logoPath + "]"); // add placeholder text if logo fails to be loaded.
                placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
                northPanel.add(placeholder);
                northPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } catch (Exception e) {
            System.err.println("Error loading logo '" + logoPath + "' for display: " + e.getMessage());
            e.printStackTrace();
        }

        JLabel headerLabel = new JLabel("Administrator Login", SwingConstants.CENTER); // Changed header
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        northPanel.add(headerLabel);

        mainPanel.add(northPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        centerPanel.add(new JLabel("Admin Username:"), gbc); // Changed label text

        adminUserField = new JTextField(); // Removed column argument
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(adminUserField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        centerPanel.add(new JLabel("Admin Password:"), gbc); // Changed label text

        adminPassField = new JPasswordField(); // Removed column argument
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(adminPassField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton adminLoginButton = new JButton("Admin Login"); // Changed button text
        adminLoginButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        adminLoginButton.setMargin(new Insets(5, 15, 5, 15));
        buttonPanel.add(adminLoginButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 8, 10, 8);
        centerPanel.add(buttonPanel, gbc);
        gbc.insets = new Insets(10, 8, 10, 8);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- SOUTH Panel (Only Back Button) ---
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Centered layout
        JButton backButton = new JButton("Back to User Login");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        backButton.setMargin(new Insets(5, 15, 5, 15));
        southPanel.add(backButton);

        mainPanel.add(southPanel, BorderLayout.SOUTH); // Add south panel
        getRootPane().setDefaultButton(adminLoginButton); // Default button is admin login

        // --- Action Listeners ---
        adminLoginButton.addActionListener(e -> handleAdminLogin()); // Link to admin login handler

        backButton.addActionListener(e -> {
            dispose(); // Close this admin login window
            SwingUtilities.invokeLater(() -> {
                LoginUI loginUI = new LoginUI(); // Go back to the main login screen
                loginUI.display();
            });
        });

        add(mainPanel);
    }

    // Method to handle the ADMIN login logic
    private void handleAdminLogin() {
        String username = adminUserField.getText().trim();
        String password = new String(adminPassField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Admin username and password cannot be empty!",
                    "Admin Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByUsername(username);

            // Check if user exists, IS an admin, and password matches
            if (user != null && user.isAdmin() && user.getPassword().equals(password)) {
                if (!user.isActive()) {
                    JOptionPane.showMessageDialog(this,
                            "Admin account is suspended.",
                            "Account Suspended", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(this,
                        "Admin Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close admin login window

                // Open Admin Dashboard
                SwingUtilities.invokeLater(() -> {
                    AdminDashboardUI adminDashboard = new AdminDashboardUI(user);
                    adminDashboard.setVisible(true); // Or display() if it implements ARSView
                });

            } else {
                // Invalid admin credentials
                JOptionPane.showMessageDialog(this,
                        "Invalid Admin username or password!", "Admin Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error during admin login: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


//Uncomment this part only if you want to run the AdminLoginUI without main code itself. (Visualization purposes)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminLoginUI adminLogin = new AdminLoginUI();
            adminLogin.display();
        });
    }

}
