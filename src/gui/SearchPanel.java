package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import flight_management.*; 

public class SearchPanel extends JPanel {

    private MainFrame mainFrame;
    private JTable flightTable;
    private DefaultTableModel tableModel;
    

    private JTextField fromField;
    private JTextField toField;
    // ---------------------------------------------

    public SearchPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());


        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topPanel.setBackground(new Color(230, 230, 250));


        topPanel.add(new JLabel("Nereden:"));
        fromField = new JTextField(12); 
        fromField.setToolTipText("Şehir giriniz (Örn: Ordu)");
        topPanel.add(fromField);


        topPanel.add(new JLabel("Nereye:"));
        toField = new JTextField(12);
        toField.setToolTipText("Şehir giriniz (Örn: Malatya)");
        topPanel.add(toField);

        JButton searchButton = new JButton("UÇUŞ ARA");
        searchButton.setBackground(new Color(0, 153, 76)); 
        searchButton.setForeground(Color.black);
        topPanel.add(searchButton);
        
 
        JButton clearButton = new JButton("Tümünü Göster");
        clearButton.setBackground(new Color(100, 149, 237));
        clearButton.setForeground(Color.WHITE);
        topPanel.add(clearButton);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Uçuş No", "Nereden", "Nereye", "Tarih", "Saat", "Uçak Tipi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        flightTable = new JTable(tableModel);
        flightTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(flightTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        
        JButton selectSeatButton = new JButton("Koltuk Seç ve İlerle >");
        selectSeatButton.setFont(new Font("Arial", Font.BOLD, 14));
        bottomPanel.add(selectSeatButton);
        
     
        JButton manageResButton = new JButton("Rezervasyonlarımı Yönet");
        manageResButton.setBackground(new Color(70, 130, 180)); 
        manageResButton.setForeground(Color.black);
        bottomPanel.add(manageResButton);
        
        JButton logoutButton = new JButton("Çıkış Yap");
        logoutButton.setForeground(Color.RED);
        bottomPanel.add(logoutButton);

        add(bottomPanel, BorderLayout.SOUTH);

        
        searchButton.addActionListener(e -> searchFlights());
        
   
        clearButton.addActionListener(e -> {
            fromField.setText("");
            toField.setText("");
            listAllFlights();
        });
        
        selectSeatButton.addActionListener(e -> openSeatSelection());
        
        manageResButton.addActionListener(e -> {

            if(mainFrame.getReservationManagementPanel() != null) {
                mainFrame.getReservationManagementPanel().refreshTable();
            }
  
            mainFrame.cardLayout.show(mainFrame.mainPanel, "ResMgmtScreen");
        });
        
        logoutButton.addActionListener(e -> {
          
            mainFrame.setCurrentUser(null);
            mainFrame.cardLayout.show(mainFrame.mainPanel, "LoginScreen");
        });
        
        listAllFlights();
    }

    private void searchFlights() {
     
        String from = fromField.getText().trim();
        String to = toField.getText().trim();


        tableModel.setRowCount(0);
        
    
        if (from.isEmpty() && to.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen bir şehir ismi giriniz.");
            return;
        }

        List<Flight> results = mainFrame.getFlightManager().searchFlights(from, to);

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aradığınız kriterde uçuş bulunamadı!");
        } else {
            for (Flight f : results) {
                tableModel.addRow(new Object[]{
                    f.getFlightNum(), 
                    f.getRoute().getDeparturePlace(), 
                    f.getRoute().getArrivalPlace(),   
                    f.getDate(), 
                    f.getHour(), 
                    f.getPlane().getPlaneModel()
                });
            }
        }
    }
    
    private void listAllFlights() {
        tableModel.setRowCount(0);
        List<Flight> allResults = mainFrame.getFlightManager().getAllFlights();
        if (allResults != null) {
            for (Flight f : allResults) {
                tableModel.addRow(new Object[]{
                    f.getFlightNum(),
                    f.getRoute().getDeparturePlace(),
                    f.getRoute().getArrivalPlace(),
                    f.getDate(),
                    f.getHour(),
                    f.getPlane().getPlaneModel()
                });
            }
        }
    }

    private void openSeatSelection() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen önce tablodan bir uçuş seçiniz!");
            return;
        }

        String flightNum = (String) tableModel.getValueAt(selectedRow, 0);
        Flight selectedFlight = mainFrame.getFlightManager().findFlightById(flightNum);

        if (selectedFlight != null) {
            SeatSelectionDialog dialog = new SeatSelectionDialog(mainFrame, selectedFlight);
            dialog.setVisible(true);
        }
    }
}