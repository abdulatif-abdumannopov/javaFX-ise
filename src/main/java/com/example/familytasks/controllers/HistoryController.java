package com.example.familytasks.controllers;

import com.example.familytasks.models.Role;
import com.example.familytasks.models.Task;
import com.example.familytasks.models.User;
import com.example.familytasks.services.DataService;
import com.example.familytasks.services.SessionService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryController {
    @FXML private TableView<Task> historyTable;
    @FXML private TableColumn<Task, String> colTitle;
    @FXML private TableColumn<Task, String> colChild;
    @FXML private TableColumn<Task, Integer> colReward;
    @FXML private TableColumn<Task, String> colStatus;

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTitle()));
        colReward.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getReward()));
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus()));

        colChild.setCellValueFactory(d -> {
            User child = DataService.getUserById(d.getValue().getChildId());
            return new javafx.beans.property.SimpleStringProperty(child != null ? child.getName() : "---");
        });

        loadHistory();
    }

    private void loadHistory() {
        User current = SessionService.getSavedSessionUser();

        List<Task> history = DataService.getTasks().stream()
                .filter(t -> "COMPLETED".equals(t.getStatus()))
                .filter(t -> current.getRole().toString().equals("PARENT") || t.getChildId() == current.getId())
                .filter(t -> current.getRole() == Role.PARENT || t.getChildId() == current.getId())
                .collect(Collectors.toList());

        Collections.reverse(history); // Последние выполненные — сверху
        historyTable.setItems(FXCollections.observableArrayList(history));
    }

    @FXML private void handleClose() {
        ((Stage) historyTable.getScene().getWindow()).close();
    }
}