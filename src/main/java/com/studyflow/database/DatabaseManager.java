package com.studyflow.database;

import com.studyflow.config.AppConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {

    private static DatabaseManager instance;

    private DatabaseManager() {
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(AppConfig.DB_URL);
    }

    public void initializeDatabase() {
        try (Connection connection = getConnection()) {
            for (String sql : readSchema().split(";")) {
                String trimmed = sql.trim();
                if (!trimmed.isBlank() && !trimmed.startsWith("--")) {
                    try (Statement statement = connection.createStatement()) {
                        statement.execute(trimmed);
                    }
                }
            }
        } catch (SQLException | IOException exception) {
            throw new IllegalStateException("Failed to initialize database schema", exception);
        }
    }

    private String readSchema() throws IOException {
        InputStream inputStream = DatabaseManager.class.getResourceAsStream("/db/schema.sql");
        if (inputStream == null) {
            throw new IllegalStateException("Schema file not found: /db/schema.sql");
        }

        StringBuilder schema = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                schema.append(line).append(System.lineSeparator());
            }
        }
        return schema.toString();
    }
}
