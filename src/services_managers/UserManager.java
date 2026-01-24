package services_managers;

import flight_management.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private List<User> users;
    private final String FILE_NAME = "users.txt";

    public UserManager() {
        users = new ArrayList<>();
        loadUsersFromFile();
    }


    public boolean registerUser(String username, String password, String realName, String role) {
    
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return false; 
            }
        }

        User newUser = new User(username, password, realName, role);
        users.add(newUser);
        saveUsersToFile(); 
        return true;
    }


    public User authenticate(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u; 
            }
        }
        return null;
    }

    
    private void saveUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (User u : users) {
                writer.write(u.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Dosya yazma hatası: " + e.getMessage());
        }
    }


    private void loadUsersFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    users.add(new User(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            System.out.println("Dosya okuma hatası: " + e.getMessage());
        }
    }
}