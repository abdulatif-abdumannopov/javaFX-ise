package com.example.familytasks.controllers;

import com.example.familytasks.HelloApplication;
import com.example.familytasks.models.Task;
import com.example.familytasks.models.User;
import com.example.familytasks.services.DataService;
import com.example.familytasks.services.SessionService;
import javafx.collections.FXCollections;
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

public class ChildController {

    @FXML private TableView<Task> tasksTable;
    @FXML private TableColumn<Task, String> colTitle;
    @FXML private TableColumn<Task, Integer> colReward;
    @FXML private TableColumn<Task, String> colStatus;
    @FXML private Label balanceLabel;
    @FXML private Button completeButton;

    private User currentUser;

    @FXML
    public void initialize() {
        
        currentUser = SessionService.getSavedSessionUser();

        
        colTitle.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTitle()));
        colReward.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getReward()));
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus()));

        refreshData();
    }

    private void refreshData() {
        if (currentUser == null) return;
        balanceLabel.setText(String.valueOf(currentUser.getBalance()));

        
        List<Task> allTasks = new ArrayList<>(DataService.getTasks());
        Collections.reverse(allTasks); 

        List<Task> myActiveTasks = allTasks.stream()
                .filter(t -> t.getChildId() == currentUser.getId()) 
                .filter(t -> !"COMPLETED".equals(t.getStatus()))    
                .toList();

        tasksTable.setItems(FXCollections.observableArrayList(myActiveTasks));
    }

    @FXML
    private void handleShowHistory() throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("history_dialog.fxml"));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Мои выполненные задания");
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(org.kordamp.bootstrapfx.BootstrapFX.bootstrapFXStylesheet());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleCompleteTask() {
        Task selected = tasksTable.getSelectionModel().getSelectedItem();

        if (selected != null && "NEW".equals(selected.getStatus())) {
            selected.setStatus("PENDING");
            DataService.saveData();

            
            tasksTable.refresh(); 
            
            
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        SessionService.clearSession();
        HelloApplication.changeScene("login.fxml");
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
}