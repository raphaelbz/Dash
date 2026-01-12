package io.github.dash.engine.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Niveau contenant toutes les entites.
 */
public class Level {
    private final List<Entity> entities;
    private Player player;
    private float scrollSpeed;
    private float gravity;
    private String levelName;

    public Level() {
        this.entities = new ArrayList<>();
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        if (entity instanceof Player) {
            this.player = (Player) entity;
        }
    }

    public List<Entity> getEntities() { return entities; }
    public Player getPlayer() { return player; }

    public void setPlayer(Player player) { this.player = player; }

    public float getScrollSpeed() { return scrollSpeed; }
    public void setScrollSpeed(float scrollSpeed) { this.scrollSpeed = scrollSpeed; }

    public float getGravity() { return gravity; }
    public void setGravity(float gravity) { this.gravity = gravity; }

    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }
}
