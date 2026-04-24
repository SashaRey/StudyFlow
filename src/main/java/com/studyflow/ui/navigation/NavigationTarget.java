package com.studyflow.ui.navigation;

/**
 * Defines available application screens and their FXML paths.
 */
public enum NavigationTarget {
    DASHBOARD("/fxml/screens/dashboard-screen.fxml"),
    TASKS("/fxml/screens/tasks-screen.fxml"),
    SCHEDULE("/fxml/screens/schedule-screen.fxml"),
    EXAMS("/fxml/screens/exams-screen.fxml"),
    ANALYTICS("/fxml/screens/analytics-screen.fxml"),
    SETTINGS("/fxml/screens/settings-screen.fxml");

    private final String fxmlPath;

    NavigationTarget(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }
}

