package com.studyflow.controller;

import com.studyflow.ui.SceneManager;

public abstract class BaseController {

    protected SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}
