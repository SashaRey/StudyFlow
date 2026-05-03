package com.studyflow.app;

import com.studyflow.database.DatabaseManager;
import com.studyflow.ui.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void init() {
        DatabaseManager.getInstance().initializeDatabase();
    }

    @Override
    public void start(Stage primaryStage) {
        SceneManager sceneManager = new SceneManager(primaryStage);
        sceneManager.showMainWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
