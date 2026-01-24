package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import flight_management.*;
import services_managers.*;

public class MainFrame extends JFrame {

    public CardLayout cardLayout;
    public JPanel mainPanel;

    private FlightManager flightManager;
    private SeatManager seatManager;
    private StaffManager staffManager;
    private ReservationManagementPanel reservationManagementPanel;
    private SimulationPanel simulationPanel;

    private ReservationManager reservationManager; 
    private UserManager userManager;
    private User currentUser;


    public MainFrame() {
   
        flightManager = new FlightManager();
        seatManager = new SeatManager();
        staffManager = new StaffManager();

        

        reservationManager = new ReservationManager(flightManager); 

   
        userManager = new UserManager();
        

        initTestData();

        setTitle("YTÜ Uçuş Yönetim Sistemi");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Sistem kapatılıyor, veriler kaydediliyor.....");
                 reservationManager.saveReservationsToFile(); 
            }
        });

   
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        

        reservationManagementPanel = new ReservationManagementPanel(this);
        simulationPanel = new SimulationPanel(this); 
  

        mainPanel.add(new LoginPanel(this), "LoginScreen");
        mainPanel.add(new SearchPanel(this), "SearchScreen");
        mainPanel.add(new AdminPanel(this), "AdminScreen");
        mainPanel.add(reservationManagementPanel, "ResMgmtScreen");
        mainPanel.add(simulationPanel, "SimulationScreen"); 

        add(mainPanel);
        cardLayout.show(mainPanel, "LoginScreen");
    }

    private void initTestData() {
        try {
            if (flightManager.getAllFlights().isEmpty()) {
                Plane plane = new Plane("TK-001", "Boeing 737", 180);
                Flight flight = new Flight("TK-1923", "Istanbul", "Ankara", "2025-06-01", "10:00", "1h", plane);
                flightManager.createFlight(flight);
            }
        } catch (Exception e) {
            System.out.println("Test verisi eklenirken (normal) uyarı: " + e.getMessage());
        }
    }
    
 
    public FlightManager getFlightManager() { return flightManager; }
    public SeatManager getSeatManager() { return seatManager; }
    public StaffManager getStaffManager() { return staffManager; }
    public ReservationManager getReservationManager() { return reservationManager; }
    public ReservationManagementPanel getReservationManagementPanel() { return reservationManagementPanel; }
    public UserManager getUserManager() { return userManager; }
    public User getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(User user) {
        this.currentUser = user;

        if (user != null) {
            System.out.println("Giriş Başarılı! Aktif Kullanıcı: " + user.getUsername());
        }
    }


	public static void main(String[] args) {
       
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }
        

        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}