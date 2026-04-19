package com.studyflow.ui;

import com.studyflow.config.AppConfig;
import com.studyflow.controller.BaseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private final Stage primaryStage;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.MAIN_WINDOW_FXML));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof BaseController baseController) {
                baseController.setSceneManager(this);
            }

            Scene scene = new Scene(root, 1000, 640);
            scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
            primaryStage.setTitle(AppConfig.APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load main window", exception);
        }
    }
}
