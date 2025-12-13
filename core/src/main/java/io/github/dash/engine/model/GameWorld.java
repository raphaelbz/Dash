package io.github.dash.engine.model;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

/**
 * Manages the game state and logic.
 */
public class GameWorld {
    private final Level currentLevel;
    private boolean levelCompleted;
    private boolean playerDead;
    private final float deathYThreshold = -50f;

    public GameWorld(Level level) {
        this.currentLevel = level;
    }

    public void update(float delta) {
        if (playerDead || levelCompleted) return;

        Player player = currentLevel.getPlayer();
        if (player == null) return;

        // Apply gravity and movement
        player.updatePhysics(delta, currentLevel.getGravity());

        // Check collisions
        Rectangle playerBounds = player.getBounds();
        boolean onGround = false;

        for (Entity entity : currentLevel.getEntities()) {
            if (entity == player) continue;

            if (entity instanceof Obstacle) {
                Obstacle obs = (Obstacle) entity;
                if (playerBounds.overlaps(obs.getBounds())) {
                    if (obs.getType() == Obstacle.ObstacleType.KILLER) {
                        playerDead = true;
                    } else if (obs.getType() == Obstacle.ObstacleType.SOLID) {
                        // Simple collision resolution: if falling and above, land on it
                        Rectangle obsBounds = obs.getBounds();
                        Rectangle intersection = new Rectangle();
                        Intersector.intersectRectangles(playerBounds, obsBounds, intersection);
                        
                        // Check if landing on top
                        if (intersection.height < intersection.width && player.getY() > obs.getY()) {
                             player.setY(obs.getY() + obs.getHeight());
                             player.setOnGround(true);
                             onGround = true;
                        } else {
                             // Side collision or hitting head - for MVP just kill or block?
                             // Instructions say "Touching a 'killer' obstacle... kills".
                             // For solid blocks, we should probably block movement or kill if running into side.
                             // For MVP auto-runner, running into a wall usually kills you.
                             if (intersection.width < intersection.height) {
                                 playerDead = true; 
                             }
                        }
                    }
                }
            } else if (entity instanceof EndFlag) {
                if (playerBounds.overlaps(entity.getBounds())) {
                    levelCompleted = true;
                }
            }
        }
        
        // Ground check (if no solid block was found)
        if (!onGround) {
             // If we were on ground but now not colliding with anything, we are falling
             // But wait, we set onGround=true only if we collided.
             // We need to persist onGround if we are still on top of something.
             // The simple logic above sets it to true if we collide.
             // If we didn't collide with any solid, onGround should be false.
             player.setOnGround(false);
        }
        
        // Check death threshold
        if (player.getY() < deathYThreshold) {
            playerDead = true;
        }
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
