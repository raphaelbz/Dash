package io.github.dash.engine.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import io.github.dash.engine.model.Player;

/**
 * Handles user input.
 */
public class InputController extends InputAdapter {
    private final Player player;
    private final float jumpForce = 500f; // Slower jump

    public InputController(Player player) {
        this.player = player;
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
}
