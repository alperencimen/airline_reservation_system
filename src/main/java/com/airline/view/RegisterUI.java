package com.airline.view;

import com.airline.dao.UserDAO;
import com.airline.model.User;
import javax.swing.*;
import java.awt.*;
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
        setTitle("Register - Airline Reservation System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Register", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // ===== FORM PANEL WITH GRIDBAGLAYOUT FOR ALIGNMENT =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 0: Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Username:"), gbc);
        
        userField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(userField, gbc);
        
        // Row 1: Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Password:"), gbc);
        
        passField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passField, gbc);
        
        // Row 2: Gender (ComboBox with default dummy option)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Gender:"), gbc);
        
        String[] genderOptions = {"Select Gender", "Male", "Female", "Other"};
        genderCombo = new JComboBox<>(genderOptions);
        // Optionally, force the dummy option as selected
        genderCombo.setSelectedIndex(0);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(genderCombo, gbc);
        
        // Row 3: Age
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Age:"), gbc);
        
        ageField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(ageField, gbc);
        
        // Row 4: Country (ComboBox with default dummy option)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Country:"), gbc);
        
        // Generate a sorted list of country names from ISO codes
        String[] countries = Arrays.stream(Locale.getISOCountries())
                                   .map(code -> new Locale("", code).getDisplayCountry(Locale.ENGLISH))
                                   .sorted()
                                   .toArray(String[]::new);
        // Create an ArrayList and add a dummy option at the beginning
        ArrayList<String> countryList = new ArrayList<>();
        countryList.add("Select Country");
        countryList.addAll(Arrays.asList(countries));
        countryCombo = new JComboBox<>(countryList.toArray(new String[0]));
        countryCombo.setSelectedIndex(0);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(countryCombo, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // ===== BUTTONS PANEL (Register, Cancel) =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set default button for Enter key activation
        getRootPane().setDefaultButton(registerButton);
        
        // Register button action
        registerButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String gender = (String) genderCombo.getSelectedItem();
            String ageText = ageField.getText().trim();
            String country = (String) countryCombo.getSelectedItem();
            
            // Validate that a proper option is selected
            if (username.isEmpty() || password.isEmpty() || gender.equals("Select Gender") 
                    || ageText.isEmpty() || country.equals("Select Country")) {
                JOptionPane.showMessageDialog(RegisterUI.this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int age;
            try {
                age = Integer.parseInt(ageText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(RegisterUI.this, "Age must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setGender(gender);
            user.setAge(age);
            user.setCountry(country);
            user.setAdmin(false);
            user.setActive(true);
            
            try {
                UserDAO userDAO = new UserDAO();
                if (userDAO.createUser(user)) {
                    JOptionPane.showMessageDialog(RegisterUI.this, "Registration successful!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // close the registration window
                } else {
                    JOptionPane.showMessageDialog(RegisterUI.this, "Registration failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(RegisterUI.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Cancel button action
        cancelButton.addActionListener(e -> dispose());
        
        add(mainPanel);
    }

    @Override
    public void display() {
        this.setVisible(true);
    }
}
