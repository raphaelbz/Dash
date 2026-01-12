package io.github.dash.engine.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import io.github.dash.engine.model.Entity;
import io.github.dash.engine.model.Level;
import io.github.dash.engine.model.Obstacle;

/**
 * Loads levels from Tiled maps.
 * Supports both object layers (legacy) and tile layers (for maps created with tile placement).
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
                Gdx.app.log("TiledLevelLoader", "Successfully loaded map: " + tmxPath);
            } else {
                Gdx.app.error("TiledLevelLoader", "Map file not found: " + tmxPath);
            }
        } catch (Exception e) {
            Gdx.app.error("TiledLevelLoader", "Error loading map: " + e.getMessage());
            e.printStackTrace();
        }

        if (map == null) {
            return createFallbackLevel();
        }

        Level level = new Level();
        MapProperties props = map.getProperties();

        // Load global properties
        level.setScrollSpeed(props.get("scrollSpeed", 350.0f, Float.class));
        level.setGravity(props.get("gravity", -3500.0f, Float.class));
        level.setLevelName(props.get("levelName", "Unknown", String.class));

        // Get tile dimensions from map
        int tileWidth = props.get("tilewidth", 32, Integer.class);
        int tileHeight = props.get("tileheight", 32, Integer.class);
        int mapHeight = props.get("height", 20, Integer.class);

        Gdx.app.log("TiledLevelLoader", "Map tile size: " + tileWidth + "x" + tileHeight + ", height: " + mapHeight + " tiles");

        // First, try to parse tile layers (for maps like mapraf.tmx)
        boolean foundTileLayers = parseTileLayers(map, level, tileWidth, tileHeight, mapHeight);

        // If no tile layers with entities were found, try object layers (legacy support)
        if (!foundTileLayers) {
            parseObjectLayers(map, level);
        }

        // Ensure player exists - place at start of level
        if (level.getPlayer() == null) {
            // Find the first ground tile to place player above it
            float playerX = 256; // Default start position
            float playerY = findGroundHeight(level, playerX) + 10;

            // Player size - proportional to 256px tiles (about 3/4 of tile height)
            float playerSize = 180;
            level.setPlayer(EntityFactory.createPlayer(playerX, playerY, playerSize, playerSize));
            level.addEntity(level.getPlayer());
            Gdx.app.log("TiledLevelLoader", "Created player at: " + playerX + ", " + playerY + " size: " + playerSize);
        }

        Gdx.app.log("TiledLevelLoader", "Level loaded with " + level.getEntities().size() + " entities");
        return new LoadedLevel(level, map);
    }

    /**
     * Parse tile layers to create game entities.
     * Layers are identified by their name or properties.
     */
    private boolean parseTileLayers(TiledMap map, Level level, int tileWidth, int tileHeight, int mapHeight) {
        boolean foundEntities = false;

        for (MapLayer layer : map.getLayers()) {
            if (!(layer instanceof TiledMapTileLayer)) {
                continue;
            }

            TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
            String layerName = tileLayer.getName().toLowerCase();
            MapProperties layerProps = tileLayer.getProperties();

            // Determine layer type
            boolean isGround = layerName.contains("ground") || layerName.contains("solid") ||
                              layerProps.get("ground", false, Boolean.class) ||
                              "solid".equalsIgnoreCase(tileLayer.getClass().getSimpleName());

            boolean isObstacle = layerName.contains("obstacle") || layerName.contains("spike") ||
                                layerProps.get("kill", false, Boolean.class);

            boolean isEndFlag = layerName.contains("endflag") || layerName.contains("flag") ||
                               layerName.contains("finish") || layerName.contains("end");

            if (!isGround && !isObstacle && !isEndFlag) {
                // Check class property (Tiled uses "class" in newer versions)
                String layerClass = layerProps.get("class", "", String.class);
                if (layerClass.isEmpty()) {
                    // Try getting it from the raw layer name attribute
                    layerClass = layer.getName();
                }

                if ("solid".equalsIgnoreCase(layerClass)) isGround = true;
                else if ("obstacle".equalsIgnoreCase(layerClass)) isObstacle = true;
                else if ("endflag".equalsIgnoreCase(layerClass)) isEndFlag = true;
            }

            // Parse tiles from this layer
            int layerWidth = tileLayer.getWidth();
            int layerHeight = tileLayer.getHeight();

            // Scale factor for obstacles (smaller = easier to dodge)
            float obstacleScale = 0.6f;

            for (int x = 0; x < layerWidth; x++) {
                for (int y = 0; y < layerHeight; y++) {
                    TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                    if (cell != null && cell.getTile() != null) {
                        // Calculate world position (Tiled Y is already handled by LibGDX)
                        float worldX = x * tileWidth;
                        float worldY = y * tileHeight;

                        if (isGround) {
                            level.addEntity(EntityFactory.createSolid(worldX, worldY, tileWidth, tileHeight));
                            foundEntities = true;
                        } else if (isObstacle) {
                            // Scale down obstacles and center them in tile
                            float scaledW = tileWidth * obstacleScale;
                            float scaledH = tileHeight * obstacleScale;
                            float offsetX = (tileWidth - scaledW) / 2;
                            float offsetY = 0; // Keep at bottom of tile
                            level.addEntity(EntityFactory.createSpike(worldX + offsetX, worldY + offsetY, scaledW, scaledH));
                            foundEntities = true;
                        } else if (isEndFlag) {
                            level.addEntity(EntityFactory.createEndFlag(worldX, worldY, tileWidth, tileHeight));
                            foundEntities = true;
                        }
                    }
                }
            }

            if (foundEntities) {
                Gdx.app.log("TiledLevelLoader", "Parsed tile layer: " + tileLayer.getName() +
                           " (ground=" + isGround + ", obstacle=" + isObstacle + ", endFlag=" + isEndFlag + ")");
            }
        }

        return foundEntities;
    }

    /**
     * Parse object layers for entity definitions (legacy/alternate format).
     */
    private void parseObjectLayers(TiledMap map, Level level) {
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
            Gdx.app.log("TiledLevelLoader", "Parsed object layer with " + objectLayer.getObjects().getCount() + " objects");
        }
    }

    /**
     * Find the height of the ground at a given X position.
     */
    private float findGroundHeight(Level level, float x) {
        float maxY = 0;
        for (Entity entity : level.getEntities()) {
            if (entity instanceof Obstacle) {
                Obstacle obs = (Obstacle) entity;
                if (obs.getType() == Obstacle.ObstacleType.SOLID) {
                    if (x >= obs.getX() && x < obs.getX() + obs.getWidth()) {
                        float top = obs.getY() + obs.getHeight();
                        if (top > maxY) {
                            maxY = top;
                        }
                    }
                }
            }
        }
        return maxY;
    }

    private LoadedLevel createFallbackLevel() {
        Gdx.app.log("TiledLevelLoader", "Creating fallback level");
        Level level = new Level();
        level.setScrollSpeed(150.0f);
        level.setGravity(-1500.0f);
        level.setLevelName("Fallback Level");

        // Player
        level.setPlayer(EntityFactory.createPlayer(100, 114, 64, 64));
        level.addEntity(level.getPlayer());

        // Ground
        level.addEntity(EntityFactory.createSolid(0, 0, 10000, 50));

        // Some obstacles
        level.addEntity(EntityFactory.createSpike(500, 50, 32, 32));
        level.addEntity(EntityFactory.createSolid(700, 50, 100, 50));

        // End Flag
        level.addEntity(EntityFactory.createEndFlag(1500, 50, 32, 100));

        return new LoadedLevel(level, null);
    }
}
