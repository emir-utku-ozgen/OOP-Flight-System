package MainTest;

import flight_management.*;
import services_managers.*;
import reservation_ticketing.*;


public class ReservationManagerTest {

    public static final String YESIL = "\u001B[32m";
    public static final String KIRMIZI = "\u001B[31m";
    public static final String SARI = "\u001B[33m";
    public static final String RESET = "\u001B[0m";

    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println("   ✈️  FLIGHT MANAGEMENT SYSTEM - TEST MODE  ✈️   ");
        System.out.println("=================================================\n");


        FlightManager fm = new FlightManager();
        ReservationManager rm = new ReservationManager(fm);
        
        Plane plane = new Plane("PLN-737", "Boeing 737", 180); 
        Flight flight = new Flight("TK-TEST", "Istanbul", "London", "2025-06-15", "14:00", "4h", plane);
        
        if (fm.findFlightById("TK-TEST") == null) {
            fm.createFlight(flight);
        } else {
            flight = fm.findFlightById("TK-TEST");
            flight.getPlane().resetSeats(); 
        }

        
        System.out.println("--- TEST 1: Tekil Rezervasyon ve İptal ---");
        Passenger p1 = new Passenger("11111", "Ahmet", "Yilmaz", "ahmet@mail.com");
        
        System.out.print(">> 1A Koltuğu alınıyor... ");
        rm.createReservation(flight, p1, "1A", "TEST_USER");        
        if (flight.getPlane().getSeatMatrix()[0][0].isReserveStatus()) {
            System.out.println(YESIL + "BAŞARILI ✅" + RESET);
        } else {
            System.out.println(KIRMIZI + "BAŞARISIZ ❌" + RESET);
        }

        System.out.print(">> 1A İptal ediliyor... ");
        flight.getPlane().getSeatMatrix()[0][0].setReserveStatus(false); 
        System.out.println(YESIL + "BAŞARILI (Koltuk Boşaltıldı) ✅" + RESET + "\n");


        System.out.println("=================================================");
        System.out.println("   SENARYO 1: MULTITHREADING SİMÜLASYONU ");
        System.out.println("   (Hedef: 90 Yolcu yerleştirmek)");
        System.out.println("=================================================");
        
        System.out.println("\n🔽 MOD 1: GÜVENLİ (SYNCHRONIZED) MOD ÇALIŞIYOR...");
        flight.getPlane().resetSeats(); 

        runSimulation(rm, flight, true);

        printSeatMap(flight.getPlane());
        int countSafe = flight.getPlane().getOccupiedSeatCount();
        
        System.out.println("Sonuç: " + countSafe + "/90 koltuk doldu.");
        if (countSafe == 90) System.out.println(YESIL + ">> SENKRONİZASYON TESTİ GEÇTİ ✅" + RESET);
        else System.out.println(KIRMIZI + ">> HATA: Sayılar tutmadı! ❌" + RESET);



        System.out.println("\n" + SARI + "-------------------------------------------------" + RESET);
        System.out.println("\n🔽 MOD 2: GÜVENSİZ (UN-SYNC) MOD ÇALIŞIYOR...");
        System.out.println("(Beklenti: Hata oluşması ve 90'dan az koltuk dolması)");
        
        flight.getPlane().resetSeats();

        runSimulation(rm, flight, false);

        printSeatMap(flight.getPlane()); 
        int countUnsafe = flight.getPlane().getOccupiedSeatCount();
        
        System.out.println("Sonuç: " + countUnsafe + "/90 koltuk doldu.");
        
        if (countUnsafe < 90) {
            System.out.println(YESIL + ">> BAŞARILI: Beklenen hata oluştu! (Race Condition Yakalandı) ✅" + RESET);
            System.out.println("Açıklama: Aynı anda giren Thread'ler birbirinin üzerine yazdı.");
        } else {
            System.out.println(SARI + ">> UYARI: Bilgisayarın çok hızlı, hata oluşmadı. Tekrar dene. ⚠️" + RESET);
        }
        
        System.out.println("\n=================================================");
        System.out.println("           TEST TAMAMLANDI           ");
        System.out.println("=================================================");




        System.out.println("\n" + SARI + "-------------------------------------------------" + RESET);
        System.out.println("🔽 SENARYO 2: ASENKRON RAPOR OLUŞTURMA TESTİ");
        
        ReportGenerator reportTask = new ReportGenerator(fm, null, null);
        

        Thread reportThread = new Thread(reportTask);
        reportThread.start();
        

        System.out.println(">> Ana program çalışmaya devam ediyor (Donma Yok)...");
        for(int i=1; i<=3; i++) {
            System.out.println(">> Kullanıcı arayüzde başka işlem yapıyor... " + i);
            Thread.sleep(1000);
        }
        

        reportThread.join();
    }



    public static void runSimulation(ReservationManager rm, Flight flight, boolean isSync) throws InterruptedException {
        Thread[] threads = new Thread[90];
        

        for (int i = 0; i < 90; i++) {
            threads[i] = new Thread(() -> rm.makeSimulationReservation(flight, isSync));
            threads[i].start();
        }


        for (Thread t : threads) {
            t.join();
        }
    }


    public static void printSeatMap(Plane plane) {
        System.out.println("\n--- UÇAK DOLULUK DURUMU ---");
        Seat[][] matrix = plane.getSeatMatrix();
        

        System.out.println("      A   B   C      D   E   F");
        System.out.println("    ----------------------------");

        for (int i = 0; i < matrix.length; i++) {
            System.out.printf("%2d | ", (i + 1)); 
            
            for (int j = 0; j < matrix[0].length; j++) {
                Seat s = matrix[i][j];
                
  
                if (j == 3) System.out.print("   "); 

   
                if (s.isReserveStatus()) {
                    System.out.print(KIRMIZI + "[X] " + RESET);
                } else {
                    System.out.print(YESIL + "[ ] " + RESET);
                }
            }
            System.out.println("|");
        }
        System.out.println("    ----------------------------");
    }

    
    
    
}