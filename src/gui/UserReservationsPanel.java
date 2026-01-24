package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import reservation_ticketing.Reservation;
import services_managers.ReservationManager;

public class UserReservationsPanel extends JPanel {

    private MainFrame mainFrame;
    private JTable resTable;
    private DefaultTableModel resModel;
    private String searchName; 

    public UserReservationsPanel(MainFrame mainFrame, String searchName) {
        this.mainFrame = mainFrame;
        this.searchName = searchName;

        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        
        JLabel titleLabel = new JLabel("Rezervasyon Listesi: " + searchName.toUpperCase(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

       
        String[] columns = {"PNR", "Uçuş No", "Yolcu Adı", "Koltuk", "Tarih", "Tutar"};
        resModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        resTable = new JTable(resModel);
        resTable.setRowHeight(30);
        resTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        resTable.getTableHeader().setBackground(new Color(52, 73, 94));
        resTable.getTableHeader().setForeground(Color.WHITE);
        
        add(new JScrollPane(resTable), BorderLayout.CENTER);


        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(236, 240, 241));
        
        JButton btnRefresh = new JButton("Yenile");
        JButton btnCancel = new JButton("Seçili Rezervasyonu İptal Et");
        JButton btnBack = new JButton("Ana Menüye Dön");

        btnPanel.add(btnBack);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        
        btnRefresh.addActionListener(e -> refreshTable());

     
        btnBack.addActionListener(e -> {
           
            mainFrame.cardLayout.show(mainFrame.mainPanel, "SearchScreen"); 
        });
        

        btnCancel.addActionListener(e -> {
            int row = resTable.getSelectedRow();
            if (row != -1) {
                String pnr = (String) resModel.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this, pnr + " iptal edilsin mi?", "Onay", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    mainFrame.getReservationManager().cancelReservation(pnr);
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Rezervasyon başarıyla iptal edildi.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen listeden bir bilet seçin.");
            }
        });


        refreshTable();
    }

    private void refreshTable() {
        resModel.setRowCount(0); 
        ReservationManager rm = mainFrame.getReservationManager();

        if (rm != null) {
        
            List<Reservation> myReservations = rm.getReservationsByName(searchName);

            for (Reservation r : myReservations) {
                if (r.isActive()) {
                    Object[] row = {
                        r.getReservationCode(),
                        r.getFlight().getFlightNum(),
                        r.getPassenger().getName(), 
                        r.getSeat().getSeatNum(),
                        r.getDateOfReservation(),
                        String.format("%.2f TL", r.getTotalPrice())
                    };
                    resModel.addRow(row);
                }
            }
        }
    }
}