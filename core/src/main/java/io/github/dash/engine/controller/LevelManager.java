package io.github.dash.engine.controller;

import io.github.dash.engine.tiled.TiledLevelLoader;

/**
 * Manages level loading.
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
