package com.airline.view;

import com.airline.dao.FlightDAO;
import com.airline.dao.BookingDAO;
import com.airline.model.Flight;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.airline.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserPreferredFlightsUI extends JFrame {
    private JTable flightsTable;
    private DefaultTableModel tableModel;
    private FlightDAO flightDAO = new FlightDAO();
    private BookingDAO bookingDAO = new BookingDAO();
    private User currentUser;

    public UserPreferredFlightsUI(User currentUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
        this.currentUser = currentUser;
        setTitle("Flights Matching Your Preferences");
        setIconImage(new ImageIcon(getClass().getResource("/images/ars_login.png")).getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        initComponents();
        loadPreferredFlights();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Flights Matching Your Seat Preference: " + 
            (currentUser.getDefaultSeatPreference() != null ? currentUser.getDefaultSeatPreference() : "Not Set"));
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Add a note about preferences
        JLabel noteLabel = new JLabel("Note: These flights have available seats matching your preferred seat type");
        noteLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        headerPanel.add(noteLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel(new Object[]{
            "Flight Number", "Departure", "Arrival", "Departure Time", 
            "Total Seats", "Available Preferred Seats"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        flightsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(flightsTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookButton = new JButton("Book Selected Flight");
        JButton goBackButton = new JButton("Go Back");

        bookButton.addActionListener(e -> {
            int selectedRow = flightsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String flightNumber = (String) tableModel.getValueAt(selectedRow, 0);
                dispose();
                SwingUtilities.invokeLater(() -> {
                    UserBookFlights bookUI = new UserBookFlights(currentUser, flightNumber);
                    bookUI.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a flight to book.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        goBackButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new UserDashboardUI(currentUser).setVisible(true));
        });

        buttonPanel.add(bookButton);
        buttonPanel.add(goBackButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadPreferredFlights() {
        try {
            String preference = currentUser.getDefaultSeatPreference();
            List<Flight> allFlights = flightDAO.getAllFlights();
            tableModel.setRowCount(0);

            for (Flight flight : allFlights) {
                // Get all booked seats for this flight
                List<String> bookedSeats = bookingDAO.getBookedSeatNumbers(flight.getId())
                    .stream().map(s -> s.trim().toUpperCase()).toList();
                int totalSeats = flight.getTotalSeats();

                // Calculate available preferred seats
                int availablePreferredSeats = 0;
                char[] seatLetters = {'A', 'B', 'C', 'D'};
                int totalRows = (int) Math.ceil((double) totalSeats / seatLetters.length);
                
                for (int row = 1; row <= totalRows; row++) {
                    for (int i = 0; i < seatLetters.length; i++) {
                        int seatIndex = (row - 1) * seatLetters.length + i;
                        if (seatIndex >= totalSeats) break; // Don't count seats that don't exist
                        char seatLetter = seatLetters[i];
                        String seat = (row + String.valueOf(seatLetter)).toUpperCase();
                        if (bookedSeats.contains(seat)) continue;
                        boolean isWindowSeat = seatLetter == 'A' || seatLetter == 'D';
                        if ((preference != null && preference.equalsIgnoreCase("WINDOW") && isWindowSeat) ||
                            (preference != null && preference.equalsIgnoreCase("AISLE") && !isWindowSeat)) {
                            availablePreferredSeats++;
                        }
                    }
                }

                if (availablePreferredSeats > 0) {
                    tableModel.addRow(new Object[]{
                        flight.getFlightNumber(),
                        flight.getDepartureAirport(),
                        flight.getArrivalAirport(),
                        flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        totalSeats,
                        availablePreferredSeats
                    });
                }
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "No flights found with available seats matching your preference.",
                    "No Flights Found",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
                SwingUtilities.invokeLater(() -> new UserDashboardUI(currentUser).setVisible(true));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            dispose();
            SwingUtilities.invokeLater(() -> new UserDashboardUI(currentUser).setVisible(true));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User dummyUser = new User();
            dummyUser.setDefaultSeatPreference("Window");
            new UserPreferredFlightsUI(dummyUser).setVisible(true);
        });
    }
} 