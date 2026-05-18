package com.example.tasklyproduction;

public class Task {
    private int id;
    private int userId;
    private String title;
    private String description;
    private String dueDate;
    private String alertTime;
    private String priority; // HIGH, MEDIUM, LOW
    private boolean isCompleted;
    private String createdAt;
    private String updatedAt;

    // Default constructor
    public Task() {
    }

    // Constructor for creating a new task
    public Task(int userId, String title, String description, String dueDate,
                String alertTime, String priority) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.alertTime = alertTime;
        this.priority = priority;
        this.isCompleted = false;
    }

    // Full constructor
    public Task(int id, int userId, String title, String description, String dueDate,
                String alertTime, String priority, boolean isCompleted,
                String createdAt, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.alertTime = alertTime;
        this.priority = priority;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getAlertTime() { return alertTime; }
    public void setAlertTime(String alertTime) { this.alertTime = alertTime; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
