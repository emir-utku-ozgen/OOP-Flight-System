package services_managers;

import flight_management.*;
import java.io.*;
import java.util.*;
import java.time.LocalDate;

public class FlightManager {
    private List<Flight> flights;
    private final String FILENAME = "flights.txt";

    public FlightManager() {
        this.flights = new ArrayList<>();
        loadFlightsFromFile();
    }

    public void createFlight(Flight flight) {
        java.time.LocalDate flightDate = java.time.LocalDate.parse(flight.getDate());
        
        // Eğer tarih bugünden önceyse listeye ekleme ve metodu bitir
        if (flightDate.isBefore(java.time.LocalDate.now())) {
            System.out.println("LOG: Geçmiş tarihli veri kaydedilmeden engellendi.");
            return; 
        }

        flights.add(flight);
        saveFlightsToFile();
    }

    public List<Flight> getAllFlights() {
        return flights;
    }

    public List<Flight> searchFlights(String departure, String arrival) {
        List<Flight> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Flight f : flights) {
            boolean routeMatch = f.getRoute().getDeparturePlace().equalsIgnoreCase(departure) &&
                                 f.getRoute().getArrivalPlace().equalsIgnoreCase(arrival);
            
            boolean dateIsValid = false;
            try {
                LocalDate flightDate = LocalDate.parse(f.getDate());
                if (!flightDate.isBefore(today)) {
                    dateIsValid = true;
                }
            } catch (Exception e) { 
                dateIsValid = false; 
            }

            if (routeMatch && dateIsValid) {
                result.add(f);
            }
        }
        return result;
    }

    public void deleteFlight(String flightNum) {
        flights.removeIf(f -> f.getFlightNum().equals(flightNum));
        saveFlightsToFile();
    }
    
    public void updateFlight(Flight updatedFlight) {
        for(int i=0; i<flights.size(); i++) {
            if(flights.get(i).getFlightNum().equals(updatedFlight.getFlightNum())) {
                flights.set(i, updatedFlight);
                break;
            }
        }
        saveFlightsToFile(); 
    }
    
    public Flight findFlightById(String flightNum) {
        for(Flight f : flights) {
            if(f.getFlightNum().equals(flightNum)) return f;
        }
        return null;
    }

    private void loadFlightsFromFile() {
        File file = new File(FILENAME);
        if (!file.exists()) return; 

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.trim().isEmpty()) continue; 

                String[] parts = line.split(",");
                
                if (parts.length >= 9) {
                    String fNum = parts[0].trim();
                    String dep = parts[1].trim();
                    String arr = parts[2].trim();
                    String date = parts[3].trim();
                    String time = parts[4].trim();
                    String dur = parts[5].trim();
                    
                    String pId = parts[6].trim();
                    String pModel = parts[7].trim();
                    int pCap = Integer.parseInt(parts[8].trim());

                    Plane plane = new Plane(pId, pModel, pCap);
                    Flight flight = new Flight(fNum, dep, arr, date, time, dur, plane);
                    
                    flights.add(flight);
                }
            }
        } catch (IOException e) {
            System.err.println("Dosya okuma hatası: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Sayı formatı hatası (Kapasite alanı bozuk olabilir): " + e.getMessage());
        }
    }

    public void saveFlightsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            for (Flight f : flights) {
                Plane p = f.getPlane();
                
                String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%d",
                        f.getFlightNum(),
                        f.getRoute().getDeparturePlace(),
                        f.getRoute().getArrivalPlace(),
                        f.getDate(),
                        f.getHour(),
                        f.getDuration(),
                        p.getPlaneID(),
                        p.getPlaneModel(),
                        p.getCapacity()
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Dosya yazma hatası: " + e.getMessage());
        }
    }
}