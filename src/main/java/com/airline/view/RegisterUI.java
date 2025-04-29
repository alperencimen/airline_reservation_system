package com.airline.view;

import com.airline.dao.UserDAO;
import com.airline.model.User;
import com.formdev.flatlaf.FlatIntelliJLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class RegisterUI extends JFrame implements ARSView {
    private JTextField userField;
    private JPasswordField passField;
    private JComboBox<String> genderCombo;
    private JTextField ageField;
    private JComboBox<String> countryCombo;

    public RegisterUI() {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize FlatLaf LaF: " + e.getMessage());
        } catch (Exception ex) { // Catch other potential exceptions
            System.err.println("An unexpected error occurred during Look and Feel setup: " + ex.getMessage());
        }

        setTitle("ARS - Register Screen");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close, don't exit app
        setSize(650, 650);
        setMinimumSize(new Dimension(550, 500));
        setLocationRelativeTo(null); // Center on screen

        loadWindowIcon();
        initComponents();
    }

    // Helper method to load the window icon
    private void loadWindowIcon() {
        String iconPath = "/images/ars_register.png";
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
                // if register icon not found
                System.err.println("Window Icon '" + iconPath + "' not found in resources. Trying common logo...");
                iconPath = "/images/ars_login.png"; // Try the login logo for convinience
                iconUrl = getClass().getResource(iconPath);
                if (iconUrl != null) {
                    ImageIcon icon = new ImageIcon(iconUrl);
                    if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                        setIconImage(icon.getImage());
                    }
                } else {
                    System.err.println("Common Window Icon '" + iconPath + "' also not found.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading window icon: " + e.getMessage());
        }
    }

    private void initComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        // Adjusted padding
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        //Logo + Header Text
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setOpaque(false);
        // Add padding below the header text field
        northPanel.setBorder(new EmptyBorder(0, 0, 10, 0));


        // Path for the logo image -use the specific one or login-
        String logoPath = "/images/ars_register.png";
        URL logoUrl = getClass().getResource(logoPath);
        if (logoUrl == null) {
            System.err.println("Register logo '" + logoPath + "' not found. Trying common logo...");
            logoPath = "/images/ars_login.png"; //if not found use the login image.
            logoUrl = getClass().getResource(logoPath);
        }

        // Load, scale, and add Logo Label
        try {
            if (logoUrl != null) {
                ImageIcon originalLogoIcon = new ImageIcon(logoUrl);
                if (originalLogoIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    Image originalImage = originalLogoIcon.getImage();
                    int desiredWidth = 480;
                    int desiredHeight = -1; // Keep aspect ratio
                    Image scaledImage = originalImage.getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH);
                    ImageIcon scaledLogoIcon = new ImageIcon(scaledImage);

                    // Create JLabel with the scaled icon
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
                // placeholder if logo fails
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
        JLabel headerLabel = new JLabel("Register a New Account", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        northPanel.add(headerLabel);

        // Add the combined north upper panel to the main panel
        mainPanel.add(northPanel, BorderLayout.NORTH);


        // Forming the panel for alignment
        JPanel formPanel = new JPanel(new GridBagLayout());
        // Add some padding around the form itself
        formPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Username Label
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Username:"), gbc);
        // Row 0: Username Field
        userField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(userField, gbc);

        // Row 1: Password Label
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Password:"), gbc);
        // Row 1: Password Field
        passField = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passField, gbc);

        // Row 2: Gender Label
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Gender:"), gbc);
        // Row 2: Gender ComboBox
        String[] genderOptions = {"Select Gender", "Male", "Female", "Other"};
        genderCombo = new JComboBox<>(genderOptions);
        genderCombo.setSelectedIndex(0); // Ensure default is selected at the begining
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(genderCombo, gbc);

        // Row 3: Age Label
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Age:"), gbc);
        // Row 3: Age Field
        ageField = new JTextField(5);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(ageField, gbc);

        // Row 4: Country Label
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Country:"), gbc);
        // Row 4: Country ComboBox
        String[] countries = Arrays.stream(Locale.getISOCountries())
                .map(code -> new Locale("", code).getDisplayCountry(Locale.ENGLISH))
                .sorted()
                .toArray(String[]::new);
        ArrayList<String> countryList = new ArrayList<>();
        countryList.add("Select Country"); // Default option
        countryList.addAll(Arrays.asList(countries));
        countryCombo = new JComboBox<>(countryList.toArray(new String[0]));
        countryCombo.setSelectedIndex(0); // Ensure default is selected at the begining
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(countryCombo, gbc);

        // Register, Cancel button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        registerButton.setMargin(new Insets(5, 15, 5, 15));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cancelButton.setMargin(new Insets(5, 15, 5, 15));
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        // Combine Form and Buttons for Scrolling
        JPanel scrollableContent = new JPanel(new BorderLayout(0, 10));
        scrollableContent.add(formPanel, BorderLayout.CENTER);
        scrollableContent.add(buttonPanel, BorderLayout.SOUTH);

        // Create Scroll Pane
        JScrollPane scrollPane = new JScrollPane(scrollableContent);

        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Set default button for Enter key activation
        getRootPane().setDefaultButton(registerButton);

        // Action Listener
        registerButton.addActionListener(this::handleRegistration);
        cancelButton.addActionListener(e -> dispose());

        // Add main panel to the frame
        add(mainPanel);
    }

    // Method to handle the registration logic
    private void handleRegistration(ActionEvent e) {
        // Retrieve data from fields
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();
        String gender = (String) genderCombo.getSelectedItem();
        String ageText = ageField.getText().trim();
        String country = (String) countryCombo.getSelectedItem();

        // Input Validation
        StringBuilder errors = new StringBuilder();
        if (username.isEmpty()) errors.append("- Username is required.\n");
        if (password.isEmpty()) errors.append("- Password is required.\n");
        // Check if a valid gender is selected
        if (gender == null || gender.equals("Select Gender")) errors.append("- Please select a gender.\n");
        if (ageText.isEmpty()) errors.append("- Age is required.\n");
        // Check if a valid country is selected
        if (country == null || country.equals("Select Country")) errors.append("- Please select a country.\n");

        int age = -1; // Initialize age
        if (!ageText.isEmpty()) {
            try {
                age = Integer.parseInt(ageText);
                if (age <= 0 || age > 120) { //upper limit of age is 120.
                    errors.append("- Please enter a valid age.\n");
                }
            } catch (NumberFormatException ex) {
                errors.append("- Age must be a valid number.\n");
            }
        }

        // If there are validation errors, show them and return
        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(RegisterUI.this,
                    "Please correct the following errors:\n" + errors.toString(),
                    "Registration Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create User object
        User user = new User();
        user.setUsername(username);

        user.setPassword(password);
        user.setGender(gender);
        user.setAge(age);
        user.setCountry(country);
        user.setAdmin(false); // New users are not admins
        user.setActive(true); // New users are active by default

        // Database interaction
        try {
            UserDAO userDAO = new UserDAO();
            if (userDAO.getUserByUsername(username) != null) {
                JOptionPane.showMessageDialog(RegisterUI.this,
                        "Username '" + username + "' already exists. Please choose another.",
                        "Registration Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }


            if (userDAO.createUser(user)) {
                JOptionPane.showMessageDialog(RegisterUI.this,
                        "Registration successful! You can now log in.",
                        "Registration Complete", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the registration window
            } else {
                // This might indicate an unexpected DB issue if createUser returns false
                JOptionPane.showMessageDialog(RegisterUI.this,
                        "Registration failed due to an unexpected error.",
                        "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            // Handle specific SQL errors if needed (duplicate entry if check above fails)
            JOptionPane.showMessageDialog(RegisterUI.this,
                    "Database error during registration: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Log for debugging further
        } catch (Exception ex) {
            // Catch any other unexpected errors
            JOptionPane.showMessageDialog(RegisterUI.this,
                    "An unexpected error occurred: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void display() {
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

/*
//Uncommand this part only if you want to run the RegisterUI without main code itself. (for Visualization purposes)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegisterUI register = new RegisterUI();
            register.display();
        });
    }
 */
}
