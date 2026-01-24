package gui;

import javax.swing.*;
import java.awt.*;

public class RegisterDialog extends JDialog {

    private MainFrame mainFrame;
    private JTextField userField, nameField;
    private JPasswordField passField;
    private JComboBox<String> roleCombo;

    public RegisterDialog(MainFrame mainFrame) {
        super(mainFrame, "Yeni Hesap Oluştur", true);
        this.mainFrame = mainFrame;
        setSize(350, 400);
        setLocationRelativeTo(mainFrame);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Ad Soyad:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Kullanıcı Adı:"));
        userField = new JTextField();
        panel.add(userField);

        panel.add(new JLabel("Şifre:"));
        passField = new JPasswordField();
        panel.add(passField);

        panel.add(new JLabel("Hesap Türü:"));
        String[] roles = {"Yolcu (Customer)", "Yönetici (Admin)"};
        roleCombo = new JComboBox<>(roles);
        panel.add(roleCombo);

        JButton btnRegister = new JButton("KAYIT OL");
        btnRegister.setBackground(new Color(46, 204, 113));
        btnRegister.setForeground(Color.WHITE);
        
        btnRegister.addActionListener(e -> doRegister());

        add(new JLabel("Kayıt Formu", SwingConstants.CENTER), BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(btnRegister, BorderLayout.SOUTH);
    }

    private void doRegister() {
        String name = nameField.getText();
        String user = userField.getText();
        String pass = new String(passField.getPassword());
        
       
        String selectedRoleRaw = (String) roleCombo.getSelectedItem();
        String roleCode = selectedRoleRaw.contains("Admin") ? "ADMIN" : "CUSTOMER";

        if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.");
            return;
        }

 
        boolean success = mainFrame.getUserManager().registerUser(user, pass, name, roleCode);

        if (success) {
            JOptionPane.showMessageDialog(this, "Kayıt Başarılı! Şimdi giriş yapabilirsiniz.");
            dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Hata: Bu kullanıcı adı zaten kullanılıyor.");
        }
    }
}