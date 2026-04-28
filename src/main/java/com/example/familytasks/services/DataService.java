package com.example.familytasks.services;

import com.example.familytasks.models.Role;
import com.example.familytasks.models.Task;
import com.example.familytasks.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataService {
    private static final String DATA_FILE = "data.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static class AppData {
        public List<User> users = new ArrayList<>();
        public List<Task> tasks = new ArrayList<>();
    }

    private static AppData currentData = new AppData();

    public static void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                currentData = gson.fromJson(reader, AppData.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String hashedDefault = BCrypt.hashpw("123", BCrypt.gensalt());
            currentData.users.add(new User(1, "Parent", hashedDefault, Role.PARENT));
            saveData();
        }
    }

    public static void saveData() {
        try (Writer writer = new FileWriter(DATA_FILE)) {
            gson.toJson(currentData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Безопасная аутентификация
    public static User authenticate(String name, String password) {
        User user = currentData.users.stream()
                .filter(u -> u.getName().equals(name))
                .findFirst()
                .orElse(null);

        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            // При входе генерируем новый токен
            user.setSessionToken(UUID.randomUUID().toString());
            saveData();
            return user;
        }
        return null;
    }

    // Проверка токена (чтобы нельзя было просто подменить ID в сессии)
    public static User getUserByToken(int id, String token) {
        User u = getUserById(id);
        if (u != null && token != null && token.equals(u.getSessionToken())) {
            return u;
        }
        return null;
    }

    public static List<User> getUsers() { return currentData.users; }
    public static List<Task> getTasks() { return currentData.tasks; }
    public static User getUserById(int id) {
        return currentData.users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }
}