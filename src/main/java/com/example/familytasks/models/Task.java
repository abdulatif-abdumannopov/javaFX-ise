package com.example.familytasks.models;

public class Task {
    private int id;
    private String title;
    private int reward;
    private int childId;
    private String status; // NEW, PENDING, COMPLETED

    public Task(int id, String title, int reward, int childId) {
        this.id = id;
        this.title = title;
        this.reward = reward;
        this.childId = childId;
        this.status = "NEW";
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getReward() { return reward; }
    public int getChildId() { return childId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public void setTitle(String title){ this.title = title; }
    public void setReward(int reward){ this.reward = reward; }
    public void setChildId(int childId){ this.childId = childId; }
}
