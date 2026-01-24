package services_managers;

import flight_management.*;
import javax.swing.*; // GUI'ye erişim için
import java.util.List;

public class ReportGenerator implements Runnable {
    
    private FlightManager flightManager;
    private JTextArea reportOutputArea; 
    private JLabel statusLabel;         
    public ReportGenerator(FlightManager flightManager, JTextArea reportOutputArea, JLabel statusLabel) {
        this.flightManager = flightManager;
        this.reportOutputArea = reportOutputArea;
        this.statusLabel = statusLabel;
    }
    
    @Override
    public void run() {
        
        SwingUtilities.invokeLater(() -> statusLabel.setText("Durum: Rapor Hazırlanıyor... (Preparing report...)"));

        try {
            
            Thread.sleep(10000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        
        List<Flight> flights = flightManager.getAllFlights();
        int totalCapacity = 0;
        int totalOccupied = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("=== DETAYLI DOLULUK RAPORU ===\n");
        sb.append("------------------------------\n");

        for (Flight f : flights) {
            int cap = f.getPlane().getCapacity();
           
            int occ = 0; 
            for(int i=0; i<f.getPlane().getSeatMatrix().length; i++) {
                for(int j=0; j<f.getPlane().getSeatMatrix()[0].length; j++) {
                     if(f.getPlane().getSeatMatrix()[i][j].isReserveStatus()) occ++;
                }
            }
            
            totalCapacity += cap;
            totalOccupied += occ;
            
            sb.append(String.format("Uçuş: %-8s | Doluluk: %d/%d\n", f.getFlightNum(), occ, cap));
        }

        double rate = (totalCapacity > 0) ? ((double) totalOccupied / totalCapacity) * 100 : 0;
        
        sb.append("------------------------------\n");
        sb.append(String.format("GENEL DOLULUK ORANI: %% %.2f\n", rate));

       
        SwingUtilities.invokeLater(() -> {
            reportOutputArea.setText(sb.toString());
            statusLabel.setText("Durum: Rapor Tamamlandı ✅");
        });
    }
}