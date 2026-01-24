package services_managers;

import flight_management.Staff;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StaffManager {
    private List<Staff> staffList;
    private final String FILE_NAME = "staff.txt";

    public StaffManager() {
        staffList = new ArrayList<>();
        loadStaffFromFile();
    }

   
    public void addStaff(String name, String role) {
        int newId = staffList.isEmpty() ? 1 : staffList.get(staffList.size() - 1).getId() + 1;
        Staff newStaff = new Staff(newId, name, role);
        staffList.add(newStaff);
        saveStaffToFile();
    }


    public void removeStaff(int id) {
        staffList.removeIf(s -> s.getId() == id);
        saveStaffToFile();
    }


    public List<Staff> getAllStaff() {
        return staffList;
    }

    private void saveStaffToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Staff s : staffList) {
                writer.write(s.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Hata: Personel dosyası kaydedilemedi: " + e.getMessage());
        }
    }

 
    private void loadStaffFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String role = parts[2];
                    staffList.add(new Staff(id, name, role));
                }
            }
        } catch (IOException e) {
            System.out.println("Hata: Personel dosyası okunamadı.");
        }
    }
}