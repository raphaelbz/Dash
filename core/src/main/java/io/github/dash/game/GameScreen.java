package io.github.dash.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.dash.engine.controller.GameController;
import io.github.dash.engine.controller.LevelManager;
import io.github.dash.engine.model.GameWorld;
import io.github.dash.engine.tiled.TiledLevelLoader;
import io.github.dash.engine.view.WorldRenderer;

/**
 * The main game screen where gameplay happens.
 */
public class GameScreen extends ScreenAdapter {
    private final GeometryDashGame game;
    private final String levelPath;

    private GameWorld gameWorld;
    private GameController gameController;
    private WorldRenderer worldRenderer;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera; // Fixed camera for HUD

    // UI elements
    private BitmapFont font;
    private GlyphLayout glyphLayout;

    // Game state
    private float deathTimer = 0;
    private float victoryTimer = 0;
    private boolean showingDeath = false;
    private boolean showingVictory = false;
    private static final float DEATH_DELAY = 1.0f;
    private static final float VICTORY_DELAY = 2.0f;

    // Progress tracking
    private float startX = 0;
    private float endX = 5000; // Will be updated based on level

    public GameScreen(GeometryDashGame game, String levelPath) {
        this.game = game;
        this.levelPath = levelPath;
    }

    @Override
    public void show() {
        // Initialize Camera (follows player) - larger viewport to see more of the level
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 9000, 5000);

        // Initialize HUD Camera (fixed, never moves)
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, 1280, 720);

        // Initialize fonts
        font = new BitmapFont();
        font.getData().setScale(2f);
        glyphLayout = new GlyphLayout();

        // Load Level
        LevelManager levelManager = new LevelManager();
        TiledLevelLoader.LoadedLevel loadedLevel = levelManager.loadLevel(levelPath);

        // Initialize World
        gameWorld = new GameWorld(loadedLevel.level);

        // Store start position for progress calculation
        if (gameWorld.getCurrentLevel().getPlayer() != null) {
            startX = gameWorld.getCurrentLevel().getPlayer().getX();
        }

        // Find EndFlag position for accurate progress calculation
        endX = startX + 5000; // Default fallback
        for (io.github.dash.engine.model.Entity entity : gameWorld.getCurrentLevel().getEntities()) {
            if (entity instanceof io.github.dash.engine.model.EndFlag) {
                endX = entity.getX();
                break;
            }
        }

        // Initialize Controller
        gameController = new GameController(gameWorld, camera);

        // Initialize Renderer
        worldRenderer = new WorldRenderer(gameWorld, camera, game.batch, loadedLevel.map);
    }

    @Override
    public void render(float delta) {
        // Handle ESC to return to menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        // Handle death state
        if (showingDeath) {
            deathTimer += delta;
            if (deathTimer >= DEATH_DELAY) {
                // Restart Level
                game.setScreen(new GameScreen(game, levelPath));
                return;
            }
            renderDeathScreen(delta);
            return;
        }

        // Handle victory state
        if (showingVictory) {
            victoryTimer += delta;
            if (victoryTimer >= VICTORY_DELAY) {
                // Go back to main menu
                game.setScreen(new MainMenuScreen(game));
                return;
            }
            renderVictoryScreen(delta);
            return;
        }

        // Update Logic
        gameController.update(delta);

        // Check Game State
        if (gameWorld.isPlayerDead()) {
            showingDeath = true;
            deathTimer = 0;
        }

        if (gameWorld.isLevelCompleted()) {
            showingVictory = true;
            victoryTimer = 0;
        }

        // Render game
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);
        worldRenderer.render();

        // Render HUD
        renderHUD();
    }

    private void renderHUD() {
        // Use fixed HUD camera so UI stays in place
        game.batch.setProjectionMatrix(hudCamera.combined);
        game.batch.begin();

        // Calculate progress
        float playerX = gameWorld.getCurrentLevel().getPlayer() != null ?
            gameWorld.getCurrentLevel().getPlayer().getX() : startX;
        float progress = Math.min(100, Math.max(0, (playerX - startX) / (endX - startX) * 100));

        // Draw progress
        font.setColor(Color.WHITE);
        String progressText = String.format("Progress: %.0f%%", progress);
        font.draw(game.batch, progressText, 20, 700);

        // Draw level name
        font.setColor(0.7f, 0.7f, 0.7f, 1f);
        String levelName = gameWorld.getCurrentLevel().getLevelName();
        if (levelName != null) {
            font.draw(game.batch, levelName, 20, 660);
        }

        // Draw controls hint
        font.getData().setScale(1f);
        font.setColor(0.5f, 0.5f, 0.5f, 1f);
        font.draw(game.batch, "SPACE to jump | ESC to quit", 20, 30);
        font.getData().setScale(2f);

        game.batch.end();
    }

    private void renderDeathScreen(float delta) {
        // Render frozen game state
        ScreenUtils.clear(0.15f, 0.05f, 0.05f, 1);
        worldRenderer.render();

        // Draw death overlay using fixed HUD camera
        game.batch.setProjectionMatrix(hudCamera.combined);
        game.batch.begin();

        // Flash effect
        float flash = (float) Math.abs(Math.sin(deathTimer * 10));
        font.setColor(1f, flash * 0.3f, flash * 0.3f, 1f);
        font.getData().setScale(4f);

        String deathText = "YOU DIED!";
        glyphLayout.setText(font, deathText);
        font.draw(game.batch, deathText, (1280 - glyphLayout.width) / 2, 400);

        font.getData().setScale(1.5f);
        font.setColor(0.7f, 0.7f, 0.7f, 1f);
        String restartText = "Restarting...";
        glyphLayout.setText(font, restartText);
        font.draw(game.batch, restartText, (1280 - glyphLayout.width) / 2, 320);

        font.getData().setScale(2f);
        game.batch.end();
    }

    private void renderVictoryScreen(float delta) {
        // Render game state with green tint
        ScreenUtils.clear(0.05f, 0.15f, 0.05f, 1);
        worldRenderer.render();

        // Draw victory overlay using fixed HUD camera
        game.batch.setProjectionMatrix(hudCamera.combined);
        game.batch.begin();

        // Pulsing effect
        float pulse = (float) (Math.sin(victoryTimer * 5) * 0.2 + 0.8);
        font.setColor(pulse, 1f, pulse, 1f);
        font.getData().setScale(4f);

        String victoryText = "LEVEL COMPLETE!";
        glyphLayout.setText(font, victoryText);
        font.draw(game.batch, victoryText, (1280 - glyphLayout.width) / 2, 420);

        font.getData().setScale(2f);
        font.setColor(1f, 1f, 0.5f, 1f);
        String congratsText = "Congratulations!";
        glyphLayout.setText(font, congratsText);
        font.draw(game.batch, congratsText, (1280 - glyphLayout.width) / 2, 340);

        font.getData().setScale(1.5f);
        font.setColor(0.7f, 0.7f, 0.7f, 1f);
        String returnText = "Returning to menu...";
        glyphLayout.setText(font, returnText);
        font.draw(game.batch, returnText, (1280 - glyphLayout.width) / 2, 280);

        font.getData().setScale(2f);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Could update viewport here
    }

    @Override
    public void dispose() {
        if (gameController != null) gameController.dispose();
        if (worldRenderer != null) worldRenderer.dispose();
        if (font != null) font.dispose();
    }
}
