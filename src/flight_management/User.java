package flight_management;

public class User {
    private String username;
    private String password;
    private String realName;
    private String role;

    public User(String username, String password, String realName, String role) {
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.role = role;
    }

    // Dosyaya yazılacak format (CSV: kadi,sifre,isim,rol)
    @Override
    public String toString() {
        return username + "," + password + "," + realName + "," + role;
    }

    // Getter Metotları
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRealName() { return realName; }
    public String getRole() { return role; }
}