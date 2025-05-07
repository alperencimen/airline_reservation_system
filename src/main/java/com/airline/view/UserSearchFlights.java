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
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        setIconImage(new ImageIcon(getClass().getResource("/images/ars_login.png")).getImage());
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

        // Add Show All Active Flights button
        JButton showAllButton = new JButton("Show All Active Flights");
        showAllButton.setBackground(Color.ORANGE);
        showAllButton.setOpaque(true);
        showAllButton.setBorderPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(showAllButton, gbc);
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        
        String[] columnNames = {"Flight Number", "Departure", "Arrival", "Departure Time", "Total Seats", "Available Seats"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        flightsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(flightsTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton goBackButton = new JButton("Go Back");
        southPanel.add(goBackButton);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        
        getRootPane().setDefaultButton(searchButton);
        
        searchButton.addActionListener(e -> searchFlights());
        showAllButton.addActionListener(e -> loadAllActiveFlights());
        goBackButton.addActionListener(e -> {
            dispose();
            new UserDashboardUI(currentUser).setVisible(true);
        });
        
        add(mainPanel);

        addContextMenuToTable(); // Add context menu functionality to the flights table
    }
    
    private void searchFlights() {
        String departure = departureField.getText().trim();
        String arrival = arrivalField.getText().trim();
        String dateStr = dateField.getText().trim();
        if (departure.isEmpty() || arrival.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be entered!", "Error", JOptionPane.ERROR_MESSAGE);
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
                        flight.getTotalSeats(),
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

    private void loadAllActiveFlights() {
        try {
            List<Flight> flights = flightDAO.getAllFlights();
            tableModel.setRowCount(0);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Flight flight : flights) {
                tableModel.addRow(new Object[]{
                    flight.getFlightNumber(),
                    flight.getDepartureAirport(),
                    flight.getArrivalAirport(),
                    flight.getDepartureTime().format(dtf),
                    flight.getTotalSeats(),
                    flight.getAvailableSeats()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addContextMenuToTable() {
        flightsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // Also check on press for cross-platform compatibility (e.g., macOS)
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            private void showPopup(MouseEvent e) {
                int row = flightsTable.rowAtPoint(e.getPoint());
                int col = flightsTable.columnAtPoint(e.getPoint());

                // Ensure the click was on a valid row and the "Flight Number" column (index 0)
                if (row >= 0 && col == 0) {
                    // Select the row where the right-click happened
                    flightsTable.setRowSelectionInterval(row, row);

                    // Retrieve the flight number from the table model
                    String flightNumber = (String) flightsTable.getValueAt(row, col);

                    if (flightNumber != null && !flightNumber.isEmpty()) {
                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem copyItem = new JMenuItem("Copy Flight Number: " + flightNumber);

                        copyItem.addActionListener(actionEvent -> {
                            // Copy the flight number to the system clipboard
                            StringSelection stringSelection = new StringSelection(flightNumber);
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            clipboard.setContents(stringSelection, null);
                        });

                        popupMenu.add(copyItem);
                        // Show the popup menu at the mouse click location
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
    }

}
