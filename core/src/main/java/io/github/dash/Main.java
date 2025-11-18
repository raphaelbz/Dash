package io.github.dash;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture cubeTexture;

    private PlayerCube player;

    @Override
    public void create () {
        batch = new SpriteBatch();

        // pour l’instant on réutilise l’image "libgdx.png" comme cube
        cubeTexture = new Texture("libgdx.png");

        // cube de taille 64x64, placé au-dessus du sol
        player = new PlayerCube(100f, PhysicsConfig.GROUND_Y, 64f);

        // Gestion de l'entrée : espace pour sauter
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.SPACE) {
                    player.jump();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void render () {
        float delta = Gdx.graphics.getDeltaTime();

        // mettre à jour la physique du cube
        player.update(delta);

        // effacer l'écran (ici noir)
        ScreenUtils.clear(0, 0, 0, 1);

        // dessiner le cube
        batch.begin();
        batch.draw(
            cubeTexture,
            player.getX(),
            player.getY(),
            player.getWidth(),
            player.getHeight()
        );
        batch.end();
    }

    @Override
    public void dispose () {
        batch.dispose();
        cubeTexture.dispose();
    }
}

