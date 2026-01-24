package gui;

import javax.swing.*;
import java.awt.*;
import flight_management.User;

public class LoginPanel extends JPanel {

    private MainFrame mainFrame;
    private JTextField userField;
    private JPasswordField passField;
    private JLabel messageLabel;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(null);
        setBackground(new Color(240, 248, 255));

        JLabel titleLabel = new JLabel("Uçuş Yönetim Sistemi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(300, 50, 400, 40);
        titleLabel.setForeground(new Color(25, 25, 112));
        add(titleLabel);

 
        JLabel userLabel = new JLabel("Kullanıcı Adı:");
        userLabel.setBounds(280, 150, 100, 25);
        add(userLabel);
        userField = new JTextField(20);
        userField.setBounds(380, 150, 150, 25);
        add(userField);


        JLabel passLabel = new JLabel("Şifre:");
        passLabel.setBounds(280, 200, 100, 25);
        add(passLabel);
        passField = new JPasswordField(20);
        passField.setBounds(380, 200, 150, 25);
        add(passField);

        JButton loginButton = new JButton("GİRİŞ YAP");
        loginButton.setBounds(280, 260, 120, 35);
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.black);
        add(loginButton);

        JButton registerButton = new JButton("KAYIT OL");
        registerButton.setBounds(410, 260, 120, 35);
        registerButton.setBackground(new Color(255, 140, 0)); // Turuncu
        registerButton.setForeground(Color.black);
        add(registerButton);

        messageLabel = new JLabel("");
        messageLabel.setBounds(300, 320, 300, 25);
        messageLabel.setForeground(Color.RED);
        add(messageLabel);


        loginButton.addActionListener(e -> doLogin());
        
        registerButton.addActionListener(e -> {
  
            new RegisterDialog(mainFrame).setVisible(true);
        });
    }

    private void doLogin() {
        System.out.println("--- LOGIN İŞLEMİ BAŞLADI ---");
        
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        System.out.println("DEBUG 1: Girilen Kullanıcı Adı: " + username);
        System.out.println("DEBUG 1: Girilen Şifre: " + password);

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Lütfen alanları doldurunuz.");
            return;
        }


        if (mainFrame.getUserManager() == null) {
            System.out.println("KRİTİK HATA: MainFrame içindeki UserManager NULL! Veri çekilemiyor.");
            JOptionPane.showMessageDialog(this, "Sistem hatası: UserManager başlatılmamış.");
            return;
        }


        User user = mainFrame.getUserManager().authenticate(username, password);


        System.out.println("DEBUG 2: Sorgu Sonucu (User objesi): " + user);

        if (user != null) {
            System.out.println("DEBUG 3: Kullanıcı bulundu, MainFrame'e gönderiliyor...");
            
          
            mainFrame.setCurrentUser(user);
            
    
            if (mainFrame.getCurrentUser() != null) {
                System.out.println("DEBUG 4: BAŞARILI! MainFrame.currentUser artık: " + mainFrame.getCurrentUser().getUsername());
            } else {
                System.out.println("DEBUG 4: HATA! MainFrame'e set etmeye çalıştık ama hala NULL.");
            }

            JOptionPane.showMessageDialog(this, "Giriş Başarılı: " + user.getUsername());
            

            if (user.getRole() != null && user.getRole().equalsIgnoreCase("ADMIN")) {
                mainFrame.cardLayout.show(mainFrame.mainPanel, "AdminScreen");
            } else {
                mainFrame.cardLayout.show(mainFrame.mainPanel, "SearchScreen");
            }
            
        } else {
            System.out.println("DEBUG 3: Kullanıcı BULUNAMADI (authenticate null döndü).");
            messageLabel.setText("Hatalı kullanıcı adı veya şifre!");
        }
        System.out.println("--- LOGIN İŞLEMİ BİTTİ ---");
    }
}