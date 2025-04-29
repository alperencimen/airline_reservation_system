package com.airline.view;

import com.airline.dao.FlightDAO;
import com.airline.model.Flight;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.airline.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserSearchFlights extends JFrame {
    private JTextField departureField;
    private JTextField arrivalField;
    private JTextField dateField;
    private JTable flightsTable;
    private DefaultTableModel tableModel;
    private FlightDAO flightDAO = new FlightDAO();
    private User currentUser;

    public UserSearchFlights(User currentUser) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to load FlatIntelliJLaf: " + ex.getMessage());
        }
        this.currentUser = currentUser;
        setTitle("Search Flights");
        setIconImage(new ImageIcon(getClass().getResource("ars.png")).getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Departure Airport:"), gbc);
        departureField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(departureField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Arrival Airport:"), gbc);
        arrivalField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(arrivalField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Date (yyyy-MM-dd):"), gbc);
        dateField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(dateField, gbc);
        
        JButton searchButton = new JButton("Search");
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(searchButton, gbc);
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        
        tableModel = new DefaultTableModel(new Object[]{"Flight Number", "Departure", "Arrival", "Departure Time", "Available Seats"}, 0);
        flightsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(flightsTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton goBackButton = new JButton("Go Back");
        southPanel.add(goBackButton);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        
        getRootPane().setDefaultButton(searchButton);
        
        searchButton.addActionListener(e -> searchFlights());
        goBackButton.addActionListener(e -> {
            dispose();
            new UserDashboardUI(currentUser).setVisible(true);
        });
        
        add(mainPanel);
    }
    
    private void searchFlights() {
        String departure = departureField.getText().trim();
        String arrival = arrivalField.getText().trim();
        String dateStr = dateField.getText().trim();
        if (departure.isEmpty() || arrival.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            List<Flight> flights = flightDAO.searchFlights(departure, arrival, java.sql.Date.valueOf(dateStr));
            tableModel.setRowCount(0);
            if (flights.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No flights found!", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                for (Flight flight : flights) {
                    tableModel.addRow(new Object[]{
                        flight.getFlightNumber(),
                        flight.getDepartureAirport(),
                        flight.getArrivalAirport(),
                        flight.getDepartureTime().format(dtf),
                        flight.getAvailableSeats()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Please use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Ensure the UI is created and shown on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Create an instance of the UserSearchFlights UI.
            // Passing 'null' for the user is acceptable for testing search functionality,
            // as login isn't strictly required just to view flights.
            UserSearchFlights searchFlightsUI = new UserSearchFlights(null);
            // Make it visible
            searchFlightsUI.setVisible(true);
        });
    }
}
