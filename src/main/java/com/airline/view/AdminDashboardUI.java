package com.airline.view;

import com.airline.model.User;
import com.airline.view.LoginUI;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

public class AdminDashboardUI extends JFrame {

    private User adminUser;

    public AdminDashboardUI(User adminUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }

        this.adminUser = adminUser;
        setTitle("Admin Dashboard");
        loadWindowIcon();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 550);
        setMinimumSize(new Dimension(550, 500));
        setLocationRelativeTo(null);
        initComponents();
    }

    private void loadWindowIcon() {
        String iconPath = "/images/ars_login.png";
        try {
            URL iconUrl = getClass().getResource(iconPath);
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    setIconImage(icon.getImage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
    }


    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setOpaque(false);

        JLabel headerLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        northPanel.add(headerLabel);

        if (adminUser != null && adminUser.getUsername() != null) {
            JLabel greetingLabel = new JLabel("Welcome back, Admin " + adminUser.getUsername() + "!", SwingConstants.CENTER);
            greetingLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            greetingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            northPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            northPanel.add(greetingLabel);
        }

        mainPanel.add(northPanel, BorderLayout.NORTH);

        // Load and add Dashboard Banner
        String bannerPath = "/images/ars_login_admin.png";
        try {
            URL bannerUrl = getClass().getResource(bannerPath);
            if (bannerUrl != null) {
                ImageIcon originalBannerIcon = new ImageIcon(bannerUrl);
                if (originalBannerIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    Image originalImage = originalBannerIcon.getImage();
                    int desiredWidth = 480;
                    int desiredHeight = -1; // Keep aspect ratio
                    Image scaledImage = originalImage.getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH);
                    ImageIcon scaledBannerIcon = new ImageIcon(scaledImage);

                    JLabel bannerLabel = new JLabel(scaledBannerIcon);
                    bannerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    bannerLabel.setMaximumSize(new Dimension(desiredWidth, scaledBannerIcon.getIconHeight()));
                    northPanel.add(bannerLabel); // Add banner first
                    northPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                } else {
                    System.err.println("Dashboard banner '" + bannerPath + "' found but failed to load.");
                }
            } else {
                System.err.println("Dashboard banner '" + bannerPath + "' not found in resources.");
            }
        } catch (Exception e) {
            System.err.println("Error loading dashboard banner '" + bannerPath + "': " + e.getMessage());
            e.printStackTrace();
        }

        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton addFlightButton = new JButton("Add Flight");
        addFlightButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new AdminAddFlight(adminUser).setVisible(true));
        });
        JButton cancelFlightButton = new JButton("Cancel Flight");
        cancelFlightButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new AdminCancelFlight(adminUser).setVisible(true));
        });
        JButton manageUsersButton = new JButton("Manage Users");
        manageUsersButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new AdminManageUsersUI(adminUser).setVisible(true));
        });

        JButton viewBookingsButton = new JButton("View Bookings");
        viewBookingsButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new AdminViewBookingsUI(adminUser).setVisible(true));
        });
        JButton logoutButton = new JButton("Logout");

        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);
        addFlightButton.setFont(buttonFont);
        cancelFlightButton.setFont(buttonFont);
        manageUsersButton.setFont(buttonFont);
        viewBookingsButton.setFont(buttonFont);
        logoutButton.setFont(buttonFont);

        centerPanel.add(addFlightButton);
        centerPanel.add(cancelFlightButton);
        centerPanel.add(manageUsersButton);
        centerPanel.add(viewBookingsButton);
        centerPanel.add(logoutButton);

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        logoutButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginUI().display());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User dummyAdmin = new User();
            dummyAdmin.setUsername("AdminTest");
            dummyAdmin.setAdmin(true);
            new AdminDashboardUI(dummyAdmin).setVisible(true);
        });
    }
}

