package com.airline.view;

import com.airline.dao.FlightDAO;
import com.airline.model.Flight;
import com.airline.model.User;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminAddFlight extends JFrame {
    private final User adminUser;
    private JTextField flightNumberField;
    private JTextField departureAirportField;
    private JTextField arrivalAirportField;
    private JTextField departureTimeField;
    private JTextField arrivalTimeField;
    private JTextField availableSeatsField;
    private JTextField airlineNameField;


    private final FlightDAO flightDAO = new FlightDAO();

    public AdminAddFlight(User adminUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
        this.adminUser = adminUser;

        setTitle("Add New Flight");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(getClass().getResource("/images/ars_login_admin.png")).getImage());
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Add New Flight", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(7, 2, 10, 10));

        flightNumberField = new JTextField();
        departureAirportField = new JTextField();
        arrivalAirportField = new JTextField();
        departureTimeField = new JTextField();
        arrivalTimeField = new JTextField();
        availableSeatsField = new JTextField();
        airlineNameField = new JTextField();

        centerPanel.add(new JLabel("Flight Number:"));
        centerPanel.add(flightNumberField);
        centerPanel.add(new JLabel("Departure Airport:"));
        centerPanel.add(departureAirportField);
        centerPanel.add(new JLabel("Arrival Airport:"));
        centerPanel.add(arrivalAirportField);
        centerPanel.add(new JLabel("Departure Time (yyyy-MM-dd HH:mm):"));
        centerPanel.add(departureTimeField);
        centerPanel.add(new JLabel("Arrival Time (yyyy-MM-dd HH:mm):"));
        centerPanel.add(arrivalTimeField);
        centerPanel.add(new JLabel("Total Seats:"));
        centerPanel.add(availableSeatsField);
        centerPanel.add(new JLabel("Airline Name:"));
        centerPanel.add(airlineNameField);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addFlightButton = new JButton("Add Flight");
        JButton goBackButton = new JButton("Go Back");


        addFlightButton.addActionListener(e -> addFlight());
        goBackButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new AdminDashboardUI(adminUser).setVisible(true));
        });


        bottomPanel.add(addFlightButton);
        bottomPanel.add(goBackButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addFlight() {
        try {
            String flightNumber = flightNumberField.getText().trim();
            String departureAirport = departureAirportField.getText().trim();
            String arrivalAirport = arrivalAirportField.getText().trim();
            LocalDateTime departureTime = LocalDateTime.parse(departureTimeField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            int availableSeats = Integer.parseInt(availableSeatsField.getText().trim());
            String airlineName = airlineNameField.getText().trim(); // ✅ eksik olan kısım

            Flight flight = new Flight();
            flight.setFlightNumber(flightNumber);
            flight.setDepartureAirport(departureAirport);
            flight.setArrivalAirport(arrivalAirport);
            flight.setDepartureTime(departureTime);
            flight.setArrivalTime(arrivalTime);
            flight.setAvailableSeats(availableSeats);
            flight.setTotalSeats(availableSeats);
            flight.setAirlineName(airlineName);
            flight.setActive(true);

            if (flightDAO.createFlight(flight)) {
                JOptionPane.showMessageDialog(this, "Flight added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                SwingUtilities.invokeLater(() -> new AdminDashboardUI(adminUser).setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add flight!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        User dummyAdmin = new User();
        dummyAdmin.setUsername("AdminTest");
        dummyAdmin.setAdmin(true);

        SwingUtilities.invokeLater(() -> new AdminAddFlight(dummyAdmin).setVisible(true));
    }

}