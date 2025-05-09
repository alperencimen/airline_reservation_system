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

public class AdminManageUsersUI extends JFrame {
    private final User adminUser;
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private final UserDAO userDAO = new UserDAO();

    public AdminManageUsersUI(User adminUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }

        this.adminUser = adminUser;
        setTitle("Manage Users");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setMinimumSize(new Dimension(700, 400));
        setLocationRelativeTo(null);
        initComponents();
        loadUsers();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel headerLabel = new JLabel("Manage Users", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{
                "ID", "Username", "Gender", "Age", "Country", "Active", "Admin"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usersTable = new JTable(tableModel);
        usersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(usersTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton suspendButton = new JButton("Suspend/Unsuspend User");
        suspendButton.addActionListener(e -> handleSuspendUser());
        
        JButton goBackButton = new JButton("Go Back");
        goBackButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new AdminDashboardUI(adminUser).setVisible(true));
        });

        buttonPanel.add(suspendButton);
        buttonPanel.add(goBackButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            tableModel.setRowCount(0);

            if (users.isEmpty()) {
                tableModel.addRow(new Object[]{"No users found.", "", "", "", "", "", ""});
            } else {
                for (User user : users) {
                    tableModel.addRow(new Object[]{
                            user.getId(),
                            user.getUsername(),
                            user.getGender(),
                            user.getAge(),
                            user.getCountry(),
                            user.isActive() ? "Yes" : "No",
                            user.isAdmin() ? "Yes" : "No"
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error loading users: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error loading users: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void handleSuspendUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to suspend/unsuspend.",
                    "No User Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        boolean isActive = tableModel.getValueAt(selectedRow, 5).equals("Yes");

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + (isActive ? "suspend" : "unsuspend") + " user '" + username + "'?",
                "Confirm Action", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                User user = userDAO.getUserById(userId);
                if (user != null) {
                    user.setActive(!isActive);
                    if (userDAO.updateUser(user)) {
                        JOptionPane.showMessageDialog(this,
                                "User '" + username + "' has been " + (isActive ? "suspended" : "unsuspended") + ".",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadUsers(); // Refresh the table
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to update user status.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
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