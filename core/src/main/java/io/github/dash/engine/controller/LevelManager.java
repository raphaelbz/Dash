package io.github.dash.engine.controller;

import io.github.dash.engine.tiled.TiledLevelLoader;

/**
 * Gestionnaire de chargement des niveaux.
 */
public class LevelManager {
    private final TiledLevelLoader loader;

    public LevelManager() {
        this.loader = new TiledLevelLoader();
    }

    public TiledLevelLoader.LoadedLevel loadLevel(String path) {
        return loader.load(path);
    }
}
