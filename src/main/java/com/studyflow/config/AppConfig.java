package com.studyflow.config;

public final class AppConfig {
    public static final String APP_TITLE = "StudyFlow";
    public static final String DB_URL = "jdbc:sqlite:studyflow.db";
    public static final String MAIN_WINDOW_FXML = "/fxml/main-window.fxml";

    private AppConfig() {
        throw new IllegalStateException("Utility class");
    }
}
