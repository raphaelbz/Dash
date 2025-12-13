package io.github.dash.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.dash.engine.controller.GameController;
import io.github.dash.engine.controller.LevelManager;
import io.github.dash.engine.model.GameWorld;
import io.github.dash.engine.tiled.TiledLevelLoader;
import io.github.dash.engine.view.WorldRenderer;

/**
 * The main game screen.
 */
public class GameScreen extends ScreenAdapter {
    private final GeometryDashGame game;
    private final String levelPath;
    
    private GameWorld gameWorld;
    private GameController gameController;
    private WorldRenderer worldRenderer;
    private OrthographicCamera camera;

    public GameScreen(GeometryDashGame game, String levelPath) {
        this.game = game;
        this.levelPath = levelPath;
    }

    @Override
    public void show() {
        // Initialize Camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 450); // Fixed viewport size
        
        // Load Level
        LevelManager levelManager = new LevelManager();
        TiledLevelLoader.LoadedLevel loadedLevel = levelManager.loadLevel(levelPath);
        
        // Initialize World
        gameWorld = new GameWorld(loadedLevel.level);
        
        // Initialize Controller
        gameController = new GameController(gameWorld, camera);
        
        // Initialize Renderer
        worldRenderer = new WorldRenderer(gameWorld, camera, game.batch, loadedLevel.map);
    }

    @Override
    public void render(float delta) {
        // Update Logic
        gameController.update(delta);
        
        // Check Game State
        if (gameWorld.isPlayerDead()) {
            // Restart Level
            game.setScreen(new GameScreen(game, levelPath));
            return;
        }
        
        if (gameWorld.isLevelCompleted()) {
            // Go back to Menu (MVP)
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        // Render
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
        worldRenderer.render();
    }

    @Override
    public void resize(int width, int height) {
        // We could update viewport here if we used a Viewport class
    }

    @Override
    public void dispose() {
        if (worldRenderer != null) worldRenderer.dispose();
    }
}
