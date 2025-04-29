package com.airline.view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.airline.dao.BookingDAO;
import com.airline.model.Booking;
import com.airline.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserViewBooking extends JFrame {

    private User currentUser;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private BookingDAO bookingDAO = new BookingDAO();

    public UserViewBooking(User currentUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
        this.currentUser = currentUser;
        if (this.currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please log in to view your bookings!", "Login Required", JOptionPane.ERROR_MESSAGE);
            dispose();
            new LoginUI().setVisible(true);
            return;
        }
        setTitle("My Bookings");
        setIconImage(new ImageIcon(getClass().getResource("/images/ars_login.png")).getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        initComponents();
        loadBookings();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        JLabel headerLabel = new JLabel("My Bookings", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        tableModel = new DefaultTableModel(new Object[]{"Booking Ref", "Flight ID", "Seat", "Date"}, 0);
        bookingsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton goBackButton = new JButton("Go Back");
        southPanel.add(goBackButton);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        
        goBackButton.addActionListener(e -> {
            dispose();
            new UserDashboardUI(currentUser).setVisible(true);
        });
        
        add(mainPanel);
    }
    
    private void loadBookings() {
        try {
            List<Booking> bookings = bookingDAO.getUserBookings(currentUser.getId());
            tableModel.setRowCount(0);
            if (bookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No bookings found!", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                for (Booking booking : bookings) {
                    tableModel.addRow(new Object[]{
                        booking.getBookingReference(),
                        booking.getFlightId(),
                        booking.getSeatNumber(),
                        booking.getBookingDate().format(dtf)
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

/*
    //Uncomment this part only if you want to run the LoginUI without main code itself. (Visualization purposes)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserViewBooking(new User()).setVisible(true));
    }

 */

}
