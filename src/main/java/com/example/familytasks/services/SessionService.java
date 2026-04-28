package com.example.familytasks.services;

import com.example.familytasks.models.User;
import com.google.gson.Gson;
import java.io.*;

public class SessionService {
    private static final String SESSION_FILE = "session.json";
    private static final Gson gson = new Gson();

    
    static class SessionData {
        int activeUserId;
        String token; 
    }

    public static void saveSession(User user) {
        SessionData session = new SessionData();
        session.activeUserId = user.getId();
        session.token = user.getSessionToken(); 

        try (Writer writer = new FileWriter(SESSION_FILE)) {
            gson.toJson(session, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User getSavedSessionUser() {
        File file = new File(SESSION_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                SessionData session = gson.fromJson(reader, SessionData.class);
                
                return DataService.getUserByToken(session.activeUserId, session.token);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void clearSession() {
        File file = new File(SESSION_FILE);
        if (file.exists()) file.delete();
    }
}