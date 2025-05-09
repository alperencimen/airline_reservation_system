package com.airline.controller;

import com.airline.dao.FlightDAO;
import com.airline.model.Flight;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightControllerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private FlightDAO mockDao;
    private FlightController controller;

    @BeforeEach
    void setUp() {
        // System.out çıktısını yakalamak için yönlendir
        System.setOut(new PrintStream(outContent));

        // FlightDAO'yu mock'la
        mockDao = Mockito.mock(FlightDAO.class);
        controller = new FlightController(mockDao);
    }

    @AfterEach
    void tearDown() {
        // Orijinalliği geri koy
        System.setOut(originalOut);
    }

    @Test
    void handleRequest_shouldPrintAllFlightNumbers() throws Exception {
        // Arrange: sahte uçuş listesi
        Flight f1 = new Flight();
        f1.setFlightNumber("TK123");
        f1.setDepartureTime(LocalDateTime.now());
        f1.setArrivalTime(LocalDateTime.now());
        f1.setAvailableSeats(0);
        f1.setTotalSeats(0);

        Flight f2 = new Flight();
        f2.setFlightNumber("AA456");
        f2.setDepartureTime(LocalDateTime.now());
        f2.setArrivalTime(LocalDateTime.now());
        f2.setAvailableSeats(0);
        f2.setTotalSeats(0);

        when(mockDao.getAllFlights()).thenReturn(List.of(f1, f2));

        // Act
        controller.handleRequest();

        // Assert: çıktıda her iki flightNumber da olmalı
        String output = outContent.toString().trim();
        assertTrue(output.contains("TK123"), "Çıktıda TK123 yazmalı");
        assertTrue(output.contains("AA456"), "Çıktıda AA456 yazmalı");

        // DAO'nun gerçekten çağrıldığını doğrulayalım
        verify(mockDao, times(1)).getAllFlights();
    }
}
