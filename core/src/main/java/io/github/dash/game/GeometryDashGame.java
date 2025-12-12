package io.github.dash.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * The main game class that manages the SpriteBatch and screens.
 */
public class GeometryDashGame extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // We will set the screen to MainMenuScreen later when it is created.
        // For now, we can leave it empty or set it if we create the screen in the same step.
        // To avoid compilation errors, I will comment this out until MainMenuScreen is ready.
        // setScreen(new MainMenuScreen(this));
        
        // Actually, I should create MainMenuScreen next, so I'll add the import and call 
        // assuming I'll create it immediately after.
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        super.dispose();
    }
}
