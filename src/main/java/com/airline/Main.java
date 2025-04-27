package com.airline;

import com.airline.controller.UserController;
import com.airline.view.LoginUI;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            LoginUI loginUI = new LoginUI();
            loginUI.display();
        });
    }
}