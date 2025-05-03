
package com.airline.model;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private int userId;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;


    public Notification() {}

    public Notification(int userId, String message, LocalDateTime createdAt) {
        this.userId = userId;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }


}
