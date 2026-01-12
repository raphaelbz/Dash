package io.github.dash.engine.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import io.github.dash.engine.model.Entity;
import io.github.dash.engine.model.GameWorld;
import io.github.dash.engine.model.Player;

/**
 * Rendu du monde de jeu.
 */
public class WorldRenderer {
    private final GameWorld gameWorld;
    private final OrthographicCamera camera;
    private final TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private final ShapeRenderer shapeRenderer;

    public WorldRenderer(GameWorld gameWorld, OrthographicCamera camera, SpriteBatch batch, TiledMap tiledMap) {
        this.gameWorld = gameWorld;
        this.camera = camera;
        this.tiledMap = tiledMap;
        this.shapeRenderer = new ShapeRenderer();

        if (tiledMap != null) {
            this.tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        } else {
            Gdx.app.log("WorldRenderer", "TiledMap null");
        }
    }

    public void render() {
        if (tiledMapRenderer != null) {
            camera.update();
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
        }

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity entity : gameWorld.getCurrentLevel().getEntities()) {
            if (entity instanceof Player) {
                shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.rect(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
            }
        }

        shapeRenderer.end();
    }

    public void dispose() {
        if (tiledMapRenderer != null) tiledMapRenderer.dispose();
        shapeRenderer.dispose();
    }
}
