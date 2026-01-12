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
 * Ecran de selection de niveau.
 */
public class LevelSelectScreen extends ScreenAdapter {
    private final GeometryDashGame game;
    private BitmapFont titleFont;
    private BitmapFont menuFont;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private GlyphLayout glyphLayout;

    private int selectedLevel = 0;
    private float animationTime = 0;

    // Niveaux disponibles: nom, chemin, description
    private static final String[][] LEVELS = {
        {"Niveau 1", "maps/mapRaf.tmx", "Niveau assez facile"},
        {"Niveau 2", "maps/mapMel.tmx", "Niveau avec sauts techniques"},
        {"Niveau 3", "maps/mapRoy.tmx", "Niveau difficile"}
    };

    // Couleurs de fond
    private final Color bgColor1 = new Color(0.02f, 0.08f, 0.15f, 1);
    private final Color bgColor2 = new Color(0.08f, 0.02f, 0.18f, 1);

    public LevelSelectScreen(GeometryDashGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);

        menuFont = new BitmapFont();
        menuFont.getData().setScale(1.5f);

        shapeRenderer = new ShapeRenderer();
        glyphLayout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        animationTime += delta;
        handleInput();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawBackground();
        drawLevelCards();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Titre
        titleFont.setColor(Color.CYAN);
        String title = "SELECT LEVEL";
        glyphLayout.setText(titleFont, title);
        titleFont.draw(game.batch, title, (1280 - glyphLayout.width) / 2, 650);

        // Cartes de niveau
        float cardWidth = 350;
        float cardSpacing = 100;
        float totalWidth = LEVELS.length * cardWidth + (LEVELS.length - 1) * cardSpacing;
        float startX = (1280 - totalWidth) / 2;

        for (int i = 0; i < LEVELS.length; i++) {
            float cardX = startX + i * (cardWidth + cardSpacing);
            float cardCenterX = cardX + cardWidth / 2;

            // Nom du niveau
            if (i == selectedLevel) {
                menuFont.setColor(1f, 0.9f, 0.3f, 1f);
            } else {
                menuFont.setColor(0.8f, 0.8f, 0.8f, 1f);
            }
            menuFont.getData().setScale(2f);
            glyphLayout.setText(menuFont, LEVELS[i][0]);
            menuFont.draw(game.batch, LEVELS[i][0], cardCenterX - glyphLayout.width / 2, 420);

            // Description
            menuFont.getData().setScale(1f);
            if (i == selectedLevel) {
                menuFont.setColor(0.9f, 0.9f, 0.9f, 1f);
            } else {
                menuFont.setColor(0.5f, 0.5f, 0.5f, 1f);
            }
            glyphLayout.setText(menuFont, LEVELS[i][2]);
            menuFont.draw(game.batch, LEVELS[i][2], cardCenterX - glyphLayout.width / 2, 320);
        }

        // Instructions
        menuFont.getData().setScale(1.2f);
        menuFont.setColor(0.4f, 0.4f, 0.4f, 1f);
        String instructions = "GAUCHE/DROITE pour choisir - ENTREE pour jouer - ESC pour retour";
        glyphLayout.setText(menuFont, instructions);
        menuFont.draw(game.batch, instructions, (1280 - glyphLayout.width) / 2, 100);

        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            selectedLevel = (selectedLevel - 1 + LEVELS.length) % LEVELS.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            selectedLevel = (selectedLevel + 1) % LEVELS.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game, LEVELS[selectedLevel][1]));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void drawBackground() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Fond degrade
        shapeRenderer.rect(0, 0, 1280, 720, bgColor1, bgColor1, bgColor2, bgColor2);

        // Particules animees
        shapeRenderer.setColor(0.2f, 0.2f, 0.4f, 0.3f);
        for (int i = 0; i < 20; i++) {
            float x = (i * 70 + animationTime * 20) % 1400 - 60;
            float y = (float) (Math.sin(animationTime * 0.5 + i * 0.5) * 100 + 360);
            float size = 10 + i % 4 * 5;
            shapeRenderer.rect(x, y, size, size);
        }

        shapeRenderer.end();
    }

    private void drawLevelCards() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float cardWidth = 350;
        float cardHeight = 250;
        float cardSpacing = 100;
        float totalWidth = LEVELS.length * cardWidth + (LEVELS.length - 1) * cardSpacing;
        float startX = (1280 - totalWidth) / 2;
        float cardY = 250;

        for (int i = 0; i < LEVELS.length; i++) {
            float cardX = startX + i * (cardWidth + cardSpacing);

            // Effet de selection
            if (i == selectedLevel) {
                float pulse = (float) (Math.sin(animationTime * 4) * 0.05 + 0.25);
                shapeRenderer.setColor(pulse, pulse * 0.8f, pulse * 1.5f, 1f);
                shapeRenderer.setColor(0.3f, 0.5f, 0.8f, 0.3f);
                shapeRenderer.rect(cardX - 5, cardY - 5, cardWidth + 10, cardHeight + 10);
            }

            // Corps de la carte
            if (i == selectedLevel) {
                shapeRenderer.setColor(0.15f, 0.15f, 0.25f, 1f);
            } else {
                shapeRenderer.setColor(0.08f, 0.08f, 0.12f, 1f);
            }
            shapeRenderer.rect(cardX, cardY, cardWidth, cardHeight);

            // Bordure
            shapeRenderer.setColor(i == selectedLevel ? new Color(0.4f, 0.6f, 1f, 1f) : new Color(0.2f, 0.2f, 0.3f, 1f));
            shapeRenderer.rect(cardX, cardY + cardHeight - 3, cardWidth, 3);
            shapeRenderer.rect(cardX, cardY, cardWidth, 3);
            shapeRenderer.rect(cardX, cardY, 3, cardHeight);
            shapeRenderer.rect(cardX + cardWidth - 3, cardY, 3, cardHeight);

            // Zone de preview
            float previewX = cardX + 25;
            float previewY = cardY + 70;
            float previewWidth = cardWidth - 50;
            float previewHeight = 100;

            shapeRenderer.setColor(0.05f, 0.05f, 0.1f, 1f);
            shapeRenderer.rect(previewX, previewY, previewWidth, previewHeight);

            // Mini preview
            if (i == selectedLevel) {
                shapeRenderer.setColor(0.3f, 0.3f, 0.4f, 1f);
            } else {
                shapeRenderer.setColor(0.15f, 0.15f, 0.2f, 1f);
            }
            shapeRenderer.rect(previewX, previewY, previewWidth, 15);

            // Mini obstacles
            shapeRenderer.setColor(i == selectedLevel ? Color.RED : new Color(0.4f, 0.2f, 0.2f, 1f));
            for (int j = 0; j < 5; j++) {
                float obstX = previewX + 30 + j * 50;
                shapeRenderer.triangle(obstX, previewY + 15, obstX + 10, previewY + 35, obstX + 20, previewY + 15);
            }

            // Mini joueur
            shapeRenderer.setColor(i == selectedLevel ? Color.YELLOW : new Color(0.5f, 0.5f, 0.3f, 1f));
            shapeRenderer.rect(previewX + 10, previewY + 15, 15, 15);
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
