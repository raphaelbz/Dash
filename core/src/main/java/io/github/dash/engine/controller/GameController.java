package io.github.dash.engine.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.dash.engine.model.GameWorld;
import io.github.dash.engine.model.Player;

/**
 * Controleur principal: auto-scroll et camera.
 */
public class GameController {
    private final GameWorld gameWorld;
    private final InputController inputController;
    private final OrthographicCamera camera;

    private static final float SPEED_MULTIPLIER = 5.0f;

    public GameController(GameWorld gameWorld, OrthographicCamera camera) {
        this.gameWorld = gameWorld;
        this.camera = camera;

        Player player = gameWorld.getCurrentLevel().getPlayer();
        this.inputController = new InputController(player);
        Gdx.input.setInputProcessor(inputController);

        if (player != null) {
            camera.position.x = player.getX() + camera.viewportWidth / 3;
            camera.position.y = player.getY() + camera.viewportHeight / 4;
            camera.update();
        }
    }

    public void update(float delta) {
        Player player = gameWorld.getCurrentLevel().getPlayer();

        if (player != null && player.isAlive()) {
            float scrollSpeed = gameWorld.getCurrentLevel().getScrollSpeed() * SPEED_MULTIPLIER;
            player.setVelocity(scrollSpeed, player.getVy());
        }

        gameWorld.update(delta);

        if (player != null) {
            float targetX = player.getX() + camera.viewportWidth / 3;
            float targetY = Math.max(camera.viewportHeight / 2, player.getY() + camera.viewportHeight / 4);

            float lerpFactor = 5.0f * delta;
            camera.position.x += (targetX - camera.position.x) * lerpFactor;
            camera.position.y += (targetY - camera.position.y) * lerpFactor * 0.5f;
            camera.update();
        }
    }

    public GameWorld getGameWorld() { return gameWorld; }

    public void dispose() {
        if (inputController != null) inputController.dispose();
    }
}
