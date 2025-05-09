package com.airline.view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.airline.dao.FlightDAO;
import com.airline.dao.BookingDAO;
import com.airline.model.Flight;
import com.airline.model.Booking;
import com.airline.model.User;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UserBookFlights extends JFrame {
    private User currentUser;
    private String selectedFlightNumber;
    private JTextField flightNumberField;
    private FlightDAO flightDAO = new FlightDAO();
    private BookingDAO bookingDAO = new BookingDAO();
    private DefaultTableModel tableModel;
    private JTable flightsTable;

    private static final int TOTAL_ROWS = 30;
    private static final char[] SEAT_LETTERS = {'A', 'B', 'C', 'D'};
    private static final char FIRST_WINDOW_SEAT = SEAT_LETTERS[0];
    private static final char LAST_WINDOW_SEAT = SEAT_LETTERS[SEAT_LETTERS.length - 1];

    public UserBookFlights(User currentUser) {
        this.currentUser = currentUser;
        this.selectedFlightNumber = null;
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch(Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
        if (this.currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please log in to book a flight!", "Login Required", JOptionPane.ERROR_MESSAGE);
            dispose();
            SwingUtilities.invokeLater(() -> new LoginUI().display());
            return;
        }
        setTitle("Book a Flight");
        setIconImage(new ImageIcon(getClass().getResource("/images/ars_login.png")).getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 300);
        setMinimumSize(new Dimension(400, 250));
        setLocationRelativeTo(null);
        initComponents();
        loadFlights();
    }

    public UserBookFlights(User currentUser, String flightNumber) {
        this.currentUser = currentUser;
        this.selectedFlightNumber = flightNumber;
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch(Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
        if (this.currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please log in to book a flight!", "Login Required", JOptionPane.ERROR_MESSAGE);
            dispose();
            SwingUtilities.invokeLater(() -> new LoginUI().display());
            return;
        }
        setTitle("Book a Flight");
        setIconImage(new ImageIcon(getClass().getResource("/images/ars_login.png")).getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 300);
        setMinimumSize(new Dimension(400, 250));
        setLocationRelativeTo(null);
        
        if (flightNumber != null) {
            // If flight number is provided, go straight to seat selection
            SwingUtilities.invokeLater(() -> {
                findAndBookFlight();
                dispose(); // Close this window after showing seat selection
            });
        } else {
            // Otherwise show the booking page
            initComponents();
            loadFlights();
        }
    }

    private void loadFlights() {
        try {
            List<Flight> flights = flightDAO.getAllFlights();
            tableModel.setRowCount(0);
            for (Flight flight : flights) {
                tableModel.addRow(new Object[]{
                    flight.getFlightNumber(),
                    flight.getDepartureAirport(),
                    flight.getArrivalAirport(),
                    flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    flight.getTotalSeats()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading flights: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Initialize table model and table
        tableModel = new DefaultTableModel(new Object[]{"Flight Number", "Departure", "Arrival", "Time", "Total Seats"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(flightsTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        JLabel headerLabel = new JLabel("Book a Flight", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        centerPanel.add(new JLabel("Enter Flight Number:"), gbc);

        flightNumberField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(flightNumberField, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton bookButton = new JButton("Find Flight & Select Seat");
        JButton goBackButton = new JButton("Go Back");
        southPanel.add(bookButton);
        southPanel.add(goBackButton);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(bookButton);

        bookButton.addActionListener(e -> findAndBookFlight());
        goBackButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new UserDashboardUI(currentUser).display());
        });

        add(mainPanel);
    }

    private List<String> generateAllSeats(int rows, char[] letters) {
        List<String> allSeats = new ArrayList<>();
        for (int i = 1; i <= rows; i++) {
            for (char letter : letters) {
                allSeats.add(i + "" + letter);
            }
        }
        return allSeats;
    }

    private boolean isWindowSeat(char seatLetter) {
        return seatLetter == FIRST_WINDOW_SEAT || seatLetter == LAST_WINDOW_SEAT;
    }

    private String formatSeatsForDisplay(List<String> seats, int seatsPerLine) {
        StringBuilder display = new StringBuilder();
        int count = 0;
        Collections.sort(seats, (s1, s2) -> {
            int row1 = Integer.parseInt(s1.substring(0, s1.length() - 1));
            int row2 = Integer.parseInt(s2.substring(0, s2.length() - 1));
            if (row1 != row2) return Integer.compare(row1, row2);
            return Character.compare(s1.charAt(s1.length() - 1), s2.charAt(s2.length() - 1));
        });
        for (String seat : seats) {
            display.append(String.format("%-5s", seat));
            count++;
            if (count % seatsPerLine == 0) display.append("\n");
        }
        return display.toString();
    }

    public void findAndBookFlight() {
        String flightNumber;
        if (selectedFlightNumber != null) {
            flightNumber = selectedFlightNumber;
        } else {
            flightNumber = flightNumberField.getText().trim().toUpperCase();
            if (flightNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Flight number cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try {
            Flight selectedFlight = flightDAO.getFlightByNumber(flightNumber);

            if (selectedFlight == null) {
                JOptionPane.showMessageDialog(this, "Flight number '" + flightNumber + "' not found or is inactive.", "Flight Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedFlight.getAvailableSeats() <= 0) {
                JOptionPane.showMessageDialog(this, "Sorry, no seats are currently available on flight " + flightNumber + ".", "No Seats Available", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int totalSeats = selectedFlight.getTotalSeats();
            int seatsPerRow = 4; // A, B, C, D
            int totalRows = (int) Math.ceil((double) totalSeats / seatsPerRow);

            // Get all booked seats for this flight
            List<String> bookedSeats = bookingDAO.getBookedSeatNumbers(selectedFlight.getId());
            
            // Create seat selection dialog
            JPanel seatSelectionPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;

            // Create left side seats (A-B)
            JPanel leftSeats = new JPanel(new GridLayout(0, 2, 5, 5));
            // Create right side seats (C-D)
            JPanel rightSeats = new JPanel(new GridLayout(0, 2, 5, 5));
            // Create aisle label with smaller gap
            JLabel aisleLabel = new JLabel("AISLE", SwingConstants.CENTER);
            aisleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            aisleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Reduced padding

            char[] seatLetters = {'A', 'B', 'C', 'D'};
            int seatIndex = 0;
            for (int row = 1; row <= totalRows; row++) {
                // Left side seats (A-B)
                for (int i = 0; i < 2; i++) {
                    if (seatIndex >= totalSeats) break;
                    String seat = row + String.valueOf(seatLetters[i]);
                    JButton seatButton = createSeatButton(seat, bookedSeats, selectedFlight);
                    leftSeats.add(seatButton);
                    seatIndex++;
                }
                // Right side seats (C-D)
                for (int i = 2; i < 4; i++) {
                    if (seatIndex >= totalSeats) break;
                    String seat = row + String.valueOf(seatLetters[i]);
                    JButton seatButton = createSeatButton(seat, bookedSeats, selectedFlight);
                    rightSeats.add(seatButton);
                    seatIndex++;
                }
            }

            // Add components to the main grid
            gbc.gridx = 0;
            gbc.gridy = 0;
            seatSelectionPanel.add(leftSeats, gbc);

            gbc.gridx = 1;
            seatSelectionPanel.add(aisleLabel, gbc);

            gbc.gridx = 2;
            seatSelectionPanel.add(rightSeats, gbc);

            // Create a container panel for the seat selection
            JPanel containerPanel = new JPanel(new BorderLayout());
            containerPanel.add(seatSelectionPanel, BorderLayout.CENTER);
            
            // Add a title at the top
            JLabel titleLabel = new JLabel("Select Your Seat for Flight " + flightNumber, SwingConstants.CENTER);
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            containerPanel.add(titleLabel, BorderLayout.NORTH);

            // Create the dialog with a larger size to accommodate all seats
            JDialog seatDialog = new JDialog(this, "Seat Selection", true);
            seatDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            seatDialog.setSize(800, 600); // Increased size
            seatDialog.setLocationRelativeTo(this);

            // Add Go Back button
            JButton goBackButton = new JButton("Go Back");
            goBackButton.addActionListener(e -> {
                seatDialog.dispose();
                dispose(); // Close the current window
                SwingUtilities.invokeLater(() -> {
                    UserDashboardUI dashboard = new UserDashboardUI(currentUser);
                    dashboard.setVisible(true);
                });
            });
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(goBackButton);
            containerPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Add the container panel to the dialog
            seatDialog.add(containerPanel);
            seatDialog.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Database error occurred: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createSeatButton(String seatNumber, List<String> bookedSeats, Flight selectedFlight) {
        JButton seatButton = new JButton(seatNumber);
        seatButton.setPreferredSize(new Dimension(40, 30));
        
        // Check if seat is already booked
        if (bookedSeats.contains(seatNumber)) {
            seatButton.setEnabled(false);
            seatButton.setBackground(Color.RED);
        } else {
            // Check if it's a window seat
            char seatLetter = seatNumber.charAt(seatNumber.length() - 1);
            if (seatLetter == 'A' || seatLetter == 'D') {
                seatButton.setBackground(Color.GREEN);
            } else {
                seatButton.setBackground(Color.YELLOW);
            }
        }
        
        seatButton.addActionListener(e -> handleSeatSelection(seatButton, seatNumber, selectedFlight));
        return seatButton;
    }

    private void handleSeatSelection(JButton seatButton, String seatNumber, Flight selectedFlight) {
        char seatLetter = seatNumber.charAt(seatNumber.length() - 1);
        String seatType = (seatLetter == 'A' || seatLetter == 'D') ? "WINDOW" : "AISLE";
        
        // Check if this matches user's preference
        if (currentUser.getDefaultSeatPreference() != null && 
            !currentUser.getDefaultSeatPreference().equalsIgnoreCase(seatType)) {
            int response = JOptionPane.showConfirmDialog(this,
                "This seat doesn't match your preferred seat type (" + 
                currentUser.getDefaultSeatPreference() + "). Would you like to proceed?",
                "Seat Type Mismatch",
                JOptionPane.YES_NO_OPTION);
            if (response != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        // Create booking
        try {
            Booking booking = new Booking();
            booking.setBookingReference("BK" + (100000 + (int)(Math.random() * 900000)));
            booking.setUserId(currentUser.getId());
            booking.setFlightId(selectedFlight.getId());
            booking.setBookingDate(LocalDateTime.now());
            booking.setSeatNumber(seatNumber);
            booking.setSeatPreference(seatType);
            booking.setActive(true);

            if (bookingDAO.createBooking(booking)) {
                selectedFlight.setAvailableSeats(selectedFlight.getAvailableSeats() - 1);
                flightDAO.updateFlight(selectedFlight);

                JOptionPane.showMessageDialog(this, 
                    "Booking successful!\nBooking Reference: " + booking.getBookingReference() +
                    "\nFlight: " + selectedFlight.getFlightNumber() +
                    "\nSeat: " + seatNumber + " (" + seatType + ")",
                    "Booking Confirmed", 
                    JOptionPane.INFORMATION_MESSAGE);

                dispose(); // Close the current window
                SwingUtilities.invokeLater(() -> {
                    UserDashboardUI dashboard = new UserDashboardUI(currentUser);
                    dashboard.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to create booking. Please try again.",
                    "Booking Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + ex.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bookFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a flight to book!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String flightNumber = (String) tableModel.getValueAt(selectedRow, 0);
        String seatPreference = currentUser.getDefaultSeatPreference();

        try {
            // Check if the flight has available preferred seats
            Flight flight = flightDAO.getFlightByNumber(flightNumber);
            if (seatPreference != null && !seatPreference.isEmpty()) {
                if (flight.getAvailablePreferredSeats() <= 0) {
                    int response = JOptionPane.showConfirmDialog(this,
                        "No " + seatPreference + " seats available. Would you like to book a different seat type?",
                        "Preferred Seats Unavailable",
                        JOptionPane.YES_NO_OPTION);
                    if (response != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
            }

            // Proceed with booking
            Booking booking = new Booking();
            booking.setUserId(currentUser.getId());
            booking.setFlightId(flight.getId());
            booking.setSeatPreference(seatPreference);
            booking.setBookingDate(LocalDateTime.now());

            if (bookingDAO.createBooking(booking)) {
                JOptionPane.showMessageDialog(this,
                    "Flight booked successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new UserDashboardUI(currentUser).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to book flight. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User testUser = new User();
            testUser.setId(1); testUser.setUsername("testuser"); testUser.setActive(true); testUser.setAdmin(false);
            if (testUser.getId() > 0) {
                new UserBookFlights(testUser).setVisible(true);
            } else {
                System.err.println("Test user setup failed.");
                JOptionPane.showMessageDialog(null, "Test user configuration error.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
