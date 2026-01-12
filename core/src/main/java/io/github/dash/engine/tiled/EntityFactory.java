package io.github.dash.engine.tiled;

import io.github.dash.engine.model.EndFlag;
import io.github.dash.engine.model.Obstacle;
import io.github.dash.engine.model.Player;

/**
 * Factory for creating game entities.
 */
public class EntityFactory {

    public static Player createPlayer(float x, float y, float w, float h) {
        return new Player(x, y, w, h);
    }

    public static Obstacle createSolid(float x, float y, float w, float h) {
        return new Obstacle(x, y, w, h, Obstacle.ObstacleType.SOLID);
    }

    public static Obstacle createSpike(float x, float y, float w, float h) {
        // Spikes are triangular - create a much smaller hitbox approximating the triangle
        // Only the center/tip area is dangerous
        float hitboxWidth = w * 0.35f;  // 35% of width - narrow center
        float hitboxHeight = h * 0.5f;  // 50% of height - upper portion
        float hitboxX = x + (w - hitboxWidth) / 2; // Center horizontally
        float hitboxY = y + h * 0.3f; // Start 30% up from bottom (where the tip is)
        return new Obstacle(hitboxX, hitboxY, hitboxWidth, hitboxHeight, Obstacle.ObstacleType.KILLER);
    }

    public static EndFlag createEndFlag(float x, float y, float w, float h) {
        return new EndFlag(x, y, w, h);
    }
}
