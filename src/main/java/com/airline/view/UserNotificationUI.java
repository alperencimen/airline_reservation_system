package com.airline.view;

import com.airline.dao.NotificationDAO;
import com.airline.model.Notification;
import com.airline.model.User;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserNotificationUI extends JFrame {
    private final User currentUser;
    private JTable table;
    private DefaultTableModel tableModel;

    public UserNotificationUI(User currentUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception e) {
            System.err.println("Failed to load FlatLaf: " + e.getMessage());
        }

        this.currentUser = currentUser;

        setTitle("Notifications");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 400); // genişliği artırdım
        setLocationRelativeTo(null);

        initComponents();
        loadNotifications();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Your Notifications", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // "Read" sütunu kaldırıldı
        tableModel = new DefaultTableModel(new Object[]{"Message", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);

        // Genişlik ayarları
        table.getColumnModel().getColumn(0).setPreferredWidth(500); // Message kolonu geniş
        table.getColumnModel().getColumn(1).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton goBackButton = new JButton("Go Back");
        goBackButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new UserDashboardUI(currentUser).display());
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(goBackButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadNotifications() {
        try {
            NotificationDAO dao = new NotificationDAO();
            List<Notification> notifications = dao.getNotificationsByUserId(currentUser.getId());

            tableModel.setRowCount(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Notification n : notifications) {
                tableModel.addRow(new Object[]{
                        n.getMessage(),
                        n.getCreatedAt().format(formatter)
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading notifications: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        User testUser = new User();
        testUser.setId(6);
        testUser.setUsername("testuser");

        SwingUtilities.invokeLater(() -> new UserNotificationUI(testUser).setVisible(true));
    }
}



