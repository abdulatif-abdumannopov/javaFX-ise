package com.example.familytasks.controllers;

import com.example.familytasks.HelloApplication;
import com.example.familytasks.models.Role;
import com.example.familytasks.models.Task;
import com.example.familytasks.models.User;
import com.example.familytasks.services.DataService;
import com.example.familytasks.services.SessionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParentController {

    @FXML private ListView<String> childrenList;

    
    @FXML private TableView<Task> tasksTable;
    @FXML private TableColumn<Task, String> colTitle;
    @FXML private TableColumn<Task, Integer> colReward;
    @FXML private TableColumn<Task, String> colStatus;
    @FXML private TableColumn<Task, String> colChild; 
    @FXML private Button btnReject;
    @FXML private Button btnApprove;
    public void initialize() {
        
        colTitle.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));

        colReward.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getReward()));

        colStatus.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        colChild.setCellValueFactory(cellData -> {
            int id = cellData.getValue().getChildId();
            User child = DataService.getUserById(id);
            String name = (child != null) ? child.getName() : "ID: " + id;
            return new javafx.beans.property.SimpleStringProperty(name);
        });

        btnApprove.setDisable(true);
        btnReject.setDisable(true);

        
        tasksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                
                btnApprove.setDisable(!"PENDING".equals(newSelection.getStatus()));
                btnReject.setDisable(!"PENDING".equals(newSelection.getStatus()));
            } else {
                btnApprove.setDisable(true);
                btnReject.setDisable(true);
            }
        });


        refreshData();
    }

    public void refreshData() {
        
        ObservableList<String> childrenNames = FXCollections.observableArrayList();
        for (User u : DataService.getUsers()) {
            if (u.getRole() == Role.CHILD) {
                childrenNames.add(u.getName() + " | Баланс: " + u.getBalance() + " 🪙");
            }
        }
        childrenList.setItems(childrenNames);

        
        List<Task> allTasks = new ArrayList<>(DataService.getTasks());
        Collections.reverse(allTasks); 


        List<Task> activeTasks = allTasks.stream()
                .filter(t -> !"COMPLETED".equals(t.getStatus()))
                .toList();

        tasksTable.setItems(FXCollections.observableArrayList(activeTasks));
        tasksTable.refresh();
    }

    @FXML
    private void handleAddTask() throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("add_task_dialog.fxml"));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Новое задание");
        stage.setScene(new Scene(loader.load()));

        stage.showAndWait(); 

        refreshData(); 
    }

    @FXML
    private void handleLogout() throws IOException {
        SessionService.clearSession(); 
        HelloApplication.changeScene("login.fxml");
    }

    @FXML
    private void handleAddChild() throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("add_child_dialog.fxml"));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Регистрация ребенка");

        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(org.kordamp.bootstrapfx.BootstrapFX.bootstrapFXStylesheet());

        stage.setScene(scene);
        stage.showAndWait(); 

        refreshData(); 
    }

    @FXML
    private void handleApproveTask() {
        Task selected = tasksTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            System.out.println("Выбрана задача со статусом: " + selected.getStatus()); 

            if ("PENDING".equals(selected.getStatus())) {
                selected.setStatus("COMPLETED");

                User child = DataService.getUserById(selected.getChildId());
                if (child != null) {
                    child.addBalance(selected.getReward());
                }

                DataService.saveData();
                refreshData(); 
            } else {
                
                System.out.println("Эту задачу нельзя одобрить, она еще не на проверке.");
            }
        }
    }

    @FXML
    private void handleRejectTask() {
        Task selected = tasksTable.getSelectionModel().getSelectedItem();
        if (selected != null && "PENDING".equals(selected.getStatus())) {
            selected.setStatus("NEW"); 
            DataService.saveData();
            refreshData();
        }
    }

    @FXML
    private void handleShowHistory() throws IOException {
        
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("history_dialog.fxml"));

        
        Scene scene = new Scene(loader.load());

        
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); 
        stage.setTitle("История выполненных заданий");

        
        scene.getStylesheets().add(org.kordamp.bootstrapfx.BootstrapFX.bootstrapFXStylesheet());
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void handleShowProfile() throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("profile_dialog.fxml"));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Мой профиль");
        stage.setScene(new Scene(loader.load()));
        stage.showAndWait();

        refreshData(); 
    }

    @FXML
    private void handleDeleteTask() {
        Task selected = tasksTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить задание \"" + selected.getTitle() + "\"?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    DataService.getTasks().remove(selected);
                    DataService.saveData();
                    refreshData();
                }
            });
        }
    }

    @FXML
    private void handleDeleteChild() {
        
        int selectedIdx = childrenList.getSelectionModel().getSelectedIndex();
        if (selectedIdx >= 0) {
            
            String selectedItem = childrenList.getSelectionModel().getSelectedItem();
            String childName = selectedItem.split(" \\|")[0];

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить профиль ребенка " + childName + "? Все его задачи останутся без автора.", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    DataService.getUsers().removeIf(u -> u.getName().equals(childName) && u.getRole().toString().equals("CHILD"));
                    DataService.saveData();
                    refreshData();
                }
            });
        }
    }

    @FXML
    private void handleEditTask() throws IOException {
        Task selected = tasksTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("add_task_dialog.fxml"));
            Scene scene = new Scene(loader.load());

            AddTaskController controller = loader.getController();
            controller.setTaskData(selected); 

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
            refreshData();
        }
    }
}