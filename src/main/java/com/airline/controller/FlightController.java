package com.airline.controller;

import com.airline.dao.FlightDAO;
import com.airline.model.Flight;

import java.util.List;

public class FlightController implements ARSController {
    private final FlightDAO flightDAO;

    // Varsayılan constructor prod kodu için
    public FlightController() {
        this(new FlightDAO());
    }

    // Testlerde mock geçebilmek için ek constructor
    public FlightController(FlightDAO flightDAO) {
        this.flightDAO = flightDAO;
    }

    @Override
    public void handleRequest() {
        try {
            List<Flight> flights = flightDAO.getAllFlights();
            flights.forEach(f -> System.out.println(f.getFlightNumber()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
