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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class UserBookFlights extends JFrame {
    private User currentUser;
    private JTextField flightNumberField;
    private FlightDAO flightDAO = new FlightDAO();
    private BookingDAO bookingDAO = new BookingDAO();

    private static final int TOTAL_ROWS = 30;
    private static final char[] SEAT_LETTERS = {'A', 'B', 'C', 'D'};
    private static final char FIRST_WINDOW_SEAT = SEAT_LETTERS[0];
    private static final char LAST_WINDOW_SEAT = SEAT_LETTERS[SEAT_LETTERS.length - 1];

    public UserBookFlights(User currentUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch(Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
        this.currentUser = currentUser;
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
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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

    private void findAndBookFlight() {
        String flightNumber = flightNumberField.getText().trim().toUpperCase();
        if (flightNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Flight number cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Flight selectedFlight = flightDAO.getFlightByNumber(flightNumber);

            if (selectedFlight == null) {
                JOptionPane.showMessageDialog(this, "Flight number '" + flightNumber + "' not found or is inactive.", "Flight Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<String> bookedSeats = bookingDAO.getBookedSeatNumbers(selectedFlight.getId());
            List<String> allPossibleSeats = generateAllSeats(TOTAL_ROWS, SEAT_LETTERS);
            List<String> availableSeatsList = new ArrayList<>(allPossibleSeats);
            availableSeatsList.removeAll(bookedSeats);

            if (availableSeatsList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Sorry, no seats are currently available on flight " + flightNumber + ".", "No Seats Available", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<String> windowSeats = new ArrayList<>();
            List<String> aisleSeats = new ArrayList<>();
            for (String seat : availableSeatsList) {
                char seatLetter = seat.charAt(seat.length() - 1);
                if (isWindowSeat(seatLetter)) windowSeats.add(seat);
                else aisleSeats.add(seat);
            }

            JPanel seatSelectionPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;

            Dimension preferredSize = new Dimension(200, 150);

            if (currentUser.getDefaultSeatPreference() == null) {
                JTextArea windowTextArea = new JTextArea(formatSeatsForDisplay(windowSeats, 5));
                windowTextArea.setEditable(false);
                windowTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane windowScrollPane = new JScrollPane(windowTextArea);
                windowScrollPane.setBorder(BorderFactory.createTitledBorder("Window Seats"));
                windowScrollPane.setPreferredSize(preferredSize);

                JTextArea aisleTextArea = new JTextArea(formatSeatsForDisplay(aisleSeats, 5));
                aisleTextArea.setEditable(false);
                aisleTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane aisleScrollPane = new JScrollPane(aisleTextArea);
                aisleScrollPane.setBorder(BorderFactory.createTitledBorder("Aisle Seats"));
                aisleScrollPane.setPreferredSize(preferredSize);

                gbc.gridx = 0; gbc.gridy = 0;
                seatSelectionPanel.add(windowScrollPane, gbc);
                gbc.gridx = 1;
                seatSelectionPanel.add(aisleScrollPane, gbc);
            } else if (currentUser.getDefaultSeatPreference().equalsIgnoreCase("WINDOW")) {
                JTextArea windowTextArea = new JTextArea(formatSeatsForDisplay(windowSeats, 5));
                windowTextArea.setEditable(false);
                windowTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane windowScrollPane = new JScrollPane(windowTextArea);
                windowScrollPane.setBorder(BorderFactory.createTitledBorder("Window Seats"));
                windowScrollPane.setPreferredSize(preferredSize);

                gbc.gridx = 0; gbc.gridy = 0;
                seatSelectionPanel.add(windowScrollPane, gbc);
            } else if (currentUser.getDefaultSeatPreference().equalsIgnoreCase("AISLE")) {
                JTextArea aisleTextArea = new JTextArea(formatSeatsForDisplay(aisleSeats, 5));
                aisleTextArea.setEditable(false);
                aisleTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane aisleScrollPane = new JScrollPane(aisleTextArea);
                aisleScrollPane.setBorder(BorderFactory.createTitledBorder("Aisle Seats"));
                aisleScrollPane.setPreferredSize(preferredSize);

                gbc.gridx = 0; gbc.gridy = 0;
                seatSelectionPanel.add(aisleScrollPane, gbc);
            }

            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            inputPanel.add(new JLabel("Enter Desired Seat (e.g., 1A):"));
            JTextField seatInputTextField = new JTextField(5);
            inputPanel.add(seatInputTextField);

            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weighty = 0;
            seatSelectionPanel.add(inputPanel, gbc);

            int result = JOptionPane.showOptionDialog(this, seatSelectionPanel, "Select Your Seat for Flight " + flightNumber,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

            if (result == JOptionPane.OK_OPTION) {
                String desiredSeatInput = seatInputTextField.getText();
                if (desiredSeatInput == null || desiredSeatInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,"Seat selection cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String desiredSeat = desiredSeatInput.trim().toUpperCase();

                if (!allPossibleSeats.contains(desiredSeat)) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid seat format: '" + desiredSeatInput + "'. Please use format like '1A', '23C'.",
                            "Invalid Seat Format", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (availableSeatsList.contains(desiredSeat)) {
                    String bookingRef = "BK" + (100000 + (int)(Math.random() * 900000));

                    Booking booking = new Booking();
                    booking.setBookingReference(bookingRef);
                    booking.setUserId(currentUser.getId());
                    booking.setFlightId(selectedFlight.getId());
                    booking.setBookingDate(LocalDateTime.now());
                    booking.setSeatNumber(desiredSeat);

                    char seatLetter = desiredSeat.charAt(desiredSeat.length() - 1);
                    booking.setSeatPreference(isWindowSeat(seatLetter) ? "WINDOW" : "AISLE");
                    booking.setActive(true);

                    if (bookingDAO.createBooking(booking)) {
                        selectedFlight.setAvailableSeats(selectedFlight.getAvailableSeats() - 1);
                        flightDAO.updateFlight(selectedFlight);

                        JOptionPane.showMessageDialog(this, "Booking successful!\nBooking Reference: " + bookingRef +
                                        "\nFlight: " + selectedFlight.getFlightNumber() +
                                        "\nSeat: " + desiredSeat + " (" + booking.getSeatPreference() + ")",
                                "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);

                        dispose();
                        SwingUtilities.invokeLater(() -> new UserDashboardUI(currentUser).display());

                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to create booking in the database!", "Booking Error", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Seat " + desiredSeat + " is not available. Please choose from the lists.", "Seat Unavailable", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error occurred: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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
