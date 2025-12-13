package io.github.dash.engine.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.dash.engine.model.GameWorld;

/**
 * Controls the game flow and updates.
 */
public class GameController {
    private final GameWorld gameWorld;
    private final InputController inputController;
    private final OrthographicCamera camera;

    public GameController(GameWorld gameWorld, OrthographicCamera camera) {
        this.gameWorld = gameWorld;
        this.camera = camera;
        this.inputController = new InputController(gameWorld.getCurrentLevel().getPlayer());
        Gdx.input.setInputProcessor(inputController);
    }

    public void update(float delta) {
        // Auto-scroll: Move player right
        if (gameWorld.getCurrentLevel().getPlayer() != null) {
             // We can either move the player or the world.
             // Instructions say: "Player moves automatically to the right."
             // So we update player's velocity X or position X.
             // Let's set a constant velocity X for the player.
             gameWorld.getCurrentLevel().getPlayer().setVelocity(
                 gameWorld.getCurrentLevel().getScrollSpeed() * 10f, // Scale up a bit? 5.0 might be slow in pixels/sec if not scaled
                 gameWorld.getCurrentLevel().getPlayer().getVy()
             );
        }

        gameWorld.update(delta);

        // Update camera to follow player
        if (gameWorld.getCurrentLevel().getPlayer() != null) {
            camera.position.x = gameWorld.getCurrentLevel().getPlayer().getX() + 200; // Keep player slightly to left
            camera.update();
        }
    }
}
