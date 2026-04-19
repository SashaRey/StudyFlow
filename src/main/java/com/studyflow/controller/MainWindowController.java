package com.studyflow.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainWindowController extends BaseController {

    @FXML
    private Label contentTitleLabel;

    @FXML
    public void showDashboard() {
        updateContentTitle("Dashboard");
    }

    @FXML
    public void showTasks() {
        updateContentTitle("Tasks");
    }

    @FXML
    public void showSchedule() {
        updateContentTitle("Schedule");
    }

    @FXML
    public void showExams() {
        updateContentTitle("Exams");
    }

    @FXML
    public void showAnalytics() {
        updateContentTitle("Analytics");
    }

    @FXML
    public void showSettings() {
        updateContentTitle("Settings");
    }

    private void updateContentTitle(String title) {
        // TODO Replace this placeholder with real screen routing/content loading.
        contentTitleLabel.setText(title);
    }
}
