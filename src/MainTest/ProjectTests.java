package MainTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import flight_management.*;
import services_managers.*;

public class ProjectTests {

    private FlightManager flightManager;
    private SeatManager seatManager;
    private Plane plane;
    
    private Flight futureFlight;
    private Flight pastFlight;

    @BeforeEach
    void setUp() {
        flightManager = new FlightManager();
        seatManager = new SeatManager();

        plane = new Plane("TEST-PLN", "Boeing 737", 180);

        String tomorrow = LocalDate.now().plusDays(1).toString(); 
        futureFlight = new Flight("TK-FUTURE", "Istanbul", "Ankara", tomorrow, "10:00", "1h", plane);

  
        String yesterday = LocalDate.now().minusDays(1).toString();
        pastFlight = new Flight("TK-PAST", "Istanbul", "Ankara", yesterday, "10:00", "1h", plane);

        flightManager.createFlight(futureFlight);
        flightManager.createFlight(pastFlight);
    }

    @Test
    void testPriceCalculation() {
   
        Seat businessSeat = plane.getSeatMatrix()[0][0]; 
        assertEquals(2500.0, businessSeat.getPrice(), "Hata: Business koltuk fiyatı yanlış hesaplanmış.");

        Seat economySeat = plane.getSeatMatrix()[10][0]; 
        assertEquals(1000.0, economySeat.getPrice(), "Hata: Economy koltuk fiyatı yanlış hesaplanmış.");
    }


    @Test
    void testFlightSearchRetrievesCorrectRoute() {
    
        List<Flight> wrongRoute = flightManager.searchFlights("Istanbul", "Izmir");
        assertTrue(wrongRoute.isEmpty(), "Hata: Yanlış rota için uçuş bulunmamalıydı.");

      
        List<Flight> correctRoute = flightManager.searchFlights("Istanbul", "Ankara");
        assertFalse(correctRoute.isEmpty(), "Hata: Uygun uçuş bulunamadı.");
        
    
        boolean found = false;
        for (Flight f : correctRoute) {
            if (f.getFlightNum().equals("TK-FUTURE")) found = true;
        }
        assertTrue(found, "Hata: Gelecek tarihli uçuş (TK-FUTURE) sonuçlarda çıkmalıydı.");
    }

    @Test
    void testFlightSearchEliminatesPastFlights() {
        List<Flight> results = flightManager.searchFlights("Istanbul", "Ankara");

        boolean foundPast = false;
        for (Flight f : results) {
            if (f.getFlightNum().equals("TK-PAST")) foundPast = true;
        }

        assertFalse(foundPast, "Hata: Tarihi geçmiş uçuş (TK-PAST) listeden elenmemiş!");
    }

    @Test
    void testEmptySeatsCountDecrease() throws Exception {
        int initialCount = seatManager.getAvailableSeatCount(plane);

        Seat seat = seatManager.findSeatByNumber(plane, "5A");
        assertFalse(seat.isReserveStatus(), "Test koltuğu başlangıçta dolu olmamalı.");
        seat.setReserveStatus(true); 
        
        
        int newCount = seatManager.getAvailableSeatCount(plane);
        
        assertEquals(initialCount - 1, newCount, "Hata: Koltuk rezerve edilince boş koltuk sayısı 1 azalmalı.");
    }

    @Test
    void testInvalidSeatException() {
        String invalidSeatNum = "99Z"; 

        Exception exception = assertThrows(Exception.class, () -> {
            seatManager.findSeatByNumber(plane, invalidSeatNum);
        }, "Hata: Olmayan koltuk için Exception fırlatılmalıydı.");
    }
}