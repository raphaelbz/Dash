package io.github.dash.engine.model;

/**
 * Represents an obstacle in the game.
 */
public class Obstacle extends Entity {
    public enum ObstacleType {
        SOLID,
        KILLER
    }

    private final ObstacleType type;

    public Obstacle(float x, float y, float width, float height, ObstacleType type) {
        super(x, y, width, height);
        this.type = type;
    }

    public ObstacleType getType() {
        return type;
    }
}
