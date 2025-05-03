package com.airline.view;

import com.airline.dao.BookingDAO;
import com.airline.model.Booking;
import com.airline.model.User;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminViewBookingsUI extends JFrame {
    private final User adminUser;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private final BookingDAO bookingDAO = new BookingDAO();

    public AdminViewBookingsUI(User adminUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }

        this.adminUser = adminUser;
        setTitle("All Bookings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(750, 450);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);

        initComponents();
        loadAllBookings();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel headerLabel = new JLabel("All Bookings", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Booking Ref", "User ID", "Flight ID", "Seat (Type)", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(tableModel);
        bookingsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Go Back");
        backButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new AdminDashboardUI(adminUser).setVisible(true));
        });
        southPanel.add(backButton);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadAllBookings() {
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            tableModel.setRowCount(0);

            if (bookings.isEmpty()) {
                tableModel.addRow(new Object[]{"No bookings found.", "", "", "", ""});
            } else {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                for (Booking booking : bookings) {
                    String seatInfo = booking.getSeatNumber();
                    if (booking.getSeatPreference() != null && !booking.getSeatPreference().isEmpty()) {
                        String preference = booking.getSeatPreference().substring(0, 1).toUpperCase() +
                                booking.getSeatPreference().substring(1).toLowerCase();
                        seatInfo += " (" + preference + ")";
                    }
                    tableModel.addRow(new Object[]{
                            booking.getBookingReference(),
                            booking.getUserId(),
                            booking.getFlightId(),
                            seatInfo,
                            booking.getBookingDate().format(dtf)
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error loading bookings: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}

