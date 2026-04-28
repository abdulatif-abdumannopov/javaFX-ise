package com.example.familytasks.controllers;

import com.example.familytasks.HelloApplication;
import com.example.familytasks.models.Role;
import com.example.familytasks.models.User;
import com.example.familytasks.services.DataService;
import com.example.familytasks.services.SessionService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class LoginController {
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMe;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() throws IOException {
        String name = loginField.getText();
        String pass = passwordField.getText();

        User user = DataService.authenticate(name, pass);

        if (user != null) {
            if (rememberMe.isSelected()) {
                SessionService.saveSession(user);
            }

            
            if (user.getRole() == Role.PARENT) {
                HelloApplication.changeScene("parent_main.fxml");
            } else {
                HelloApplication.changeScene("child_main.fxml");
            }
        } else {
            errorLabel.setText("Неверный логин или пароль");
        }
    }
}