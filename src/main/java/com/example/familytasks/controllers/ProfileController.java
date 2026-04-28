package com.example.familytasks.controllers;

import com.example.familytasks.models.User;
import com.example.familytasks.services.DataService;
import com.example.familytasks.services.SessionService;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

public class ProfileController {
    @FXML private TextField nameField;
    @FXML private PasswordField passwordField;

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionService.getSavedSessionUser();
        if (currentUser != null) {
            nameField.setText(currentUser.getName());
        }
    }

    @FXML
    private void handleSave() {
        if (currentUser == null) return;

        String newName = nameField.getText().trim();
        String newPass = passwordField.getText().trim();

        if (!newName.isEmpty()) {
            currentUser.setName(newName);

            if (!newPass.isEmpty()) {
                String hashedPassword = BCrypt.hashpw(newPass, BCrypt.gensalt());
                currentUser.setPassword(hashedPassword);
            }

            DataService.saveData();
            closeWindow();
        }
    }

    @FXML private void handleCancel() { closeWindow(); }

    private void closeWindow() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
}