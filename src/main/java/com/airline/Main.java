package com.airline;

import com.airline.dao.UserDAO;
import com.airline.dao.FlightDAO;
import com.airline.dao.BookingDAO;
import com.airline.model.User;
import com.airline.model.Flight;
import com.airline.model.Booking;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;
    private static final UserDAO userDAO = new UserDAO();
    private static final FlightDAO flightDAO = new FlightDAO();
    private static final BookingDAO bookingDAO = new BookingDAO();

    public static void main(String[] args) {
        try {
            System.out.println("=== Airline Reservation System ===");
            System.out.println("1. Terminal Interface");
            System.out.println("2. GUI Interface");
            System.out.print("Choose interface type: ");
    
            int interfaceChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline
    
            if (interfaceChoice == 1) {
                startTerminalInterface();
            } else if (interfaceChoice == 2) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new com.airline.ui.LoginUI().setVisible(true);
                    }
                });
            } else {
                System.out.println("Invalid choice. Using terminal interface.");
                startTerminalInterface();
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    

    private static void startTerminalInterface() throws SQLException {
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else if (currentUser.isAdmin()) {
                showAdminMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private static void showLoginMenu() throws SQLException {
        System.out.println("\n=== Airline Reservation System ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option!");
        }
    }

    private static void showUserMenu() throws SQLException {
        System.out.println("\n=== User Menu ===");
        System.out.println("1. Search Flights");
        System.out.println("2. View Flights by Seat Preference");
        System.out.println("3. Book a Flight");
        System.out.println("4. View My Bookings");
        System.out.println("5. Cancel Booking");
        System.out.println("6. Logout");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                searchFlights();
                break;
            case 2:
                searchFlightsByPreference();
                break;
            case 3:
                bookFlight();
                break;
            case 4:
                viewMyBookings();
                break;
            case 5:
                cancelBooking();
                break;
            case 6:
                currentUser = null;
                break;
            default:
                System.out.println("Invalid option!");
        }
    }

    private static void showAdminMenu() throws SQLException {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("1. Add New Flight");
        System.out.println("2. View All Flights");
        System.out.println("3. Cancel Flight");
        System.out.println("4. View All Users");
        System.out.println("5. Suspend User");
        System.out.println("6. Logout");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                addNewFlight();
                break;
            case 2:
                viewAllFlights();
                break;
            case 3:
                cancelFlight();
                break;
            case 4:
                viewAllUsers();
                break;
            case 5:
                suspendUser();
                break;
            case 6:
                currentUser = null;
                break;
            default:
                System.out.println("Invalid option!");
        }
    }

    private static void login() throws SQLException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println("Error: Username cannot be empty!");
            return;
        }
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) {
            System.out.println("Error: Password cannot be empty!");
            return;
        }

        User user = userDAO.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            if (!user.isActive()) {
                System.out.println("Your account has been suspended. Please contact the administrator.");
                return;
            }
            currentUser = user;
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password!");
        }
    }

    private static void register() throws SQLException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println("Error: Username cannot be empty!");
            return;
        }
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) {
            System.out.println("Error: Password cannot be empty!");
            return;
        }
        
        System.out.print("Enter gender: ");
        String gender = scanner.nextLine().trim();
        if (gender.isEmpty()) {
            System.out.println("Error: Gender cannot be empty!");
            return;
        }
        
        System.out.print("Enter age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        System.out.print("Enter country: ");
        String country = scanner.nextLine().trim();
        if (country.isEmpty()) {
            System.out.println("Error: Country cannot be empty!");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setGender(gender);
        user.setAge(age);
        user.setCountry(country);
        user.setAdmin(false);
        user.setActive(true);

        if (userDAO.createUser(user)) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed!");
        }
    }

    private static void searchFlights() throws SQLException {
        System.out.print("Enter departure airport: ");
        String departure = scanner.nextLine().trim();
        if (departure.isEmpty()) {
            System.out.println("Error: Departure airport cannot be empty!");
            return;
        }
        
        System.out.print("Enter arrival airport: ");
        String arrival = scanner.nextLine().trim();
        if (arrival.isEmpty()) {
            System.out.println("Error: Arrival airport cannot be empty!");
            return;
        }
        
        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine().trim();
        if (dateStr.isEmpty()) {
            System.out.println("Error: Date cannot be empty!");
            return;
        }

        List<Flight> flights = flightDAO.searchFlights(departure, arrival, java.sql.Date.valueOf(dateStr));
        if (flights.isEmpty()) {
            System.out.println("No flights found!");
            return;
        }

        System.out.println("\nAvailable Flights:");
        for (Flight flight : flights) {
            System.out.printf("Flight %s: %s -> %s, Departure: %s, Available Seats: %d%n",
                    flight.getFlightNumber(),
                    flight.getDepartureAirport(),
                    flight.getArrivalAirport(),
                    flight.getDepartureTime(),
                    flight.getAvailableSeats());
        }
    }

    private static void searchFlightsByPreference() throws SQLException {
        System.out.print("Enter departure airport: ");
        String departure = scanner.nextLine().trim();
        if (departure.isEmpty()) {
            System.out.println("Error: Departure airport cannot be empty!");
            return;
        }
        
        System.out.print("Enter arrival airport: ");
        String arrival = scanner.nextLine().trim();
        if (arrival.isEmpty()) {
            System.out.println("Error: Arrival airport cannot be empty!");
            return;
        }
        
        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine().trim();
        if (dateStr.isEmpty()) {
            System.out.println("Error: Date cannot be empty!");
            return;
        }
        
        System.out.print("Enter seat preference (WINDOW/AISLE): ");
        String seatPreference = scanner.nextLine().trim().toUpperCase();
        if (seatPreference.isEmpty()) {
            System.out.println("Error: Seat preference cannot be empty!");
            return;
        }

        List<Flight> flights = flightDAO.searchFlights(departure, arrival, java.sql.Date.valueOf(dateStr));
        if (flights.isEmpty()) {
            System.out.println("No flights found!");
            return;
        }

        System.out.println("\nAvailable Flights with " + seatPreference + " seats:");
        boolean found = false;
        for (Flight flight : flights) {
            // Calculate available seats for the preference
            int availableSeats = flight.getAvailableSeats();
            if (availableSeats > 0) {
                // Simple logic: Even rows have window seats, odd rows have aisle seats
                int availablePreferenceSeats = (seatPreference.equals("WINDOW") ? 
                    availableSeats / 2 : (availableSeats + 1) / 2);
                
                if (availablePreferenceSeats > 0) {
                    found = true;
                    System.out.printf("Flight %s: %s -> %s, Departure: %s, Available %s Seats: %d%n",
                            flight.getFlightNumber(),
                            flight.getDepartureAirport(),
                            flight.getArrivalAirport(),
                            flight.getDepartureTime(),
                            seatPreference,
                            availablePreferenceSeats);
                }
            }
        }
        
        if (!found) {
            System.out.println("No flights found with " + seatPreference + " seats available!");
        }
    }

    private static void viewMyBookings() throws SQLException {
        List<Booking> bookings = bookingDAO.getUserBookings(currentUser.getId());
        if (bookings.isEmpty()) {
            System.out.println("No bookings found!");
            return;
        }

        System.out.println("\nYour Bookings:");
        for (Booking booking : bookings) {
            System.out.printf("Booking Ref: %s, Flight ID: %d, Seat: %s, Date: %s%n",
                    booking.getBookingReference(),
                    booking.getFlightId(),
                    booking.getSeatNumber(),
                    booking.getBookingDate());
        }
    }

    private static void cancelBooking() throws SQLException {
        System.out.print("Enter booking reference to cancel: ");
        String bookingRef = scanner.nextLine().trim();
        if (bookingRef.isEmpty()) {
            System.out.println("Error: Booking reference cannot be empty!");
            return;
        }
        
        Booking booking = bookingDAO.getBookingByReference(bookingRef);
        if (booking != null && booking.getUserId() == currentUser.getId()) {
            // Get the flight and increment available seats
            Flight flight = flightDAO.getFlightById(booking.getFlightId());
            if (flight != null) {
                flight.setAvailableSeats(flight.getAvailableSeats() + 1);
                flightDAO.updateFlight(flight);
            }
            
            if (bookingDAO.updateBookingStatus(booking.getId(), false)) {
                System.out.println("Booking cancelled successfully!");
            } else {
                System.out.println("Failed to cancel booking!");
            }
        } else {
            System.out.println("Booking not found or unauthorized!");
        }
    }

    private static void addNewFlight() throws SQLException {
        System.out.print("Enter flight number: ");
        String flightNumber = scanner.nextLine().trim();
        if (flightNumber.isEmpty()) {
            System.out.println("Error: Flight number cannot be empty!");
            return;
        }
        
        System.out.print("Enter departure airport: ");
        String departure = scanner.nextLine().trim();
        if (departure.isEmpty()) {
            System.out.println("Error: Departure airport cannot be empty!");
            return;
        }
        
        System.out.print("Enter arrival airport: ");
        String arrival = scanner.nextLine().trim();
        if (arrival.isEmpty()) {
            System.out.println("Error: Arrival airport cannot be empty!");
            return;
        }
        
        System.out.print("Enter departure time (YYYY-MM-DD HH:mm): ");
        String departureTimeStr = scanner.nextLine().trim();
        if (departureTimeStr.isEmpty()) {
            System.out.println("Error: Departure time cannot be empty!");
            return;
        }
        
        System.out.print("Enter arrival time (YYYY-MM-DD HH:mm): ");
        String arrivalTimeStr = scanner.nextLine().trim();
        if (arrivalTimeStr.isEmpty()) {
            System.out.println("Error: Arrival time cannot be empty!");
            return;
        }
        
        System.out.print("Enter available seats: ");
        int seats = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        System.out.print("Enter airline name: ");
        String airline = scanner.nextLine().trim();
        if (airline.isEmpty()) {
            System.out.println("Error: Airline name cannot be empty!");
            return;
        }

        try {
            LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            if (departureTime.isAfter(arrivalTime)) {
                System.out.println("Error: Departure time cannot be later than arrival time!");
                return;
            }

            Flight flight = new Flight();
            flight.setFlightNumber(flightNumber);
            flight.setDepartureAirport(departure);
            flight.setArrivalAirport(arrival);
            flight.setDepartureTime(departureTime);
            flight.setArrivalTime(arrivalTime);
            flight.setAvailableSeats(seats);
            flight.setAirlineName(airline);
            flight.setActive(true);

            if (flightDAO.createFlight(flight)) {
                System.out.println("Flight added successfully!");
            } else {
                System.out.println("Failed to add flight!");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date/time format. Please use YYYY-MM-DD HH:mm format (e.g., 2024-03-02 14:30)");
        }
    }

    private static void viewAllFlights() throws SQLException {
        List<Flight> flights = flightDAO.getAllFlights();
        if (flights.isEmpty()) {
            System.out.println("No flights found!");
            return;
        }

        System.out.println("\nAll Flights:");
        for (Flight flight : flights) {
            System.out.printf("Flight %s: %s -> %s, Departure: %s, Available Seats: %d%n",
                    flight.getFlightNumber(),
                    flight.getDepartureAirport(),
                    flight.getArrivalAirport(),
                    flight.getDepartureTime(),
                    flight.getAvailableSeats());
        }
    }

    private static void cancelFlight() throws SQLException {
        System.out.print("Enter flight number to cancel: ");
        String flightNumber = scanner.nextLine().trim();
        if (flightNumber.isEmpty()) {
            System.out.println("Error: Flight number cannot be empty!");
            return;
        }
        
        Flight flight = flightDAO.getFlightByNumber(flightNumber);
        if (flight != null) {
            // Check if there are any active bookings for this flight
            List<Booking> bookings = bookingDAO.getBookingsByFlightId(flight.getId());
            if (!bookings.isEmpty()) {
                System.out.println("Cannot cancel flight with active bookings!");
                return;
            }
            
            flight.setActive(false);
            if (flightDAO.updateFlightStatus(flight.getId(), false)) {
                System.out.println("Flight cancelled successfully!");
            } else {
                System.out.println("Failed to cancel flight!");
            }
        } else {
            System.out.println("Flight not found!");
        }
    }

    private static void viewAllUsers() throws SQLException {
        List<User> users = userDAO.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found!");
            return;
        }

        System.out.println("\nAll Users:");
        for (User user : users) {
            System.out.printf("Username: %s, Age: %d, Country: %s, Status: %s%n",
                    user.getUsername(),
                    user.getAge(),
                    user.getCountry(),
                    user.isActive() ? "Active" : "Suspended");
        }
    }

    private static void suspendUser() throws SQLException {
        System.out.print("Enter username to suspend: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println("Error: Username cannot be empty!");
            return;
        }
        
        User user = userDAO.getUserByUsername(username);
        if (user != null) {
            if (userDAO.updateUserStatus(user.getId(), false)) {
                System.out.println("User suspended successfully!");
            } else {
                System.out.println("Failed to suspend user!");
            }
        } else {
            System.out.println("User not found!");
        }
    }

    private static void bookFlight() throws SQLException {
        System.out.print("Enter flight number to book: ");
        String flightNumber = scanner.nextLine().trim();
        if (flightNumber.isEmpty()) {
            System.out.println("Error: Flight number cannot be empty!");
            return;
        }
        
        // Get flight details
        Flight selectedFlight = flightDAO.getFlightByNumber(flightNumber);
        
        if (selectedFlight == null) {
            System.out.println("Flight not found!");
            return;
        }
        
        if (selectedFlight.getAvailableSeats() <= 0) {
            System.out.println("No seats available on this flight!");
            return;
        }
        
        // Generate booking reference (simple format: BK + random number)
        String bookingRef = "BK" + (100000 + (int)(Math.random() * 900000));
        
        // Generate seat number (row + seat letter)
        int row = (selectedFlight.getAvailableSeats() % 30 + 1);
        // Use different seat letters (A, B, C, D) based on the booking number
        String[] seatLetters = {"A", "B", "C", "D"};
        String seatLetter = seatLetters[selectedFlight.getAvailableSeats() % 4];
        String seatNumber = row + seatLetter;
        
        // Create booking
        Booking booking = new Booking();
        booking.setBookingReference(bookingRef);
        booking.setUserId(currentUser.getId());
        booking.setFlightId(selectedFlight.getId());
        booking.setBookingDate(LocalDateTime.now());
        booking.setSeatNumber(seatNumber);
        booking.setSeatPreference("WINDOW"); // Default to window seat
        booking.setActive(true);
        
        if (bookingDAO.createBooking(booking)) {
            // Update available seats
            selectedFlight.setAvailableSeats(selectedFlight.getAvailableSeats() - 1);
            flightDAO.updateFlight(selectedFlight);
            
            System.out.println("\nBooking successful!");
            System.out.printf("Booking Reference: %s%n", bookingRef);
            System.out.printf("Flight: %s%n", selectedFlight.getFlightNumber());
            System.out.printf("Seat: %s%n", seatNumber);
            System.out.printf("Departure: %s%n", selectedFlight.getDepartureTime());
            System.out.printf("Arrival: %s%n", selectedFlight.getArrivalTime());
        } else {
            System.out.println("Failed to create booking!");
        }
    }
} 