package io.github.dash.engine.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

/**
 * Monde du jeu: gere la physique et les collisions.
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

        player.updatePhysics(delta, currentLevel.getGravity());
        handleCollisions(player);

        if (player.getY() < deathYThreshold) {
            Gdx.app.log("GameWorld", "Chute mortelle");
            playerDead = true;
        }
    }

    private void handleCollisions(Player player) {
        Rectangle playerBounds = player.getBounds();
        boolean foundGround = false;

        for (Entity entity : currentLevel.getEntities()) {
            if (entity == player) continue;

            Rectangle entityBounds = entity.getBounds();
            if (!playerBounds.overlaps(entityBounds)) continue;

            if (entity instanceof Obstacle) {
                Obstacle obs = (Obstacle) entity;

                if (obs.getType() == Obstacle.ObstacleType.KILLER) {
                    Gdx.app.log("GameWorld", "Touche un spike");
                    playerDead = true;
                    return;
                } else if (obs.getType() == Obstacle.ObstacleType.SOLID) {
                    foundGround = resolveSolidCollision(player, playerBounds, obs, entityBounds) || foundGround;
                }
            } else if (entity instanceof EndFlag) {
                Gdx.app.log("GameWorld", "Niveau termine!");
                levelCompleted = true;
                return;
            }
        }

        if (!foundGround && player.getVy() < 0) {
            player.setOnGround(false);
        }
    }

    private boolean resolveSolidCollision(Player player, Rectangle playerBounds, Obstacle obs, Rectangle obsBounds) {
        float overlapLeft = (playerBounds.x + playerBounds.width) - obsBounds.x;
        float overlapRight = (obsBounds.x + obsBounds.width) - playerBounds.x;
        float overlapTop = (playerBounds.y + playerBounds.height) - obsBounds.y;
        float overlapBottom = (obsBounds.y + obsBounds.height) - playerBounds.y;

        float minOverlapX = Math.min(overlapLeft, overlapRight);
        float minOverlapY = Math.min(overlapTop, overlapBottom);
        float landingTolerance = 10f;

        if (minOverlapY < minOverlapX) {
            if (overlapBottom < overlapTop && player.getVy() < 0) {
                player.setY(obsBounds.y + obsBounds.height);
                player.setOnGround(true);
                return true;
            } else if (overlapTop < overlapBottom && player.getVy() > 0) {
                player.setY(obsBounds.y - playerBounds.height);
                player.setVy(0);
            }
        } else {
            if (overlapLeft < overlapRight) {
                if (minOverlapX > landingTolerance) {
                    Gdx.app.log("GameWorld", "Collision mur");
                    playerDead = true;
                }
            } else {
                player.setX(obsBounds.x + obsBounds.width);
            }
        }

        return false;
    }

    public boolean isLevelCompleted() { return levelCompleted; }
    public boolean isPlayerDead() { return playerDead; }
    public Level getCurrentLevel() { return currentLevel; }
}
