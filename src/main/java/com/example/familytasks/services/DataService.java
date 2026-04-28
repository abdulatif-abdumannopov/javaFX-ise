package com.example.familytasks.services;

import com.example.familytasks.models.Role;
import com.example.familytasks.models.Task;
import com.example.familytasks.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            // Если файла нет, создаем тестового родителя по умолчанию
            currentData.users.add(new User(1, "Мама", "123", Role.PARENT));
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

    public static List<User> getUsers() { return currentData.users; }
    public static List<Task> getTasks() { return currentData.tasks; }

    public static User authenticate(String name, String password) {
        return currentData.users.stream()
                .filter(u -> u.getName().equals(name) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public static User getUserById(int id) {
        return currentData.users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }
}
