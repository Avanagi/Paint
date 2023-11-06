package com.example.paint;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class Paint extends Application {
    /**
     * Метод, обрабатывающий логику создания сцены
     *
     * @param stage Место, в котором реализуется сцена
     * @throws IOException Параметр, отвечающий за выбрасываемые ошибки
     */
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("paint.fxml")));
        Scene scene = new Scene(root);
        stage.setTitle("Paint");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Метод, запускающий всю программу
     */
    public static void main(String[] args) {
        launch();
    }
}
