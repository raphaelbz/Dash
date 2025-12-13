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
        return new Obstacle(x, y, w, h, Obstacle.ObstacleType.KILLER);
    }

    public static EndFlag createEndFlag(float x, float y, float w, float h) {
        return new EndFlag(x, y, w, h);
    }
}
