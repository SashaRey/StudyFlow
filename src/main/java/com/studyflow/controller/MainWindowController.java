package com.studyflow.controller;

import com.studyflow.ui.navigation.NavigationManager;
import com.studyflow.ui.navigation.NavigationTarget;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class MainWindowController extends BaseController {

    @FXML
    private StackPane contentContainer;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button tasksButton;

    @FXML
    private Button scheduleButton;

    @FXML
    private Button examsButton;

    @FXML
    private Button analyticsButton;

    @FXML
    private Button settingsButton;

    private NavigationManager navigationManager;

    @FXML
    public void initialize() {
        navigationManager = new NavigationManager(contentContainer);
        openScreen(NavigationTarget.DASHBOARD, dashboardButton);
    }

    @FXML
    public void showDashboard() {
        openScreen(NavigationTarget.DASHBOARD, dashboardButton);
    }

    @FXML
    public void showTasks() {
        openScreen(NavigationTarget.TASKS, tasksButton);
    }

    @FXML
    public void showSchedule() {
        openScreen(NavigationTarget.SCHEDULE, scheduleButton);
    }

    @FXML
    public void showExams() {
        openScreen(NavigationTarget.EXAMS, examsButton);
    }

    @FXML
    public void showAnalytics() {
        openScreen(NavigationTarget.ANALYTICS, analyticsButton);
    }

    @FXML
    public void showSettings() {
        openScreen(NavigationTarget.SETTINGS, settingsButton);
    }

    private void openScreen(NavigationTarget target, Button activeButton) {
        navigationManager.navigateTo(target);
        setActiveButton(activeButton);
    }

    private void setActiveButton(Button activeButton) {
        Button[] buttons = {
                dashboardButton,
                tasksButton,
                scheduleButton,
                examsButton,
                analyticsButton,
                settingsButton
        };

        for (Button button : buttons) {
            button.getStyleClass().remove("nav-button-active");
        }

        activeButton.getStyleClass().add("nav-button-active");
    }
}
