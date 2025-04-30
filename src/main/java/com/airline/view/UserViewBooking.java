package com.airline.view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.airline.dao.BookingDAO;
import com.airline.model.Booking;
import com.airline.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
            SwingUtilities.invokeLater(() -> new LoginUI().display());
            return;
        }
        setTitle("My Bookings");
        setIconImage(new ImageIcon(getClass().getResource("/images/ars_login.png")).getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 450);
        setMinimumSize(new Dimension(600, 400));
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

        tableModel = new DefaultTableModel(new Object[]{"Booking Ref", "Flight ID", "Seat (Type)", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Keep cells non-editable
            }
        };
        bookingsTable = new JTable(tableModel);
        bookingsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add Mouse Listener for Right-Click Copy
        addContextMenuToTable();


        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton goBackButton = new JButton("Go Back");
        southPanel.add(goBackButton);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        goBackButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new UserDashboardUI(currentUser).display());
        });

        add(mainPanel);
    }

    /**
     * for copying the booking reference when right-clicked.
     */
    private void addContextMenuToTable() {
        bookingsTable.addMouseListener(new MouseAdapter() {
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
                int row = bookingsTable.rowAtPoint(e.getPoint());
                int col = bookingsTable.columnAtPoint(e.getPoint());

                // Ensure the click was on a valid row and the "Booking Ref" column (index 0)
                if (row >= 0 && col == 0) {
                    // Select the row where the right-click happened
                    bookingsTable.setRowSelectionInterval(row, row);

                    // Retrieve the booking reference from the table model
                    String bookingRef = (String) bookingsTable.getValueAt(row, col);

                    if (bookingRef != null && !bookingRef.isEmpty()) {
                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem copyItem = new JMenuItem("Copy Reference: " + bookingRef);

                        copyItem.addActionListener(actionEvent -> {
                            // Copy the booking reference to the system clipboard
                            StringSelection stringSelection = new StringSelection(bookingRef);
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


    private void loadBookings() {
        if (currentUser == null) return;

        try {
            List<Booking> bookings = bookingDAO.getUserBookings(currentUser.getId());
            tableModel.setRowCount(0);

            if (bookings.isEmpty()) {
                tableModel.addRow(new Object[]{"No bookings found.", "", "", ""});
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
            JOptionPane.showMessageDialog(this, "An unexpected error occurred loading bookings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

/*

    public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> {
             User testUser = new User();
             testUser.setId(1); testUser.setUsername("testuser"); testUser.setActive(true); testUser.setAdmin(false);
             if(testUser.getId() > 0) {
                 new UserViewBooking(testUser).setVisible(true);
             } else {
                 System.err.println("Test user setup failed.");
                 JOptionPane.showMessageDialog(null, "Test user configuration error.", "Error", JOptionPane.ERROR_MESSAGE);
             }
         });
    }
 */

}