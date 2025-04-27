package com.airline.controller;

import com.airline.model.User;
import com.airline.ui.UserDashboardUI;

public class UserController implements ARSController {
    private User currentUser;
    private UserDashboardUI userDashboardUI;

    public UserController(User currentUser) {
        this.currentUser = currentUser;
        this.userDashboardUI = new UserDashboardUI(currentUser);
    }

    @Override
    public void handleRequest() {
        userDashboardUI.display();
    }
}