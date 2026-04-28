package com.example.familytasks;

import atlantafx.base.theme.PrimerDark;
import com.example.familytasks.models.Role;
import com.example.familytasks.models.User;
import com.example.familytasks.services.DataService;
import com.example.familytasks.services.SessionService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DataService.loadData();
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        // --- ЛОГИКА СЕССИИ ---
        User savedUser = SessionService.getSavedSessionUser();

        if (savedUser != null) {
            // Если нашли сессию, сразу определяем экран по роли
            String fxml = (savedUser.getRole() == Role.PARENT) ? "parent_main.fxml" : "child_main.fxml";
            showScene(stage, fxml, "Family Tasks - Кабинет");
        } else {
            // Если сессии нет, открываем логин
            showScene(stage, "login.fxml", "Family Tasks - Авторизация");
        }
    }

    // Вспомогательный метод для первичной настройки сцены
    private void showScene(Stage stage, String fxml, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(true); // Для главных окон лучше разрешить ресайз
        stage.show();
    }

    public static void changeScene(String fxml) throws IOException {
        // Берем текущее активное окно
        Stage stage = (Stage) Stage.getWindows().filtered(Window::isShowing).get(0);
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}