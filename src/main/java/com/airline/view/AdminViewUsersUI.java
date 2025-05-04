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

public class AdminViewUsersUI extends JFrame {

    protected JTable usersTable;
    protected User adminUser;
    protected DefaultTableModel tableModel;
    protected UserDAO userDAO = new UserDAO();

    public AdminViewUsersUI() {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }

        setTitle("View All Users");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setMinimumSize(new Dimension(700, 400));
        setLocationRelativeTo(null);
        initComponents();
        loadUsers();
    }

    public AdminViewUsersUI(User adminUser) {
        this();
        this.adminUser = adminUser;
    }

    protected void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel headerLabel = new JLabel("All Registered Users", SwingConstants.CENTER);
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
        JScrollPane scrollPane = new JScrollPane(usersTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton goBackButton = new JButton("Go Back");
        goBackButton.addActionListener(e -> {
            dispose();
            if (adminUser != null) {
            SwingUtilities.invokeLater(() -> new AdminDashboardUI(adminUser).setVisible(true));
            } else {
                SwingUtilities.invokeLater(() -> new AdminDashboardUI(new User()).setVisible(true));
            }
        });
        southPanel.add(goBackButton);

        mainPanel.add(southPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    protected void loadUsers() {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminViewUsersUI().setVisible(true));
    }
}

