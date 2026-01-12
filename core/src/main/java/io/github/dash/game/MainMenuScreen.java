package io.github.dash.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * The main menu screen with polished visuals and navigation.
 */
public class MainMenuScreen extends ScreenAdapter {
    private final GeometryDashGame game;
    private BitmapFont titleFont;
    private BitmapFont menuFont;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private GlyphLayout glyphLayout;

    private int selectedOption = 0;
    private final String[] menuOptions = {"PLAY", "EXIT"};
    private float animationTime = 0;

    // Colors for the gradient background
    private final Color bgColor1 = new Color(0.05f, 0.05f, 0.15f, 1);
    private final Color bgColor2 = new Color(0.1f, 0.05f, 0.2f, 1);

    public MainMenuScreen(GeometryDashGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        titleFont = new BitmapFont();
        titleFont.getData().setScale(4f);
        titleFont.setColor(Color.CYAN);

        menuFont = new BitmapFont();
        menuFont.getData().setScale(2f);

        shapeRenderer = new ShapeRenderer();
        glyphLayout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        animationTime += delta;

        // Handle input
        handleInput();

        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background gradient
        drawBackground();

        // Draw decorative elements
        drawDecorations();

        // Draw title and menu
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Draw title with glow effect
        String title = "GEOMETRY DASH";
        glyphLayout.setText(titleFont, title);
        float titleX = (1280 - glyphLayout.width) / 2;
        float titleY = 550;

        // Pulsing color effect
        float pulse = (float) (Math.sin(animationTime * 3) * 0.3 + 0.7);
        titleFont.setColor(pulse, 1f, 1f, 1f);
        titleFont.draw(game.batch, title, titleX, titleY);

        // Draw subtitle
        menuFont.setColor(0.7f, 0.7f, 0.7f, 1f);
        String subtitle = "Claude Edition";
        glyphLayout.setText(menuFont, subtitle);
        menuFont.draw(game.batch, subtitle, (1280 - glyphLayout.width) / 2, titleY - 60);

        // Draw menu options
        float menuStartY = 350;
        for (int i = 0; i < menuOptions.length; i++) {
            if (i == selectedOption) {
                // Selected option - animate and highlight
                float bounce = (float) Math.sin(animationTime * 5) * 5;
                menuFont.setColor(1f, 0.8f, 0f, 1f);
                glyphLayout.setText(menuFont, "> " + menuOptions[i] + " <");
                menuFont.draw(game.batch, "> " + menuOptions[i] + " <",
                    (1280 - glyphLayout.width) / 2, menuStartY - i * 60 + bounce);
            } else {
                menuFont.setColor(0.6f, 0.6f, 0.6f, 1f);
                glyphLayout.setText(menuFont, menuOptions[i]);
                menuFont.draw(game.batch, menuOptions[i],
                    (1280 - glyphLayout.width) / 2, menuStartY - i * 60);
            }
        }

        // Draw controls hint
        menuFont.getData().setScale(1f);
        menuFont.setColor(0.4f, 0.4f, 0.4f, 1f);
        String controls = "UP/DOWN to select - ENTER to confirm - SPACE to jump in game";
        glyphLayout.setText(menuFont, controls);
        menuFont.draw(game.batch, controls, (1280 - glyphLayout.width) / 2, 80);
        menuFont.getData().setScale(2f);

        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            selectedOption = (selectedOption - 1 + menuOptions.length) % menuOptions.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            selectedOption = (selectedOption + 1) % menuOptions.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            selectOption();
        }
    }

    private void selectOption() {
        switch (selectedOption) {
            case 0: // PLAY - Go directly to mapRaf level
                game.setScreen(new GameScreen(game, "maps/mapraf.tmx"));
                break;
            case 1: // EXIT
                Gdx.app.exit();
                break;
        }
    }

    private void drawBackground() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Gradient background
        shapeRenderer.rect(0, 0, 1280, 720, bgColor1, bgColor1, bgColor2, bgColor2);

        shapeRenderer.end();
    }

    private void drawDecorations() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw animated floating squares (like Geometry Dash)
        for (int i = 0; i < 15; i++) {
            float x = (i * 100 + animationTime * 30) % 1400 - 60;
            float y = (float) (Math.sin(animationTime + i) * 50 + 150 + i * 30);
            float size = 20 + i % 3 * 10;
            float alpha = 0.1f + (i % 5) * 0.02f;

            shapeRenderer.setColor(0.3f, 0.3f, 0.5f, alpha);
            shapeRenderer.rect(x, y, size, size);
        }

        // Draw ground line
        shapeRenderer.setColor(0.3f, 0.3f, 0.4f, 1f);
        shapeRenderer.rect(0, 0, 1280, 3);

        // Draw some geometric patterns at the bottom
        shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 0.5f);
        for (int i = 0; i < 40; i++) {
            float x = i * 32;
            shapeRenderer.rect(x, 3, 30, 30);
        }

        shapeRenderer.end();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (titleFont != null) titleFont.dispose();
        if (menuFont != null) menuFont.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
