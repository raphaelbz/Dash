package io.github.dash;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture cubeTexture;
    private PlayerCube player;
    private OrthographicCamera camera;
    private Rectangle spikeRect;

    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(
            false,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
        camera.position.set(
            Gdx.graphics.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f,
            0
        );
        camera.update();

        cubeTexture = new Texture("libgdx.png"); // tu pourras mettre "cube.png" plus tard
        player = new PlayerCube(100f, PhysicsConfig.GROUND_Y, 64f);

        spikeRect = new Rectangle(
            600f,
            PhysicsConfig.GROUND_Y,
            60f,
            80f
        );
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // gestion de l'entrée : espace pour sauter
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.jump();
        }

        // physique du joueur
        player.update(delta);

        // collision joueur / pique
        Rectangle playerRect = new Rectangle(
            player.getX(),
            player.getY(),
            player.getWidth(),
            player.getHeight()
        );
        if (playerRect.overlaps(spikeRect)) {
            resetPlayer();
        }

        // la caméra suit le joueur en X
        camera.position.x = player.getX() + player.getWidth() / 2f;
        camera.update();

        ScreenUtils.clear(0, 0, 0, 1);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // sol simple (bande horizontale)
        batch.draw(
            cubeTexture,
            camera.position.x - Gdx.graphics.getWidth() / 2f,
            PhysicsConfig.GROUND_Y - 10,
            Gdx.graphics.getWidth(),
            10
        );

        // joueur
        batch.draw(
            cubeTexture,
            player.getX(),
            player.getY(),
            player.getWidth(),
            player.getHeight()
        );

        // pique
        batch.draw(
            cubeTexture,
            spikeRect.x,
            spikeRect.y,
            spikeRect.width,
            spikeRect.height
        );

        batch.end();
    }

    private void resetPlayer() {
        player = new PlayerCube(100f, PhysicsConfig.GROUND_Y, 64f);
    }

    @Override
    public void dispose() {
        batch.dispose();
        cubeTexture.dispose();
    }
}
