module com.example.familytasks {
    // Стандартные модули JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // Библиотеки, которые ты добавил в pom.xml
    requires com.google.gson;
    requires org.kordamp.bootstrapfx.core;
    requires net.synedra.validatorfx;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires atlantafx.base;

    // Если ты все еще используешь AtlantaFX (дизайн), добавь и его:
    // requires atlantafx.base;

    // Разрешаем JavaFX и другим библиотекам доступ к твоим классам
    opens com.example.familytasks to javafx.fxml;
    opens com.example.familytasks.controllers to javafx.fxml;

    // КРИТИЧНО ДЛЯ GSON: разрешаем ему читать твои модели и сервисы
    opens com.example.familytasks.models to com.google.gson;
    opens com.example.familytasks.services to com.google.gson;

    exports com.example.familytasks;
}