package io.github.dash.engine.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import io.github.dash.engine.model.Level;

/**
 * Loads levels from Tiled maps.
 */
public class TiledLevelLoader {

    public static class LoadedLevel {
        public final Level level;
        public final TiledMap map;

        public LoadedLevel(Level level, TiledMap map) {
            this.level = level;
            this.map = map;
        }
    }

    public LoadedLevel load(String tmxPath) {
        TiledMap map = null;
        try {
            if (Gdx.files.internal(tmxPath).exists()) {
                map = new TmxMapLoader().load(tmxPath);
            } else {
                Gdx.app.error("TiledLevelLoader", "Map file not found: " + tmxPath);
            }
        } catch (Exception e) {
            Gdx.app.error("TiledLevelLoader", "Error loading map: " + e.getMessage());
        }

        if (map == null) {
            return createFallbackLevel();
        }

        Level level = new Level();
        MapProperties props = map.getProperties();

        // Load global properties
        level.setScrollSpeed(props.get("scrollSpeed", 150.0f, Float.class));
        level.setGravity(props.get("gravity", -1000.0f, Float.class));
        level.setLevelName(props.get("levelName", "Unknown", String.class));

        // Load objects
        MapLayer objectLayer = map.getLayers().get("Objects");
        if (objectLayer == null) {
            objectLayer = map.getLayers().get("Objets");
        }

        if (objectLayer != null) {
            for (MapObject obj : objectLayer.getObjects()) {
                MapProperties objProps = obj.getProperties();
                float x = objProps.get("x", 0f, Float.class);
                float y = objProps.get("y", 0f, Float.class);
                float w = objProps.get("width", 32f, Float.class);
                float h = objProps.get("height", 32f, Float.class);
                String type = objProps.get("type", "", String.class);

                if ("PlayerStart".equals(type) || "spawn".equals(type)) {
                    level.setPlayer(EntityFactory.createPlayer(x, y, w, h));
                    level.addEntity(level.getPlayer());
                } else if ("Spike".equals(type) || "kill".equals(type)) {
                    level.addEntity(EntityFactory.createSpike(x, y, w, h));
                } else if ("Solid".equals(type)) {
                    level.addEntity(EntityFactory.createSolid(x, y, w, h));
                } else if ("EndFlag".equals(type) || "finish".equals(type)) {
                    level.addEntity(EntityFactory.createEndFlag(x, y, w, h));
                }
            }
        }

        // Ensure player exists
        if (level.getPlayer() == null) {
            level.setPlayer(EntityFactory.createPlayer(100, 100, 32, 32));
            level.addEntity(level.getPlayer());
        }

        return new LoadedLevel(level, map);
    }

    private LoadedLevel createFallbackLevel() {
        Gdx.app.log("TiledLevelLoader", "Creating fallback level");
        Level level = new Level();
        level.setScrollSpeed(150.0f);
        level.setGravity(-1000.0f);
        level.setLevelName("Fallback Level");

        // Player
        level.setPlayer(EntityFactory.createPlayer(100, 50, 32, 32));
        level.addEntity(level.getPlayer());

        // Ground
        level.addEntity(EntityFactory.createSolid(0, 0, 10000, 50));

        // Some obstacles
        level.addEntity(EntityFactory.createSpike(500, 50, 32, 32));
        level.addEntity(EntityFactory.createSolid(700, 50, 100, 50));

        // End Flag
        level.addEntity(EntityFactory.createEndFlag(1500, 50, 32, 100));

        // We return null for map in fallback, Renderer must handle null map
        return new LoadedLevel(level, null);
    }
}
