package com.example.familytasks.models;

public class User {
    private int id;
    private String name;
    private String password;
    private Role role;
    private int balance;

    public User(int id, String name, String password, Role role) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
        this.balance = 0;
    }

    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public int getBalance() { return balance; }

    // Сеттеры
    public void addBalance(int amount) { this.balance += amount; }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPassword(String password){
        this.password = password;
    }
}