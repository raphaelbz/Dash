package io.github.dash.engine.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import io.github.dash.engine.model.EndFlag;
import io.github.dash.engine.model.Entity;
import io.github.dash.engine.model.GameWorld;
import io.github.dash.engine.model.Obstacle;
import io.github.dash.engine.model.Player;

/**
 * Renders the game world.
 */
public class WorldRenderer {
    private final GameWorld gameWorld;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private final ShapeRenderer shapeRenderer;

    public WorldRenderer(GameWorld gameWorld, OrthographicCamera camera, SpriteBatch batch, TiledMap tiledMap) {
        this.gameWorld = gameWorld;
        this.camera = camera;
        this.batch = batch;
        this.tiledMap = tiledMap;
        this.shapeRenderer = new ShapeRenderer();

        if (tiledMap != null) {
            this.tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        }
    }

    public void render() {
        // Render Tiled Map
        if (tiledMapRenderer != null) {
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
        }

        // Render Entities
        // For MVP, using ShapeRenderer for entities if no sprites are set up, 
        // or we can use the batch if we had textures.
        // Instructions say: "For MVP, you can draw simple colored rectangles using ShapeRenderer OR use a 1x1 white texture"
        // Let's use ShapeRenderer for clarity and simplicity as we don't have textures for everything yet.
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity entity : gameWorld.getCurrentLevel().getEntities()) {
            if (entity instanceof Player) {
                shapeRenderer.setColor(Color.YELLOW);
            } else if (entity instanceof Obstacle) {
                Obstacle obs = (Obstacle) entity;
                if (obs.getType() == Obstacle.ObstacleType.KILLER) {
                    shapeRenderer.setColor(Color.RED);
                } else {
                    shapeRenderer.setColor(Color.GRAY);
                }
            } else if (entity instanceof EndFlag) {
                shapeRenderer.setColor(Color.GREEN);
            } else {
                shapeRenderer.setColor(Color.WHITE);
            }

            shapeRenderer.rect(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
        }

        shapeRenderer.end();
    }
    
    public void dispose() {
        if (tiledMapRenderer != null) tiledMapRenderer.dispose();
        shapeRenderer.dispose();
    }
}
