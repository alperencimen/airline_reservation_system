package com.airline.model;

public class Admin implements ARSModel {
    private int id;
    private String username;
    private String password;
    private boolean isActive;

    // Getters
    @Override
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    // Setters
    @Override
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }
}