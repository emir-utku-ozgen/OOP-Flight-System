package gui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import flight_management.Flight;
import flight_management.Seat;
import services_managers.ReportGenerator;

public class SimulationPanel extends JPanel {
    private MainFrame mainFrame;
    private JPanel seatGridPanel;
    private Map<String, JButton> seatButtons;
    private JLabel statusLabel;
    private JCheckBox syncCheckBox;
    private JTextArea reportArea;
    private JProgressBar progressBar;

    
    private final Color COLOR_BG = new Color(236, 240, 241);
    private final Color COLOR_HEADER = new Color(44, 62, 80);
    private final Color COLOR_ACCENT = new Color(52, 152, 219);
    
   
    private final Color COLOR_TAKEN = new Color(39, 174, 96);
    private final Color COLOR_EMPTY = new Color(189, 195, 199);
    
   
    private final Font MAIN_FONT = getSystemOptimizedFont(Font.BOLD, 14);
    private final Font SMALL_FONT = getSystemOptimizedFont(Font.PLAIN, 12);

    public SimulationPanel(MainFrame mainFrame) {
        System.out.println("SimulationPanel oluşturuluyor..."); 
        
        this.mainFrame = mainFrame;
        this.seatButtons = new HashMap<>();
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        controlPanel.setBackground(Color.white);
        controlPanel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                new EmptyBorder(5, 5, 5, 5)
        ));
        
        JButton backButton = createModernButton("<< Geri", new Color(149, 165, 166), Color.WHITE);
        backButton.addActionListener(e -> mainFrame.cardLayout.show(mainFrame.mainPanel, "AdminScreen"));
        
        JButton runSimButton = createModernButton("SİMÜLASYONU BAŞLAT >", COLOR_ACCENT, Color.black);
        
        syncCheckBox = new JCheckBox("Synchronized (Güvenli Mod)");
        syncCheckBox.setFont(MAIN_FONT);
        syncCheckBox.setForeground(COLOR_HEADER);
        syncCheckBox.setBackground(Color.WHITE);
        syncCheckBox.setFocusPainted(false);
       
        syncCheckBox.setOpaque(true); 
        
        controlPanel.add(backButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(syncCheckBox);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(runSimButton);
        
        add(controlPanel, BorderLayout.NORTH);


        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(COLOR_BG);
        centerWrapper.setBorder(new EmptyBorder(20, 50, 20, 50));

        JPanel planeBodyPanel = new JPanel(new BorderLayout());
        planeBodyPanel.setBackground(Color.WHITE);
        planeBodyPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(189, 195, 199), 4, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel mapTitle = new JLabel("CANLI KOLTUK DURUMU", SwingConstants.CENTER);
        mapTitle.setFont(getSystemOptimizedFont(Font.BOLD, 16));
        mapTitle.setForeground(COLOR_HEADER);
        mapTitle.setBorder(new EmptyBorder(0,0,15,0));
        planeBodyPanel.add(mapTitle, BorderLayout.NORTH);

        seatGridPanel = new JPanel();
        seatGridPanel.setLayout(new BoxLayout(seatGridPanel, BoxLayout.Y_AXIS));
        seatGridPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(seatGridPanel);
        scrollPane.setBorder(null);
       
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        planeBodyPanel.add(scrollPane, BorderLayout.CENTER);
        centerWrapper.add(planeBodyPanel, BorderLayout.CENTER);
        
        add(centerWrapper, BorderLayout.CENTER);


        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(new EmptyBorder(15, 30, 15, 30));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                new EmptyBorder(15, 30, 15, 30)
        ));
        
        progressBar = new JProgressBar(0, 90);
        progressBar.setStringPainted(true);
        progressBar.setString("Sistem Bekleniyor...");
        progressBar.setFont(SMALL_FONT);
        progressBar.setForeground(COLOR_ACCENT);
        progressBar.setPreferredSize(new Dimension(100, 30));

        statusLabel = new JLabel("Durum: Hazır", SwingConstants.LEFT);
        statusLabel.setFont(MAIN_FONT);
        statusLabel.setForeground(COLOR_HEADER);
        
        reportArea = new JTextArea(5, 40);
        reportArea.setEditable(false);

        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 13)); 
        reportArea.setBackground(new Color(40, 44, 52));
        reportArea.setForeground(new Color(152, 195, 121));
        reportArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JButton genReportButton = createModernButton("ASENKRON RAPOR OLUŞTUR", new Color(46, 204, 113), Color.WHITE);
        
        JPanel statusPanel = new JPanel(new BorderLayout(10, 5));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(progressBar, BorderLayout.CENTER);

        bottomPanel.add(statusPanel, BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        bottomPanel.add(genReportButton, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);

      
        runSimButton.addActionListener(e -> startSimulation());
        genReportButton.addActionListener(e -> generateAsyncReport());
        
        initializeSeatMapUI(); 
    }

    
    private Font getSystemOptimizedFont(int style, int size) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return new Font("Segoe UI", style, size);
        } else if (os.contains("mac")) {
           
            return UIManager.getFont("Label.font").deriveFont(style, (float)size);
        } else {
            return new Font("SansSerif", style, size);
        }
    }

    private JButton createModernButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(getSystemOptimizedFont(Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        
        
        btn.setOpaque(true); 
        btn.setContentAreaFilled(true); 

        
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private void initializeSeatMapUI() {
        seatGridPanel.removeAll();
        seatButtons.clear();

        String[] leftCols = {"A", "B", "C"};
        String[] rightCols = {"D", "E", "F"};

        for (int i = 1; i <= 30; i++) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
            rowPanel.setBackground(Color.WHITE);
            
           
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); 
            

            for (String col : leftCols) rowPanel.add(createSeatButton(i, col));

            JLabel aisleLabel = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            aisleLabel.setPreferredSize(new Dimension(40, 30));
            aisleLabel.setFont(getSystemOptimizedFont(Font.BOLD, 14));
            aisleLabel.setForeground(new Color(149, 165, 166));
            rowPanel.add(aisleLabel);

            for (String col : rightCols) rowPanel.add(createSeatButton(i, col));

            seatGridPanel.add(rowPanel);
        }
        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    private JButton createSeatButton(int row, String col) {
        JButton btn = new JButton(col);
        btn.setPreferredSize(new Dimension(50, 35));
        btn.setFont(getSystemOptimizedFont(Font.PLAIN, 11));
        btn.setBackground(COLOR_EMPTY); 
        btn.setForeground(Color.DARK_GRAY);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(Color.WHITE, 1));
        

        btn.setOpaque(true); 
        
        String seatNum = row + col;
        seatButtons.put(seatNum, btn);
        return btn;
    }

    private void startSimulation() {
        boolean isSync = syncCheckBox.isSelected();

        flight_management.Plane tempPlane = new flight_management.Plane("SIM-PLANE", "Boeing 737", 180);
        Flight testFlight = new Flight("SIM-TEST", "Sanal Kalkış", "Sanal Varış", 
                                       java.time.LocalDate.now().toString(), "00:00", "2h", tempPlane);

        updateSeatMapUI(testFlight);
        
        statusLabel.setText("Simülasyon çalışıyor... (Mod: " + (isSync ? "SENKRON" : "ASENKRON") + ")");
        statusLabel.setForeground(COLOR_ACCENT);
        progressBar.setValue(0);
        progressBar.setString("Yolcular yerleşiyor...");

        new Thread(() -> {
            Thread[] threads = new Thread[90];
            for (int i = 0; i < 90; i++) {
                threads[i] = new Thread(() -> {
                   
                    mainFrame.getReservationManager().makeSimulationReservation(testFlight, isSync);
                    
                    SwingUtilities.invokeLater(() -> progressBar.setValue(progressBar.getValue() + 1));
                    
                    
                    SwingUtilities.invokeLater(() -> updateSeatMapUI(testFlight));
                });
                threads[i].start();
            }

            for (Thread t : threads) {
                try { t.join(); } catch (InterruptedException ex) { ex.printStackTrace(); }
            }

            SwingUtilities.invokeLater(() -> {
                updateSeatMapUI(testFlight);
                int count = calculateOccupiedSeats(testFlight);
                
                progressBar.setValue(90);
                progressBar.setString("Simülasyon Tamamlandı");
                
                statusLabel.setText("SONUÇ: " + count + "/90 koltuk doldu.");
                
                if (isSync && count == 90) {
                    statusLabel.setForeground(new Color(46, 204, 113)); 
                    JOptionPane.showMessageDialog(this, "Başarılı! Tüm koltuklar güvenle satıldı.");
                } else if (!isSync && count < 90) {
                    statusLabel.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(this, "HATA: Race Condition! Bazı veriler kayboldu.");
                }
            });
            
        }).start();
    }

    private void generateAsyncReport() {
        reportArea.setText(""); 
        statusLabel.setText("Rapor hazırlanıyor...");
        statusLabel.setForeground(COLOR_ACCENT);
        
        ReportGenerator task = new ReportGenerator(mainFrame.getFlightManager(), reportArea, statusLabel);
        Thread workerThread = new Thread(task);
        workerThread.start();
    }

    private void updateSeatMapUI(Flight f) {
        for (int row = 1; row <= 30; row++) {
            for (String col : new String[]{"A", "B", "C", "D", "E", "F"}) {
                String seatNum = row + col;
                JButton btn = seatButtons.get(seatNum);
                
                if (btn != null) {
                    try {
                        Seat s = mainFrame.getSeatManager().findSeatByNumber(f.getPlane(), seatNum);
                        if (s != null && s.isReserveStatus()) {
                            btn.setBackground(COLOR_TAKEN); 
                            btn.setForeground(Color.WHITE);
                            btn.setText(col);
                        } else {
                            btn.setBackground(COLOR_EMPTY); 
                            btn.setForeground(Color.DARK_GRAY);
                            btn.setText(col);
                        }
                    } catch (Exception e) { }
                }
            }
        }
    }

    private int calculateOccupiedSeats(Flight f) {
        int count = 0;
        String[] cols = {"A", "B", "C", "D", "E", "F"};
        for (int r = 1; r <= 30; r++) {
            for (String c : cols) {
                try {
                    Seat s = mainFrame.getSeatManager().findSeatByNumber(f.getPlane(), r + c);
                    if (s != null && s.isReserveStatus()) count++;
                } catch (Exception e) {}
            }
        }
        return count;
    }
}