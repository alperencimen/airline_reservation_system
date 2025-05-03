package com.airline.view;

import com.airline.dao.UserDAO;
import com.airline.model.User;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class SettingsUI extends JFrame {
    private final User currentUser;
    private final UserDAO userDAO = new UserDAO();

    public SettingsUI(User currentUser) {
        this.currentUser = currentUser;

        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception e) {
            System.err.println("Failed to load FlatIntelliJLaf: " + e.getMessage());
        }

        setTitle("Settings - Seat Preference");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 20, 20, 20));

        JLabel headerLabel = new JLabel("Set Your Default Seat Preference", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(headerLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel preferenceLabel = new JLabel("Seat Preference:");

        String[] options = {"", "WINDOW", "AISLE"};
        JComboBox<String> seatBox = new JComboBox<>(options);

        if (currentUser.getDefaultSeatPreference() != null && !currentUser.getDefaultSeatPreference().isEmpty()) {
            seatBox.setSelectedItem(currentUser.getDefaultSeatPreference());
        } else {
            seatBox.setSelectedIndex(0);
        }

        centerPanel.add(preferenceLabel);
        centerPanel.add(seatBox);
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton backButton = new JButton("Go Back");

        saveButton.addActionListener(e -> {
            String selected = (String) seatBox.getSelectedItem();

            if (selected == null || selected.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a seat preference before saving.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            currentUser.setDefaultSeatPreference(selected);
            try {
                boolean success = userDAO.updateUserPreference(currentUser);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Preference saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save preference.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new UserDashboardUI(currentUser).display());
        });

        bottomPanel.add(saveButton);
        bottomPanel.add(backButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);
    }


    public static void main(String[] args) {
        User dummyUser = new User();
        dummyUser.setId(1);
        dummyUser.setUsername("TestUser");
        dummyUser.setDefaultSeatPreference("WINDOW");
        SwingUtilities.invokeLater(() -> new SettingsUI(dummyUser).setVisible(true));
    }
}
