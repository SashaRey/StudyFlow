package com.studyflow.ui.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Loads and switches screens inside the main content area.
 */
public class NavigationManager {

    private final StackPane contentContainer;
    private final Map<NavigationTarget, Parent> screenCache = new EnumMap<>(NavigationTarget.class);

    public NavigationManager(StackPane contentContainer) {
        this.contentContainer = contentContainer;
    }

    public void navigateTo(NavigationTarget target) {
        Parent screen = screenCache.computeIfAbsent(target, this::loadScreen);
        contentContainer.getChildren().setAll(screen);
    }

    private Parent loadScreen(NavigationTarget target) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(target.getFxmlPath()));
            return loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load screen: " + target.name(), exception);
        }
    }
}

