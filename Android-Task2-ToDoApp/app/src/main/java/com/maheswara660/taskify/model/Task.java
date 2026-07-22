package com.maheswara660.taskify.model;

public class Task {
    private int id;
    private int userId;
    private String taskText;
    private boolean isCompleted;
    private long createdAt;

    public Task() {
    }

    public Task(int id, int userId, String taskText, boolean isCompleted, long createdAt) {
        this.id = id;
        this.userId = userId;
        this.taskText = taskText;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
    }

    public Task(int userId, String taskText, boolean isCompleted, long createdAt) {
        this.userId = userId;
        this.taskText = taskText;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
