package com.airline.ui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.airline.dao.UserDAO;
import com.airline.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginUI extends JFrame {

    public LoginUI() {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
    
        setTitle("ARS");
        setIconImage(new ImageIcon(getClass().getResource("ars.png")).getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Airline Reservation System", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(20);
        userPanel.add(userLabel);
        userPanel.add(userField);
        centerPanel.add(userPanel);
        
        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);
        passPanel.add(passLabel);
        passPanel.add(passField);
        centerPanel.add(passPanel);
        
        JButton loginButton = new JButton("Login \u2705");
        centerPanel.add(loginButton);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel registerLabel = new JLabel("<HTML><U>Register</U></HTML>");
        registerLabel.setForeground(Color.BLUE);
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel continueLabel = new JLabel("<HTML><U>Continue without logging in</U></HTML>");
        continueLabel.setForeground(Color.BLUE);
        continueLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        optionsPanel.add(registerLabel);
        optionsPanel.add(continueLabel);
        centerPanel.add(optionsPanel);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> new RegisterUI().setVisible(true));
            }
        });
        
        continueLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new UserDashboardUI(null).setVisible(true);
            }
        });
        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText().trim();
                String password = new String(passField.getPassword()).trim();
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginUI.this, "Username and password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    UserDAO userDAO = new UserDAO();
                    User user = userDAO.getUserByUsername(username);
                    if (user != null && user.getPassword().equals(password)) {
                        if (!user.isActive()) {
                            JOptionPane.showMessageDialog(LoginUI.this, "Your account is suspended. Please contact your administrator.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        JOptionPane.showMessageDialog(LoginUI.this, "Login successful!", "Info", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        if (user.isAdmin()) {
                            new AdminDashboardUI().setVisible(true);
                        } else {
                            new UserDashboardUI(user).setVisible(true);
                        }
                    } else {
                        JOptionPane.showMessageDialog(LoginUI.this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(LoginUI.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        add(mainPanel);
    }
}
