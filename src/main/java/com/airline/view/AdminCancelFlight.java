package com.airline.view;

import com.airline.dao.BookingDAO;
import com.airline.dao.FlightDAO;
import com.airline.dao.NotificationDAO;
import com.airline.model.Booking;
import com.airline.model.Flight;
import com.airline.model.User;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class AdminCancelFlight extends JFrame {
    private final User adminUser;
    private JTextField flightNumberField;
    private final FlightDAO flightDAO = new FlightDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private JTable flightsTable;
    private DefaultTableModel tableModel;

    public AdminCancelFlight(User adminUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
        this.adminUser = adminUser;

        setTitle("Cancel Flight (Admin)");
        loadWindowIcon();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 300);
        setMinimumSize(new Dimension(400, 250));
        setLocationRelativeTo(null);
        initComponents();
    }

    private void loadWindowIcon() {
        String iconPath = "/images/ars.png";
        try {
            URL iconUrl = getClass().getResource(iconPath);
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    setIconImage(icon.getImage());
                } else {
                    System.err.println("Icon load failed for: " + iconPath);
                }
            } else {
                System.err.println("Icon not found: " + iconPath);
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Cancel Flight", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Table for active flights
        tableModel = new DefaultTableModel(new Object[]{
            "Flight Number", "Departure", "Arrival", "Departure Time", "Total Seats"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(flightsTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel Selected Flight");
        JButton backBtn = new JButton("Go Back");
        southPanel.add(cancelBtn);
        southPanel.add(backBtn);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(cancelBtn);

        cancelBtn.addActionListener(e -> cancelSelectedFlight());
        backBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new AdminDashboardUI(adminUser).setVisible(true));
        });

        add(mainPanel);
        loadActiveFlights();
    }

    private void loadActiveFlights() {
        try {
            List<Flight> flights = flightDAO.getAllFlights(); // Only active flights
            tableModel.setRowCount(0);
            for (Flight flight : flights) {
                tableModel.addRow(new Object[]{
                    flight.getFlightNumber(),
                    flight.getDepartureAirport(),
                    flight.getArrivalAirport(),
                    flight.getDepartureTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    flight.getTotalSeats()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading flights: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelSelectedFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String flightNumber = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure to cancel flight '" + flightNumber + "'?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            Flight flight = flightDAO.getFlightByNumberIgnoreActive(flightNumber);
            if (flight == null) {
                JOptionPane.showMessageDialog(this, "Flight not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!flight.isActive()) {
                JOptionPane.showMessageDialog(this, "Flight is already inactive.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            flight.setActive(false);
            boolean success = flightDAO.updateFlightStatus(flight.getId(), false);
            if (success) {
                List<Booking> bookings = bookingDAO.getBookingsByFlightId(flight.getId());
                for (Booking b : bookings) {
                    if (b.isActive()) {
                        bookingDAO.updateBookingStatus(b.getId(), false);
                        notificationDAO.createNotification(b.getUserId(),
                                "Your booking '" + b.getBookingReference() + "' was cancelled due to flight cancellation.");
                    }
                }
                JOptionPane.showMessageDialog(this, "Flight and related bookings successfully cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadActiveFlights(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel the flight in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "SQL Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
