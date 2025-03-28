package com.airline.model;

import java.time.LocalDateTime;

public class Booking {
    private int id;
    private String bookingReference;
    private int userId;
    private int flightId;
    private LocalDateTime bookingDate;
    private String seatNumber;
    private String seatPreference; // WINDOW or AISLE
    private boolean isActive;

    // Getters
    public int getId() {
        return id;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public int getUserId() {
        return userId;
    }

    public int getFlightId() {
        return flightId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getSeatPreference() {
        return seatPreference;
    }

    public boolean isActive() {
        return isActive;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setSeatPreference(String seatPreference) {
        this.seatPreference = seatPreference;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
} 