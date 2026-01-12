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
import io.github.dash.engine.model.EndFlag;
import io.github.dash.engine.model.Entity;
import io.github.dash.engine.model.GameWorld;
import io.github.dash.engine.tiled.TiledLevelLoader;
import io.github.dash.engine.view.WorldRenderer;

/**
 * Ecran de jeu principal.
 */
public class GameScreen extends ScreenAdapter {
    private final GeometryDashGame game;
    private final String levelPath;

    private GameWorld gameWorld;
    private GameController gameController;
    private WorldRenderer worldRenderer;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;

    private BitmapFont font;
    private GlyphLayout glyphLayout;

    private float deathTimer = 0;
    private float victoryTimer = 0;
    private boolean showingDeath = false;
    private boolean showingVictory = false;
    private static final float DEATH_DELAY = 1.0f;
    private static final float VICTORY_DELAY = 2.0f;

    private float startX = 0;
    private float endX = 5000;

    public GameScreen(GeometryDashGame game, String levelPath) {
        this.game = game;
        this.levelPath = levelPath;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 9000, 5000);

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, 1280, 720);

        font = new BitmapFont();
        font.getData().setScale(2f);
        glyphLayout = new GlyphLayout();

        LevelManager levelManager = new LevelManager();
        TiledLevelLoader.LoadedLevel loadedLevel = levelManager.loadLevel(levelPath);

        gameWorld = new GameWorld(loadedLevel.level);

        if (gameWorld.getCurrentLevel().getPlayer() != null) {
            startX = gameWorld.getCurrentLevel().getPlayer().getX();
        }

        endX = startX + 5000;
        for (Entity entity : gameWorld.getCurrentLevel().getEntities()) {
            if (entity instanceof EndFlag) {
                endX = entity.getX();
                break;
            }
        }

        gameController = new GameController(gameWorld, camera);
        worldRenderer = new WorldRenderer(gameWorld, camera, game.batch, loadedLevel.map);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        if (showingDeath) {
            deathTimer += delta;
            if (deathTimer >= DEATH_DELAY) {
                game.setScreen(new GameScreen(game, levelPath));
                return;
            }
            renderDeathScreen();
            return;
        }

        if (showingVictory) {
            victoryTimer += delta;
            if (victoryTimer >= VICTORY_DELAY) {
                game.setScreen(new MainMenuScreen(game));
                return;
            }
            renderVictoryScreen();
            return;
        }

        gameController.update(delta);

        if (gameWorld.isPlayerDead()) {
            showingDeath = true;
            deathTimer = 0;
        }

        if (gameWorld.isLevelCompleted()) {
            showingVictory = true;
            victoryTimer = 0;
        }

        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);
        worldRenderer.render();
        renderHUD();
    }

    private void renderHUD() {
        game.batch.setProjectionMatrix(hudCamera.combined);
        game.batch.begin();

        float playerX = gameWorld.getCurrentLevel().getPlayer() != null ?
            gameWorld.getCurrentLevel().getPlayer().getX() : startX;
        float progress = Math.min(100, Math.max(0, (playerX - startX) / (endX - startX) * 100));

        font.setColor(Color.WHITE);
        font.draw(game.batch, String.format("Progression: %.0f%%", progress), 20, 700);

        font.setColor(0.7f, 0.7f, 0.7f, 1f);
        String levelName = gameWorld.getCurrentLevel().getLevelName();
        if (levelName != null) {
            font.draw(game.batch, levelName, 20, 660);
        }

        font.getData().setScale(1f);
        font.setColor(0.5f, 0.5f, 0.5f, 1f);
        font.draw(game.batch, "ESPACE pour sauter | ESC pour quitter", 20, 30);
        font.getData().setScale(2f);

        game.batch.end();
    }

    private void renderDeathScreen() {
        ScreenUtils.clear(0.15f, 0.05f, 0.05f, 1);
        worldRenderer.render();

        game.batch.setProjectionMatrix(hudCamera.combined);
        game.batch.begin();

        float flash = (float) Math.abs(Math.sin(deathTimer * 10));
        font.setColor(1f, flash * 0.3f, flash * 0.3f, 1f);
        font.getData().setScale(4f);

        String deathText = "MORT!";
        glyphLayout.setText(font, deathText);
        font.draw(game.batch, deathText, (1280 - glyphLayout.width) / 2, 400);

        font.getData().setScale(1.5f);
        font.setColor(0.7f, 0.7f, 0.7f, 1f);
        String restartText = "Redemarrage...";
        glyphLayout.setText(font, restartText);
        font.draw(game.batch, restartText, (1280 - glyphLayout.width) / 2, 320);

        font.getData().setScale(2f);
        game.batch.end();
    }

    private void renderVictoryScreen() {
        ScreenUtils.clear(0.05f, 0.15f, 0.05f, 1);
        worldRenderer.render();

        game.batch.setProjectionMatrix(hudCamera.combined);
        game.batch.begin();

        float pulse = (float) (Math.sin(victoryTimer * 5) * 0.2 + 0.8);
        font.setColor(pulse, 1f, pulse, 1f);
        font.getData().setScale(4f);

        String victoryText = "NIVEAU TERMINE!";
        glyphLayout.setText(font, victoryText);
        font.draw(game.batch, victoryText, (1280 - glyphLayout.width) / 2, 420);

        font.getData().setScale(2f);
        font.setColor(1f, 1f, 0.5f, 1f);
        String congratsText = "Bravo!";
        glyphLayout.setText(font, congratsText);
        font.draw(game.batch, congratsText, (1280 - glyphLayout.width) / 2, 340);

        font.getData().setScale(1.5f);
        font.setColor(0.7f, 0.7f, 0.7f, 1f);
        String returnText = "Retour au menu...";
        glyphLayout.setText(font, returnText);
        font.draw(game.batch, returnText, (1280 - glyphLayout.width) / 2, 280);

        font.getData().setScale(2f);
        game.batch.end();
    }

    @Override
    public void dispose() {
        if (gameController != null) gameController.dispose();
        if (worldRenderer != null) worldRenderer.dispose();
        if (font != null) font.dispose();
    }
}
