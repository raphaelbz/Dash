package io.github.dash.engine.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

/**
 * Manages the game state and logic.
 * Handles physics, collisions, and game state transitions.
 */
public class GameWorld {
    private final Level currentLevel;
    private boolean levelCompleted;
    private boolean playerDead;
    private final float deathYThreshold = -100f;

    public GameWorld(Level level) {
        this.currentLevel = level;
    }

    public void update(float delta) {
        if (playerDead || levelCompleted) return;

        Player player = currentLevel.getPlayer();
        if (player == null || !player.isAlive()) return;

        // Apply gravity and movement
        player.updatePhysics(delta, currentLevel.getGravity());

        // Check collisions
        handleCollisions(player);

        // Check death threshold (fell off the map)
        if (player.getY() < deathYThreshold) {
            Gdx.app.log("GameWorld", "Player fell below threshold");
            playerDead = true;
        }
    }

    private void handleCollisions(Player player) {
        Rectangle playerBounds = player.getBounds();
        boolean foundGround = false;

        // First pass: collect all solid collisions for proper resolution
        for (Entity entity : currentLevel.getEntities()) {
            if (entity == player) continue;

            Rectangle entityBounds = entity.getBounds();
            if (!playerBounds.overlaps(entityBounds)) continue;

            if (entity instanceof Obstacle) {
                Obstacle obs = (Obstacle) entity;

                if (obs.getType() == Obstacle.ObstacleType.KILLER) {
                    // Touching a spike kills instantly
                    Gdx.app.log("GameWorld", "Player hit spike at x=" + obs.getX());
                    playerDead = true;
                    return;
                } else if (obs.getType() == Obstacle.ObstacleType.SOLID) {
                    // Resolve solid collision
                    foundGround = resolveSolidCollision(player, playerBounds, obs, entityBounds) || foundGround;
                }
            } else if (entity instanceof EndFlag) {
                // Reached the end!
                Gdx.app.log("GameWorld", "Player reached end flag!");
                levelCompleted = true;
                return;
            }
        }

        // Update ground state
        if (!foundGround && player.getVy() < 0) {
            // Falling and not on any ground
            player.setOnGround(false);
        }
    }

    /**
     * Resolves collision with a solid obstacle.
     * Returns true if the player is standing on this obstacle.
     */
    private boolean resolveSolidCollision(Player player, Rectangle playerBounds, Obstacle obs, Rectangle obsBounds) {
        // Calculate overlap amounts
        float overlapLeft = (playerBounds.x + playerBounds.width) - obsBounds.x;
        float overlapRight = (obsBounds.x + obsBounds.width) - playerBounds.x;
        float overlapTop = (playerBounds.y + playerBounds.height) - obsBounds.y;
        float overlapBottom = (obsBounds.y + obsBounds.height) - playerBounds.y;

        // Find the smallest overlap to determine collision direction
        float minOverlapX = Math.min(overlapLeft, overlapRight);
        float minOverlapY = Math.min(overlapTop, overlapBottom);

        // Small tolerance for landing detection
        float landingTolerance = 10f;

        if (minOverlapY < minOverlapX) {
            // Vertical collision
            if (overlapBottom < overlapTop && player.getVy() < 0) {
                // Landing on top of obstacle
                player.setY(obsBounds.y + obsBounds.height);
                player.setOnGround(true);
                return true;
            } else if (overlapTop < overlapBottom && player.getVy() > 0) {
                // Hitting head on bottom of obstacle
                player.setY(obsBounds.y - playerBounds.height);
                player.setVy(0);
            }
        } else {
            // Horizontal collision
            if (overlapLeft < overlapRight) {
                // Hitting right side of player against left side of obstacle
                // In Geometry Dash, this typically means death (running into a wall)
                if (minOverlapX > landingTolerance) {
                    Gdx.app.log("GameWorld", "Player hit wall (right collision)");
                    playerDead = true;
                }
            } else {
                // Hitting left side of player (rare in auto-runner)
                player.setX(obsBounds.x + obsBounds.width);
            }
        }

        return false;
    }

    public boolean isLevelCompleted() {
        return levelCompleted;
    }

    public boolean isPlayerDead() {
        return playerDead;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }
}
