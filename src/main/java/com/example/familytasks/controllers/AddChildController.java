package com.example.familytasks.controllers;

import com.example.familytasks.models.Role;
import com.example.familytasks.models.User;
import com.example.familytasks.services.DataService;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

public class AddChildController {
    @FXML private TextField nameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String rawPassword = passwordField.getText().trim();

        if (!name.isEmpty() && !rawPassword.isEmpty()) {
            
            String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

            
            int newId = DataService.getUsers().stream()
                    .mapToInt(User::getId).max().orElse(0) + 1;

            
            User newChild = new User(newId, name, hashedPassword, Role.CHILD);

            
            DataService.getUsers().add(newChild);
            DataService.saveData();

            closeWindow();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
}