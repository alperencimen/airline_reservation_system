package com.airline.view;

import com.airline.dao.UserDAO;
import com.airline.model.User;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AdminManageUsersUI extends AdminViewUsersUI {
    private JButton suspendButton;
    private JButton unsuspendButton;
    private UserDAO userDAO = new UserDAO();

    public AdminManageUsersUI(User adminUser) {
        super(adminUser);
        setTitle("Manage Users");
        addActionButtons();
    }

    private void addActionButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        suspendButton = new JButton("Suspend User");
        suspendButton.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow >= 0) {
                int userId = (int) tableModel.getValueAt(selectedRow, 0);
                try {
                    if (userDAO.updateUserStatus(userId, false)) {
                        JOptionPane.showMessageDialog(this,
                                "User has been suspended successfully.",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to suspend user.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error suspending user: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a user to suspend.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        unsuspendButton = new JButton("Unsuspend User");
        unsuspendButton.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow >= 0) {
                int userId = (int) tableModel.getValueAt(selectedRow, 0);
                try {
                    if (userDAO.updateUserStatus(userId, true)) {
                        JOptionPane.showMessageDialog(this,
                                "User has been unsuspended successfully.",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to unsuspend user.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error unsuspending user: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a user to unsuspend.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton goBackButton = new JButton("Go Back");
        goBackButton.addActionListener(e -> {
            dispose();
            if (adminUser != null) {
                SwingUtilities.invokeLater(() -> new AdminDashboardUI(adminUser).setVisible(true));
            } else {
                SwingUtilities.invokeLater(() -> new AdminDashboardUI(new User()).setVisible(true));
            }
        });

        buttonPanel.add(suspendButton);
        buttonPanel.add(unsuspendButton);
        buttonPanel.add(Box.createHorizontalStrut(20)); // Add some space
        buttonPanel.add(goBackButton);

        JPanel mainPanel = (JPanel) getContentPane().getComponent(0);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User dummyAdmin = new User();
            dummyAdmin.setUsername("AdminTest");
            dummyAdmin.setAdmin(true);
            new AdminManageUsersUI(dummyAdmin).setVisible(true);
        });
    }
} 