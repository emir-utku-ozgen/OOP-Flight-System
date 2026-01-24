package services_managers;

import java.io.*;
import java.time.*;
import java.util.*;
import flight_management.*;
import reservation_ticketing.*;

public class ReservationManager {
    private List<Reservation> reservations;
    private final String FILENAME = "reservations.txt";
    private SeatManager seatManager;
    private FlightManager flightManager;
    
    public ReservationManager(FlightManager flightManager) {
        this.reservations = new ArrayList<>();
        this.seatManager = new SeatManager();
        this.flightManager = flightManager;
        loadReservationsFromFile(); 
    }
    
 
    public void createReservation(Reservation reservation) {
        reservations.add(reservation);
        saveReservationsToFile();
        System.out.println("Reservation added via object! Price: " + reservation.getTotalPrice());
    }
    
    public List<Reservation> getReservationsByName(String searchName) {
        List<Reservation> filteredList = new ArrayList<>();
        
   
        if (searchName == null || searchName.trim().isEmpty()) {
            return filteredList;
        }

        for (Reservation r : reservations) {
            if (r.getPassenger().getName().trim().equalsIgnoreCase(searchName.trim())) {
                filteredList.add(r);
            }
        }
        return filteredList;
    }

    public void createReservation(Flight flight, Passenger passenger, String seatNum, String ownerUsername) {
        try {
            Seat seat = seatManager.findSeatByNumber(flight.getPlane(), seatNum);
            
            if (seat == null || seat.isReserveStatus()) {
                System.out.println("Hata: " + seatNum + " koltuğu zaten dolu!");
                return;
            }
            seat.setReserveStatus(true);
            String resCode = generateResCode();
            String currentDate = java.time.LocalDate.now().toString();
            double price = seat.getPrice(); 

            Reservation res = new Reservation(
                resCode, 
                flight, 
                passenger, 
                seat, 
                currentDate, 
                price, 
                passenger.getName(), 
                ownerUsername        
            );
            res.setActive(true);
            reservations.add(res);
            saveReservationsToFile();
            System.out.println("Reservation is successful! Code: " + resCode);

        } catch (Exception e) {
            System.out.println("Rezervasyon Hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void makeSimulationReservation(Flight flight, boolean isSync) {
        if (isSync) {
            bookSeatSynchronized(flight);
        } else {
            bookSeatUnsynchronized(flight);
        }
    }
    
    private synchronized void bookSeatSynchronized(Flight flight) {
        Seat seat = seatManager.getRandomAvailableSeat(flight.getPlane());
        
        if (seat != null) {
            seat.setReserveStatus(true);

            Passenger botUser = new Passenger("SimBot", "11111", "555-0000", "bot@sim.com");
            
            String resCode = "SYNC-" + System.currentTimeMillis(); 
            String date = java.time.LocalDate.now().toString(); 
            double price = seat.getPrice();
            
            Reservation newRes = new Reservation(
                resCode, 
                flight, 
                botUser, 
                seat, 
                date, 
                price, 
                "Sim Yolcusu", 
                "SYSTEM_BOT"    
            );
            
            this.reservations.add(newRes);            
            System.out.println(Thread.currentThread().getName() + " koltuğu kaptı: " + seat.getSeatNum());
        }
    }
    
    private void bookSeatUnsynchronized(Flight flight) {
        Seat seat = seatManager.getRandomAvailableSeat(flight.getPlane());
        
        if (seat != null) {         
            try { 
                Thread.sleep(20); 
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            }
            
            seat.setReserveStatus(true);
            
            Passenger botUser = new Passenger("Zeynel", "00000", "000-0000", "zeynel@com");
            
            String resCode = "UNSYNC-" + System.currentTimeMillis();
            String date = java.time.LocalDate.now().toString();

            double price = seat.getPrice();

            Reservation newRes = new Reservation(
                resCode, 
                flight, 
                botUser, 
                seat, 
                date, 
                price, 
                "RaceBot Yolcusu",  
                "SYSTEM_RACE_BOT"   
            );

            this.reservations.add(newRes);

            System.out.println(Thread.currentThread().getName() + " (Unsafe) İşlem yaptı.");
        }
    }
    public void cancelReservation(String resCode) {
        Reservation found = findReservationByCode(resCode);
        
        if (found != null && found.isActive()) {
            found.getSeat().setReserveStatus(false);
            found.setActive(false);  
            saveReservationsToFile();
            System.out.println("Reservation is cancelled. " + resCode);
        } else {
            System.out.println("ERROR!");
        }
    }
    
    public Reservation findReservationByCode(String resCode) {
        for (Reservation r : reservations) {
            if (r.getReservationCode().equalsIgnoreCase(resCode)) {
                return r;
            }
        }
        return null;
    }
    
    public List<Reservation> getReservationsByPassenger(String passengerID) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getPassenger().getPassengerID().equals(passengerID) && r.isActive()) {
                result.add(r);
            }
        }
        return result;
    }

    private String generateResCode() {
        return "PNR" + (10000 + new Random().nextInt(90000));
    }
    
    public List<Reservation> getAllReservations() {
        return reservations;
    }


    public void saveReservationsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            for (Reservation r : reservations) {
                if (r.isActive()) {
                   
                    String line = String.format("%s,%s,%s,%s,%s,%s,%.2f",
                        r.getReservationCode(),
                        r.getFlight().getFlightNum(),
                        r.getPassenger().getPassengerID(),
                        r.getPassenger().getName(),
                        r.getSeat().getSeatNum(),
                        r.getDateOfReservation(),
                        r.getTotalPrice() 
                    );
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Yazma Hatası: " + e.getMessage());
        }
    }

  
    public void loadReservationsFromFile() {
        File file = new File(FILENAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                
               
                if (parts.length >= 7) { 
                    String pnr = parts[0];
                    String flightNum = parts[1];
                    String passId = parts[2];
                    String passName = parts[3]; 
                    String seatNum = parts[4];
                    String date = parts[5];

                    double price = Double.parseDouble(parts[6].replace(",", "."));

                    
                    String ownerUsername = (parts.length >= 8) ? parts[7] : "Bilinmiyor"; 
             

                    Flight flight = flightManager.findFlightById(flightNum);
                    if (flight != null) {
                        Seat seat = seatManager.findSeatByNumber(flight.getPlane(), seatNum);
                        if (seat != null) {
                            seat.setReserveStatus(true); 
                            Passenger passenger = new Passenger(passId, passName, "", "");
                            
                            
                            Reservation res = new Reservation(pnr, flight, passenger, seat, date, price, passName, ownerUsername);
                            
                            res.setActive(true);
                            reservations.add(res);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("DEBUG: Okuma Hatası -> " + e.getMessage());
            e.printStackTrace(); 
        }
    }
    public List<Reservation> getReservationsByUser(String username) {
       
        List<Reservation> userReservations = new ArrayList<>();
        
        for (Reservation res : getAllReservations()) { 
            if (res.getOwnerUsername().equals(username)) {
                userReservations.add(res);
            }
        }
        return userReservations;
    }
    public void addReservation(Reservation res) {
        this.reservations.add(res);
        saveReservationsToFile(); 
    }
}