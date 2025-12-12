package io.github.dash.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * The main menu screen.
 */
public class MainMenuScreen extends ScreenAdapter {
    private final GeometryDashGame game;
    private BitmapFont font;

    public MainMenuScreen(GeometryDashGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        game.batch.begin();
        font.draw(game.batch, "Press ENTER to start", Gdx.graphics.getWidth() / 2f - 70, Gdx.graphics.getHeight() / 2f);
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
             game.setScreen(new GameScreen(game, "maps/level1.tmx"));
        }
    }

    @Override
    public void hide() {
        font.dispose();
    }
}
