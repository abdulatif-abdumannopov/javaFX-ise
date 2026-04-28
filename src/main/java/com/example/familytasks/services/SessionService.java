package com.example.familytasks.services;

import com.example.familytasks.models.User;
import com.google.gson.Gson;

import java.io.*;

public class SessionService {
    private static final String SESSION_FILE = "session.json";
    private static final Gson gson = new Gson();

    static class SessionData {
        int activeUserId;
    }

    // Сохранить текущего юзера
    public static void saveSession(User user) {
        SessionData session = new SessionData();
        session.activeUserId = user.getId();
        try (Writer writer = new FileWriter(SESSION_FILE)) {
            gson.toJson(session, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Загрузить юзера при старте приложения
    public static User getSavedSessionUser() {
        File file = new File(SESSION_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                SessionData session = gson.fromJson(reader, SessionData.class);
                return DataService.getUserById(session.activeUserId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Выйти из аккаунта (удалить сессию)
    public static void clearSession() {
        File file = new File(SESSION_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}