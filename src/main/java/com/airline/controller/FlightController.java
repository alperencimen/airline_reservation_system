package com.airline.controller;

import com.airline.dao.FlightDAO;
import com.airline.model.Flight;
import java.util.List;

public class FlightController implements ARSController {
    private FlightDAO flightDAO;

    public FlightController() {
        this.flightDAO = new FlightDAO();
    }

    @Override
    public void handleRequest() {
        try {
            List<Flight> flights = flightDAO.getAllFlights();
            flights.forEach(flight -> System.out.println(flight.getFlightNumber()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}