package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import flight_management.*;
import services_managers.*;

public class AdminPanel extends JPanel {

    private MainFrame mainFrame;


    private final Color COL_DARK_BG = new Color(44, 62, 80);
    private final Color COL_SIDE_BG = new Color(236, 240, 241);
    private final Color COL_ACCENT  = new Color(52, 152, 219);
    private final Color COL_SUCCESS = new Color(39, 174, 96);
    private final Color COL_DANGER  = new Color(192, 57, 43);
    private final Color COL_WARNING = new Color(243, 156, 18);
    
    private final Font  FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 20);
    private final Font  FONT_STD    = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font  FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 14);

 
    private JTabbedPane flightSubTabs; 
    private JTable flightTable;
    private DefaultTableModel flightModel;
    
   
    private JTextField addFNum, addDuration, addDate, addTime;
    private JTextField addFrom, addTo; 

    private JTextField editFNum, editDuration, editDate, editTime;
    private JTextField editFrom, editTo; 
  
    
    private JPanel editPanel; 

    private JTable staffTable;
    private DefaultTableModel staffModel;
    private JTextField staffNameField;
    private JComboBox<String> staffRoleCombo;

    public AdminPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(COL_SIDE_BG);


        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COL_DARK_BG);
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("YÖNETİCİ KONTROL PANELİ");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);

        JButton btnLogout = createStyledButton("Çıkış Yap", COL_DANGER, Color.WHITE);
        btnLogout.addActionListener(e -> mainFrame.cardLayout.show(mainFrame.mainPanel, "LoginScreen"));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);


        JTabbedPane mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setFont(FONT_BOLD);
        mainTabbedPane.setBackground(Color.WHITE);
        
        mainTabbedPane.addTab(" Uçuş Yönetimi ", null, createFlightSection(), "Uçuşları Listele, Ekle ve Düzenle");
        mainTabbedPane.addTab(" Personel Yönetimi ", null, createStaffSection(), "Personel Kadrosunu Yönet");
        mainTabbedPane.addTab(" Sistem Simülasyonu ", null, createSimulationSection(), "Thread ve Senaryo Testleri");

        add(mainTabbedPane, BorderLayout.CENTER);
    }

    private JPanel createFlightSection() {
        JPanel container = new JPanel(new BorderLayout());
        
        flightSubTabs = new JTabbedPane(JTabbedPane.LEFT);
        flightSubTabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel addPanel = createFlightForm(true); 
        this.editPanel = createFlightForm(false); 

        JPanel listPanel = new JPanel(new BorderLayout());
        String[] columns = {"Uçuş No", "Nereden", "Nereye", "Tarih", "Saat", "Süre"};
        flightModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        flightTable = new JTable(flightModel);
        styleTable(flightTable);
        
        JPanel listBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGoEdit = createStyledButton("Seçileni Düzenle", COL_WARNING, Color.WHITE);
        JButton btnDelete = createStyledButton("Seçileni Sil", COL_DANGER, Color.WHITE);
        JButton btnRefresh = createStyledButton("Listeyi Yenile", COL_ACCENT, Color.WHITE);
        
        listBtnPanel.add(btnRefresh);
        listBtnPanel.add(btnDelete);
        listBtnPanel.add(btnGoEdit);
        
        listPanel.add(new JScrollPane(flightTable), BorderLayout.CENTER);
        listPanel.add(listBtnPanel, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> refreshFlightTable());
        
        btnDelete.addActionListener(e -> {
            try {
                int row = flightTable.getSelectedRow();
                if (row == -1) { JOptionPane.showMessageDialog(this, "Lütfen silinecek uçuşu seçin."); return; }
                String fNum = (String) flightModel.getValueAt(row, 0);
                if (JOptionPane.showConfirmDialog(this, fNum + " silinsin mi?", "Onay", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    mainFrame.getFlightManager().deleteFlight(fNum);
                    refreshFlightTable();
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage()); }
        });

        btnGoEdit.addActionListener(e -> {
            int row = flightTable.getSelectedRow();
            if (row == -1) { 
                JOptionPane.showMessageDialog(this, "Düzenlemek için listeden bir uçuş seçiniz."); 
                return; 
            }
            
            loadFlightToEditForm(row); 
            
            if (flightSubTabs.indexOfComponent(editPanel) == -1) {
                flightSubTabs.addTab("Düzenle", editPanel);
            }
            flightSubTabs.setSelectedComponent(editPanel);
        });

        flightSubTabs.addTab("Uçuş Listesi", listPanel);
        flightSubTabs.addTab("Yeni Uçuş Ekle", addPanel);

        container.add(flightSubTabs, BorderLayout.CENTER);
        refreshFlightTable();
        return container;
    }

    private JPanel createFlightForm(boolean isAddMode) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNum = new JTextField(15);
        
        // --- DEĞİŞİKLİK 2: JTextField tanımlaması ---
        JTextField txtFrom = new JTextField(15);
        JTextField txtTo = new JTextField(15); 
        // --------------------------------------------
        
        JTextField txtDate = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 10); txtDate.setEditable(false);
        JTextField txtTime = new JTextField("10:00", 10); txtTime.setEditable(false);
        JTextField txtDur = new JTextField("1h", 10);
        
        if (isAddMode) {
            addFNum = txtNum; addFrom = txtFrom; addTo = txtTo; 
            addDate = txtDate; addTime = txtTime; addDuration = txtDur;
        } else {
            editFNum = txtNum; editFrom = txtFrom; editTo = txtTo; 
            editDate = txtDate; editTime = txtTime; editDuration = txtDur;
            txtNum.setEditable(false);
            txtNum.setBackground(new Color(240,240,240));
        }

        JLabel lblHeader = new JLabel(isAddMode ? "YENİ UÇUŞ OLUŞTUR" : "UÇUŞ BİLGİLERİNİ GÜNCELLE");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(COL_DARK_BG);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(lblHeader, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        gbc.gridx = 0; panel.add(new JLabel("Uçuş No:"), gbc);
        gbc.gridx = 1; panel.add(txtNum, gbc);
        
        gbc.gridx = 2; panel.add(new JLabel("Tarih:"), gbc);
        JPanel pnlDate = new JPanel(new BorderLayout()); 
        JButton btnDate = new JButton("Seç");
        btnDate.addActionListener(e -> new TinyCalendar(mainFrame, txtDate).setVisible(true));
        pnlDate.add(txtDate, BorderLayout.CENTER); pnlDate.add(btnDate, BorderLayout.EAST);
        gbc.gridx = 3; panel.add(pnlDate, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0; panel.add(new JLabel("Nereden:"), gbc);
        gbc.gridx = 1; panel.add(txtFrom, gbc);
        
        gbc.gridx = 2; panel.add(new JLabel("Saat:"), gbc);
        JPanel pnlTime = new JPanel(new BorderLayout());
        JButton btnTime = new JButton("Seç");
        btnTime.addActionListener(e -> new TinyTimePicker(mainFrame, txtTime).setVisible(true));
        pnlTime.add(txtTime, BorderLayout.CENTER); pnlTime.add(btnTime, BorderLayout.EAST);
        gbc.gridx = 3; panel.add(pnlTime, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0; panel.add(new JLabel("Nereye:"), gbc);
        gbc.gridx = 1; panel.add(txtTo, gbc);

        gbc.gridx = 2; panel.add(new JLabel("Süre:"), gbc);
        gbc.gridx = 3; panel.add(txtDur, gbc);

        JButton btnSave = createStyledButton(isAddMode ? "Uçuşu Ekle" : "Değişiklikleri Kaydet", isAddMode ? COL_SUCCESS : COL_WARNING, Color.WHITE);
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(btnSave, gbc);

        btnSave.addActionListener(e -> {
            if (isAddMode) executeAddFlight();
            else executeUpdateFlight();
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(panel, BorderLayout.NORTH);
        return wrapper;
    }

  
    private JPanel createStaffSection() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Personel Ekle");
        lblTitle.setFont(FONT_BOLD);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        staffNameField = new JTextField();
        staffNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        String[] roles = {"Pilot", "Kabin Memuru", "Teknisyen", "Yer Hizmetleri", "Operasyon", "Doktor", "Güvenlik"};
        staffRoleCombo = new JComboBox<>(roles);
        staffRoleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JButton btnAdd = createStyledButton("Kaydet", COL_SUCCESS, Color.WHITE);
        btnAdd.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(lblTitle);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(new JLabel("Ad Soyad:"));
        formPanel.add(staffNameField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(new JLabel("Pozisyon:"));
        formPanel.add(staffRoleCombo);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(btnAdd);
        formPanel.add(Box.createVerticalGlue());

        String[] columns = {"ID", "Ad Soyad", "Pozisyon"};
        staffModel = new DefaultTableModel(columns, 0) { public boolean isCellEditable(int r, int c) { return false; }};
        staffTable = new JTable(staffModel);
        styleTable(staffTable);
        
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(new JScrollPane(staffTable), BorderLayout.CENTER);
        JButton btnDelete = createStyledButton("Seçili Personeli Sil", COL_DANGER, Color.WHITE);
        listPanel.add(btnDelete, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, listPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.0);

        btnAdd.addActionListener(e -> {
            try {
                String name = staffNameField.getText();
                String role = (String) staffRoleCombo.getSelectedItem();
                
                if (role == null) {
                    JOptionPane.showMessageDialog(this, "Lütfen pozisyon seçiniz.");
                    return;
                }

                if(!name.isEmpty()) {
                    if (mainFrame.getStaffManager() != null) {
                        mainFrame.getStaffManager().addStaff(name, role);
                        refreshStaffTable();
                        staffNameField.setText("");
                        JOptionPane.showMessageDialog(this, "Personel eklendi!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "İsim boş olamaz.");
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage()); }
        });

        btnDelete.addActionListener(e -> {
            try {
                int row = staffTable.getSelectedRow();
                if(row != -1) {
                    int id = Integer.parseInt(staffModel.getValueAt(row, 0).toString());
                    if (mainFrame.getStaffManager() != null) {
                        mainFrame.getStaffManager().removeStaff(id);
                        refreshStaffTable();
                        JOptionPane.showMessageDialog(this, "Personel silindi.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Silinecek personeli seçiniz.");
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage()); }
        });

        if (mainFrame.getStaffManager() != null) {
             refreshStaffTable();
        }
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(splitPane, BorderLayout.CENTER);
        return wrapper;
    }

   
    private JPanel createSimulationSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel title = new JLabel("Sistem Simülasyon Merkezi");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(COL_DARK_BG);

        JLabel desc = new JLabel("<html><center>Multithreading ve Asenkron işlem testlerini buradan başlatabilirsiniz.<br>" +
                                 "Uçak koltuk doluluk testleri ve raporlama simülasyonları.</center></html>");
        desc.setFont(FONT_STD);

        JButton btnOpenSim = createStyledButton("Simülasyon Ekranını Aç >>", new Color(142, 68, 173), Color.WHITE);
        btnOpenSim.setPreferredSize(new Dimension(250, 50));
        btnOpenSim.setFont(new Font("Segoe UI", Font.BOLD, 16));

        btnOpenSim.addActionListener(e -> mainFrame.cardLayout.show(mainFrame.mainPanel, "SimulationScreen"));

        gbc.gridx = 0; gbc.gridy = 0; 
        panel.add(title, gbc);
        gbc.gridy = 1; panel.add(desc, gbc);
        gbc.gridy = 2; panel.add(btnOpenSim, gbc);

        return panel;
    }



    private void loadFlightToEditForm(int row) {
        editFNum.setText(flightModel.getValueAt(row, 0).toString());
        editFrom.setText(flightModel.getValueAt(row, 1).toString());
        editTo.setText(flightModel.getValueAt(row, 2).toString());
        editDate.setText(flightModel.getValueAt(row, 3).toString());
        editTime.setText(flightModel.getValueAt(row, 4).toString());
        editDuration.setText(flightModel.getValueAt(row, 5).toString());
    }
    private void executeAddFlight() {
        try {
            // 1. Önce formdaki verileri alalım
            String num = addFNum.getText().trim();
            String from = addFrom.getText().trim();
            String to = addTo.getText().trim();
            String dateStr = addDate.getText().trim(); // Kullanıcının elle girdiği tarih

            // --- EN KRİTİK KONTROL BURASI ---
            java.time.LocalDate today = java.time.LocalDate.now(); // Bugünün tarihi (24 Ocak 2026)
            java.time.LocalDate flightDate;

            try {
                flightDate = java.time.LocalDate.parse(dateStr); // Tarihi ayrıştır (YYYY-MM-DD)
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Hata: Tarih formatı YYYY-MM-DD olmalı!");
                return; // Format bozuksa burada dur, ekleme yapma!
            }

            // EĞER SEÇİLEN TARİH BUGÜNDEN ÖNCEYSE
            if (flightDate.isBefore(today)) {
                JOptionPane.showMessageDialog(this, 
                    "HATA: Geçmiş bir tarihe uçuş ekleyemezsiniz!\n" +
                    "Seçilen Tarih: " + flightDate + "\n" +
                    "Bugünün Tarihi: " + today, 
                    "Geçersiz Tarih", 
                    JOptionPane.ERROR_MESSAGE);
                
                return; // <--- AGA BURASI ŞART! Eğer bu satırı koymazsan mesajı gösterir ama aşağıda eklemeye devam eder.
            }
            // --------------------------------

            // Eğer kod buraya kadar geldiyse tarih GEÇERLİDİR. Kayıt işlemlerine geçebiliriz:
            Plane plane = new Plane("PLN-" + num, "Boeing 737", 180);
            Flight f = new Flight(num, from, to, dateStr, addTime.getText(), addDuration.getText(), plane);
            
            // FlightManager üzerinden kaydet
            mainFrame.getFlightManager().createFlight(f);
            
            // Tabloyu ve formu tazele
            refreshFlightTable();
            addFNum.setText(""); 
            addFrom.setText(""); 
            addTo.setText("");
            
            JOptionPane.showMessageDialog(this, "Uçuş Başarıyla Eklendi!");
            flightSubTabs.setSelectedIndex(0); 

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Sistem hatası: " + e.getMessage());
        }
    }
    private void executeUpdateFlight() {
        try {
            String num = editFNum.getText();
            Plane plane = new Plane("PLN-" + num, "Boeing 737", 180); 
            
   
            Flight f = new Flight(num, editFrom.getText(), editTo.getText(),
                                  editDate.getText(), editTime.getText(), editDuration.getText(), plane);
      
            
            mainFrame.getFlightManager().updateFlight(f);
            refreshFlightTable();
            
            JOptionPane.showMessageDialog(this, "Uçuş Güncellendi!");
            flightSubTabs.setSelectedIndex(0); 
            flightSubTabs.remove(editPanel);   
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Güncelleme Hatası: " + e.getMessage());
        }
    }

    public void refreshFlightTable() {
        try {
            flightModel.setRowCount(0);
            if (mainFrame.getFlightManager() != null) {
                java.util.List<Flight> flights = mainFrame.getFlightManager().getAllFlights();
                if(flights != null) {
                    for(Flight f : flights) {
                        flightModel.addRow(new Object[]{f.getFlightNum(), f.getRoute().getDeparturePlace(), f.getRoute().getArrivalPlace(),
                                                        f.getDate(), f.getHour(), f.getDuration()});
                    }
                }
            }
        } catch (Exception e) { System.err.println(e.getMessage()); }
    }

    private void refreshStaffTable() {
        try {
            staffModel.setRowCount(0);
            if (mainFrame.getStaffManager() != null) {
                java.util.List<Staff> staff = mainFrame.getStaffManager().getAllStaff();
                for(Staff s : staff) staffModel.addRow(new Object[]{s.getId(), s.getName(), s.getRole()});
            }
        } catch (Exception e) { System.err.println(e.getMessage()); }
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(230,230,230));
        table.setSelectionBackground(new Color(220, 230, 241));
        table.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245,245,245));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(COL_DARK_BG);
    }

    class TinyCalendar extends JDialog {
        private JTextField targetField;
        private java.time.LocalDate currentDate;
        private JPanel pnlDays;
        private JLabel lblMonth;

        public TinyCalendar(Frame owner, JTextField targetField) {
            super(owner, "Tarih Seç", true);
            this.targetField = targetField;
            this.currentDate = java.time.LocalDate.now();
            setSize(350, 350); 
            setLocationRelativeTo(targetField); setLayout(new BorderLayout());

            JPanel header = new JPanel(new BorderLayout()); header.setBackground(COL_ACCENT);
            JButton btnPrev = new JButton("<"); JButton btnNext = new JButton(">");
            lblMonth = new JLabel("", SwingConstants.CENTER); lblMonth.setForeground(Color.WHITE);
            lblMonth.setFont(new Font("Arial", Font.BOLD, 14));
            
            btnPrev.setContentAreaFilled(false); btnPrev.setBorderPainted(false); btnPrev.setForeground(Color.WHITE);
            btnNext.setContentAreaFilled(false); btnNext.setBorderPainted(false); btnNext.setForeground(Color.WHITE);

            btnPrev.addActionListener(e -> { currentDate = currentDate.minusMonths(1); updateCalendar(); });
            btnNext.addActionListener(e -> { currentDate = currentDate.plusMonths(1); updateCalendar(); });

            header.add(btnPrev, BorderLayout.WEST); header.add(lblMonth, BorderLayout.CENTER); header.add(btnNext, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);

            pnlDays = new JPanel(new GridLayout(0, 7, 2, 2)); pnlDays.setBackground(Color.WHITE);
            pnlDays.setBorder(new EmptyBorder(5,5,5,5));
            add(pnlDays, BorderLayout.CENTER); updateCalendar();
        }
        
        private void updateCalendar() {
            pnlDays.removeAll();
            lblMonth.setText(currentDate.getMonth() + " " + currentDate.getYear());
            int daysInMonth = currentDate.lengthOfMonth();
            int startDay = currentDate.withDayOfMonth(1).getDayOfWeek().getValue();
            for(int i=1; i<startDay; i++) pnlDays.add(new JLabel(""));
            
            for(int i=1; i<=daysInMonth; i++) {
                int d = i; 
                JButton b = new JButton(String.valueOf(d));
                b.setBackground(Color.WHITE); 
                b.setFocusPainted(false);
                b.setMargin(new Insets(1, 1, 1, 1)); 
                b.setFont(new Font("Segoe UI", Font.BOLD, 12)); 

                b.addActionListener(e -> { targetField.setText(currentDate.withDayOfMonth(d).toString()); dispose(); });
                pnlDays.add(b);
            }
            pnlDays.revalidate(); pnlDays.repaint();
        }
    }

    class TinyTimePicker extends JDialog {
        private String selectedHour = "12";
        private String selectedMinute = "00";
        
        public TinyTimePicker(Frame owner, JTextField targetField) {
            super(owner, "Saat ve Dakika Seç", true);
            setSize(600, 350); 
            setLocationRelativeTo(targetField);
            setLayout(new BorderLayout());

            JPanel mainContainer = new JPanel(new GridLayout(1, 2, 15, 0)); 
            mainContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

    
            JPanel pnlHour = new JPanel(new GridLayout(6, 4, 2, 2)); 
            pnlHour.setBorder(BorderFactory.createTitledBorder("Saat"));
            ButtonGroup grpHour = new ButtonGroup();

            for (int i = 0; i < 24; i++) {
                String val = String.format("%02d", i);
                JToggleButton btn = new JToggleButton(val);
                btn.setFocusPainted(false);
                btn.setBackground(Color.WHITE);
                
                if(targetField.getText().startsWith(val)) {
                    btn.setSelected(true);
                    selectedHour = val;
                }

                btn.addActionListener(e -> selectedHour = val);
                grpHour.add(btn);
                pnlHour.add(btn);
            }

            
            JPanel pnlMin = new JPanel(new GridLayout(4, 3, 2, 2));
            pnlMin.setBorder(BorderFactory.createTitledBorder("Dakika"));
            ButtonGroup grpMin = new ButtonGroup();

            int[] mins = {0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55};
            for (int m : mins) {
                String val = String.format("%02d", m);
                JToggleButton btn = new JToggleButton(val);
                btn.setFocusPainted(false);
                btn.setBackground(Color.WHITE);

                if(targetField.getText().endsWith(":" + val)) {
                    btn.setSelected(true);
                    selectedMinute = val;
                }

                btn.addActionListener(e -> selectedMinute = val);
                grpMin.add(btn);
                pnlMin.add(btn);
            }

            mainContainer.add(pnlHour);
            mainContainer.add(pnlMin);
            add(mainContainer, BorderLayout.CENTER);

            JButton btnOk = new JButton("ZAMANI AYARLA");
            btnOk.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnOk.setBackground(new Color(46, 204, 113));
            btnOk.setForeground(Color.WHITE);
            btnOk.addActionListener(e -> {
                targetField.setText(selectedHour + ":" + selectedMinute);
                dispose();
            });

            add(btnOk, BorderLayout.SOUTH);
        }
    }
}