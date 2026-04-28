package com.example.familytasks.controllers;

import com.example.familytasks.models.Role;
import com.example.familytasks.models.Task;
import com.example.familytasks.models.User;
import com.example.familytasks.services.DataService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddTaskController {
    @FXML private TextField titleField;
    @FXML private TextField rewardField;
    @FXML private ComboBox<User> childSelector; 

    private Task taskToEdit = null;

    @FXML
    public void initialize() {
        
        childSelector.getItems().addAll(
                DataService.getUsers().stream()
                        .filter(u -> u.getRole() == Role.CHILD)
                        .toList()
        );

        
        childSelector.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        childSelector.setButtonCell(childSelector.getCellFactory().call(null));
    }

    
    public void setTaskData(Task task) {
        this.taskToEdit = task;
        titleField.setText(task.getTitle());
        rewardField.setText(String.valueOf(task.getReward()));

        
        User selectedChild = DataService.getUserById(task.getChildId());
        childSelector.getSelectionModel().select(selectedChild);
    }

    @FXML
    private void handleSave() {
        try {
            String title = titleField.getText().trim();
            String rewardStr = rewardField.getText().trim();
            User selectedChild = childSelector.getValue();

            if (title.isEmpty() || rewardStr.isEmpty() || selectedChild == null) {
                System.out.println("Ошибка: Заполните все поля");
                return;
            }

            int reward = Integer.parseInt(rewardStr);

            if (taskToEdit != null) {
                
                taskToEdit.setTitle(title);
                taskToEdit.setReward(reward);
                taskToEdit.setChildId(selectedChild.getId());
            } else {
                
                int newId = DataService.getTasks().stream()
                        .mapToInt(Task::getId).max().orElse(0) + 1;

                Task newTask = new Task(newId, title, reward, selectedChild.getId());
                DataService.getTasks().add(newTask);
            }

            DataService.saveData(); 
            closeWindow();

        } catch (NumberFormatException e) {
            System.out.println("Ошибка: очки должны быть целым числом");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) titleField.getScene().getWindow()).close();
    }
}