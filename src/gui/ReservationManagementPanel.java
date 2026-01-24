package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import reservation_ticketing.Reservation;
import flight_management.User;

public class ReservationManagementPanel extends JPanel {
    private MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;

    public ReservationManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));


        JLabel titleLabel = new JLabel("Rezervasyon Yönetimi", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

    
        String[] columns = {"PNR Kodu", "Uçuş No", "Yolcu Adı", "Koltuk", "Tarih"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        JButton deleteButton = new JButton("Seçili Rezervasyonu İptal Et");
        JButton refreshButton = new JButton("Listeyi Yenile");
        JButton backButton = new JButton("Ana Menüye Dön");

     

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String pnrCode = (String) tableModel.getValueAt(selectedRow, 0);
                
                int confirm = JOptionPane.showConfirmDialog(this, 
                    pnrCode + " kodlu rezervasyonu iptal etmek istediğinize emin misiniz?", 
                    "Onay", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    mainFrame.getReservationManager().cancelReservation(pnrCode);
                    refreshTable(); 
                    JOptionPane.showMessageDialog(this, "Rezervasyon iptal edildi.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen iptal etmek için bir satır seçin.");
            }
        });


        refreshButton.addActionListener(e -> refreshTable());

        backButton.addActionListener(e -> mainFrame.cardLayout.show(mainFrame.mainPanel, "SearchScreen"));

        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                System.out.println("Panel görünür oldu, tablo yenileniyor...");
                refreshTable();
            }
        });
    }

    public void refreshTable() {
        System.out.println("--- refreshTable Metodu Başladı ---");
        

        tableModel.setRowCount(0);
        
  
        User currentUser = mainFrame.getCurrentUser();
        if (currentUser == null) {
            System.out.println("HATA: currentUser NULL görünüyor! Giriş yapılmamış olabilir.");
            return;
        }
        System.out.println("Giriş Yapan Kullanıcı: " + currentUser.getUsername());

        List<Reservation> reservations;

  
        if ("admin".equals(currentUser.getUsername())) {
             reservations = mainFrame.getReservationManager().getAllReservations();
             System.out.println("Mod: Admin (Tüm veriler çekildi)");
        } else {
             reservations = mainFrame.getReservationManager().getReservationsByUser(currentUser.getUsername());
             System.out.println("Mod: User (Sadece kullanıcı verileri çekildi)");
        }

        if (reservations == null || reservations.isEmpty()) {
            System.out.println("UYARI: Rezervasyon listesi BOŞ döndü (Size: 0)");
            return;
        } else {
            System.out.println("Listelenen Rezervasyon Sayısı: " + reservations.size());
        }


        for (Reservation res : reservations) {

             System.out.println("Bulunan Rezervasyon -> PNR: " + res.getReservationCode() + " | Active: " + res.isActive());
             
             if (res.isActive()) {
                 tableModel.addRow(new Object[]{
                     res.getReservationCode(), 
                     res.getFlight().getFlightNum(),
                     res.getPassengerName(),
                     res.getSeat().getSeatNum(),
                     res.getDateOfReservation()
                 });
             }
         }
         System.out.println("Tablo güncelleme tamamlandı.");
    }
}