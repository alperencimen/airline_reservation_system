package com.airline.model;

public interface ARSModel {
    // Common methods for models

    // Get the ID of the entity
    int getId();

    // Set the ID of the entity
    void setId(int id);

    // Check if the entity is active
    boolean isActive();

    // Set the active status of the entity
    void setActive(boolean active);
}