package io.github.dash.engine.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import io.github.dash.engine.model.Player;

/**
 * Handles user input and jump sound.
 */
public class InputController extends InputAdapter {
    private final Player player;
    private final float jumpForce = 1600f;
    private Sound jumpSound;

    public InputController(Player player) {
        this.player = player;
        loadJumpSound();

        // Set up jump callback for sound
        player.setOnJumpCallback(() -> playJumpSound());
    }

    private void loadJumpSound() {
        try {
            jumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.wav"));
        } catch (Exception e) {
            Gdx.app.log("InputController", "Could not load jump sound: " + e.getMessage());
            jumpSound = null;
        }
    }

    private void playJumpSound() {
        if (jumpSound != null) {
            jumpSound.play(0.5f);  // Volume at 50%
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            player.jump(jumpForce);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            player.jump(jumpForce);
            return true;
        }
        return false;
    }

    public void dispose() {
        if (jumpSound != null) {
            jumpSound.dispose();
        }
    }
}
