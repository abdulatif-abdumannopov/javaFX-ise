package com.example.familytasks.controllers;

import com.example.familytasks.models.Role;
import com.example.familytasks.models.User;
import com.example.familytasks.services.DataService;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddChildController {
    @FXML private TextField nameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (!name.isEmpty() && !pass.isEmpty()) {
            // Генерируем новый ID (максимальный + 1)
            int newId = DataService.getUsers().stream()
                    .mapToInt(User::getId)
                    .max().orElse(0) + 1;

            User newChild = new User(newId, name, pass, Role.CHILD);
            DataService.getUsers().add(newChild);
            DataService.saveData(); // Сохраняем в JSON

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