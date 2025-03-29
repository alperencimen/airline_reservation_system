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
            System.out.println("2. GUI Interface (Not Available Yet)");
            System.out.print("Choose interface type: ");

            int interfaceChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (interfaceChoice == 1) {
                startTerminalInterface();
            } else if (interfaceChoice == 2) {
                System.out.println("GUI interface is not available yet. Please use terminal interface.");
                startTerminalInterface();
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
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = userDAO.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password!");
        }
    }

    private static void register() throws SQLException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter gender: ");
        String gender = scanner.nextLine();
        System.out.print("Enter age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Enter country: ");
        String country = scanner.nextLine();

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
        String departure = scanner.nextLine();
        System.out.print("Enter arrival airport: ");
        String arrival = scanner.nextLine();
        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine();

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
        String departure = scanner.nextLine();
        System.out.print("Enter arrival airport: ");
        String arrival = scanner.nextLine();
        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine();
        System.out.print("Enter seat preference (WINDOW/AISLE): ");
        String seatPreference = scanner.nextLine().toUpperCase();

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
        String bookingRef = scanner.nextLine();
        
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
        String flightNumber = scanner.nextLine();
        System.out.print("Enter departure airport: ");
        String departure = scanner.nextLine();
        System.out.print("Enter arrival airport: ");
        String arrival = scanner.nextLine();
        System.out.print("Enter departure time (yyyy-MM-dd HH:mm): ");
        String departureTimeStr = scanner.nextLine();
        System.out.print("Enter arrival time (yyyy-MM-dd HH:mm): ");
        String arrivalTimeStr = scanner.nextLine();
        System.out.print("Enter available seats: ");
        int seats = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Enter airline name: ");
        String airline = scanner.nextLine();

        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setDepartureAirport(departure);
        flight.setArrivalAirport(arrival);
        flight.setDepartureTime(LocalDateTime.parse(departureTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        flight.setArrivalTime(LocalDateTime.parse(arrivalTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        flight.setAvailableSeats(seats);
        flight.setAirlineName(airline);
        flight.setActive(true);

        if (flightDAO.createFlight(flight)) {
            System.out.println("Flight added successfully!");
        } else {
            System.out.println("Failed to add flight!");
        }
    }

    private static void viewAllFlights() throws SQLException {
        // This is a simplified version - you might want to add more search parameters
        List<Flight> flights = flightDAO.searchFlights("", "", java.sql.Date.valueOf(LocalDateTime.now().toLocalDate()));
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
        String flightNumber = scanner.nextLine();
        
        // You'll need to add a method in FlightDAO to get flight by number
        // For now, this is a placeholder
        System.out.println("Flight cancellation functionality to be implemented!");
    }

    private static void viewAllUsers() throws SQLException {
        // You'll need to add a method in UserDAO to get all users
        System.out.println("View all users functionality to be implemented!");
    }

    private static void suspendUser() throws SQLException {
        System.out.print("Enter username to suspend: ");
        String username = scanner.nextLine();
        
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
        String flightNumber = scanner.nextLine();
        
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
        
        // Generate seat number (simple format: row + seat)
        String seatNumber = (selectedFlight.getAvailableSeats() % 30 + 1) + "A";
        
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