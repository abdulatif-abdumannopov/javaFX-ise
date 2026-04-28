package com.example.familytasks.models;

import java.util.UUID;

public class User {
    private int id;
    private String name;
    private String password; 
    private Role role;
    private int balance;
    private String sessionToken; 

    public User(int id, String name, String password, Role role) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
        this.balance = 0;
    }

    
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public int getBalance() { return balance; }
    public String getSessionToken() { return sessionToken; }

    
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
    public void setBalance(int balance) { this.balance = balance; }
    public void addBalance(int amount) { this.balance += amount; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }
}