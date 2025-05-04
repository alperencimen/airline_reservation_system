package com.airline.view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.airline.dao.UserDAO;
import com.airline.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.SQLException;

public class LoginUI extends JFrame implements ARSView {
    private JTextField userField;
    private JPasswordField passField;

    @Override
    public void display() {
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

    public LoginUI() {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize FlatLaf LaF: " + e.getMessage());
        } catch (Exception ex) {
            System.err.println("An unexpected error occurred during Look and Feel setup: " + ex.getMessage());
        }


        setTitle("ARS - Login Screen");

        setSize(650, 500);
        setMinimumSize(new Dimension(550, 450));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadWindowIcon();
        initComponents();
    }

    private void loadWindowIcon() {
        String iconPath = "/images/ars_login.png"; //ars_login image is used for the login screen.
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

        String logoPath = "/images/ars_login.png";
        try {
            URL logoUrl = getClass().getResource(logoPath);
            if (logoUrl != null) {
                ImageIcon originalLogoIcon = new ImageIcon(logoUrl);

                if (originalLogoIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    Image originalImage = originalLogoIcon.getImage();
                    int desiredWidth = 480;
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

        JLabel headerLabel = new JLabel("Login to Your ARS Account", SwingConstants.CENTER);
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
        centerPanel.add(new JLabel("Username:"), gbc);

        userField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        centerPanel.add(new JLabel("Password:"), gbc);

        passField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(passField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        loginButton.setMargin(new Insets(5, 15, 5, 15));
        buttonPanel.add(loginButton);

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

        JLabel adminLoginLabel = new JLabel("<HTML><U>Admin Login</U></HTML>");
        adminLoginLabel.setFont(optionsFont);
        adminLoginLabel.setForeground(Color.RED.darker());
        adminLoginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        optionsPanel.add(registerLabel);
        optionsPanel.add(continueLabel);
        optionsPanel.add(adminLoginLabel);

        mainPanel.add(optionsPanel, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(loginButton);

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
                dispose();
                SwingUtilities.invokeLater(() -> {
                    UserDashboardUI userDashboard = new UserDashboardUI(null);
                    userDashboard.display();
                });
            }
        });

        adminLoginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose(); // Close the current LoginUI
                SwingUtilities.invokeLater(() -> {
                    // Assuming AdminLoginUI exists and has a constructor and a display method
                    AdminLoginUI adminLogin = new AdminLoginUI();
                    adminLogin.display(); // Or adminLogin.setVisible(true);
                });
            }
        });


        loginButton.addActionListener(e -> handleLogin());

        add(mainPanel);
    }

    private void handleLogin() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(LoginUI.this,
                    "Username and password fields cannot be empty!",
                    "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByUsername(username);

            if (user != null && !user.isAdmin() && user.getPassword().equals(password)) {//If the passed username and password does not belong to an admin, proceed.
                if (!user.isActive()) {
                    JOptionPane.showMessageDialog(LoginUI.this,
                            "Your account is suspended. Please contact your administrator.",
                            "Account Suspended", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(LoginUI.this,
                        "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();

                SwingUtilities.invokeLater(() -> {
                    UserDashboardUI userDashboard = new UserDashboardUI(user);
                    userDashboard.display();
                });

            } else if (user != null && user.isAdmin()) {
                JOptionPane.showMessageDialog(LoginUI.this,
                        "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
            else {
                JOptionPane.showMessageDialog(LoginUI.this,
                        "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(LoginUI.this,
                    "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(LoginUI.this,
                    "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

/*
//Uncomment this part only if you want to run the LoginUI without main code itself. (Visualization purposes)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginUI login = new LoginUI();
            login.display();
        });
    }

 */
}
