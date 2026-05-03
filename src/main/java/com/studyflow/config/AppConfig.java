package com.studyflow.config;

import java.nio.file.Path;

public final class AppConfig {
    public static final String APP_TITLE = "StudyFlow";
    public static final String DB_URL = "jdbc:sqlite:" + Path.of(System.getProperty("user.dir"), "studyflow.db").toString();
    public static final String MAIN_WINDOW_FXML = "/fxml/main-window.fxml";

    private AppConfig() {
        throw new IllegalStateException("Utility class");
    }
}
