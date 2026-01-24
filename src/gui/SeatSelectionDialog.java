package gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Random;
import flight_management.*;
import reservation_ticketing.*;
import services_managers.*;

public class SeatSelectionDialog extends JDialog {

    private Flight flight;
    private MainFrame mainFrame;
    private JPanel seatPanel;

    public SeatSelectionDialog(MainFrame mainFrame, Flight flight) {
        super(mainFrame, "Koltuk Seçimi: " + flight.getFlightNum(), true);
        this.mainFrame = mainFrame;
        this.flight = flight;

        setSize(600, 700);
        setLocationRelativeTo(mainFrame);
        setLayout(new BorderLayout());

  
        JLabel infoLabel = new JLabel("Lütfen bir koltuk seçiniz (Yeşil: Boş, Kırmızı: Dolu)", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(infoLabel, BorderLayout.NORTH);

        Seat[][] seats = flight.getPlane().getSeatMatrix();
        int rows = seats.length;
        int cols = seats[0].length;

        seatPanel = new JPanel(new GridLayout(rows, cols, 5, 5));
        seatPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Seat seat = seats[i][j];
                JButton seatBtn = new JButton(seat.getSeatNum());
                seatBtn.setOpaque(true);
                seatBtn.setBorderPainted(false);

                if (seat.isReserveStatus()) {
                    seatBtn.setBackground(new Color(231, 76, 60)); 
                    seatBtn.setEnabled(false);
                } else {
                    seatBtn.setBackground(new Color(46, 204, 113));

                    seatBtn.addActionListener(e -> makeReservation(seat, seatBtn));
                }
                seatPanel.add(seatBtn);
            }
        }
        
        add(new JScrollPane(seatPanel), BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("Kapat");
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn, BorderLayout.SOUTH);
    }

    private void makeReservation(Seat seat, JButton btn) {

        String inputWeight = JOptionPane.showInputDialog(this, 
                "Bagaj ağırlığınızı giriniz (kg):\n(İlk 15 KG Ücretsiz, sonrası 50 TL/kg)", "0");
        
        if (inputWeight == null) return; 

        try {
            double weight = Double.parseDouble(inputWeight);
            

            double seatPrice = seat.getPrice();
                        double baggageFee = CalculatePrice.calculateBaggageFee(weight);
            double totalPrice = seatPrice + baggageFee;

     
            String message = String.format(
                "=== BİLET ÖZETİ ===\n" +
                "Koltuk No: %s (%s)\n" +
                "Koltuk Fiyatı: %.2f TL\n" +
                "Bagaj: %.1f KG (Ekstra Ücret: %.2f TL)\n" +
                "--------------------------------\n" +
                "TOPLAM TUTAR: %.2f TL\n\n" +
                "Satın almayı onaylıyor musunuz?",
                seat.getSeatNum(),
                seat.getSeatClass(),
                seatPrice,
                weight, baggageFee,
                totalPrice
            );
                             
            int confirm = JOptionPane.showConfirmDialog(this, message, "Ödeme Onayı", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                String passengerName = JOptionPane.showInputDialog(this, "Yolcu Adı Soyadı:");
                
                if (passengerName != null && !passengerName.trim().isEmpty()) {
                    
                  
                    String ownerUsername = "Misafir";
                    if (mainFrame.getCurrentUser() != null) {
                        ownerUsername = mainFrame.getCurrentUser().getUsername();
                    }
                    

                    String pnrCode = "PNR-" + (10000 + new Random().nextInt(90000));
                    String currentDate = LocalDate.now().toString();
                   
                    Passenger passenger = new Passenger("ID-" + System.currentTimeMillis(), passengerName, "", "");
                    
                  
                    Reservation newRes = new Reservation(
                        pnrCode,             
                        flight,             
                        passenger,         
                        seat,                
                        currentDate,     
                        totalPrice,          
                        passenger.getName(), 
                        ownerUsername        
                    );
                    
                  
                    mainFrame.getReservationManager().addReservation(newRes);
                    
              
                    seat.setReserveStatus(true);
                    

                    JOptionPane.showMessageDialog(this, 
                        "Sayın " + passengerName + ",\n" +
                        "Biletiniz başarıyla oluşturuldu!\n" +
                        "PNR Kodunuz: " + pnrCode + "\n" +
                        "Toplam Ödenen: " + totalPrice + " TL");
                    
                   
                    btn.setBackground(new Color(231, 76, 60)); 
                    btn.setEnabled(false);
                    
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli bir sayı giriniz!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}