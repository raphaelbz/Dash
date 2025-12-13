package io.github.dash.engine.model;

/**
 * The player character.
 */
public class Player extends DynamicEntity {
    private boolean alive = true;
    private boolean onGround = false;

    public Player(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    public void jump(float jumpForce) {
        if (onGround && alive) {
            vy = jumpForce;
            onGround = false;
        }
    }

    public void kill() {
        alive = false;
        vx = 0;
        vy = 0;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
        if (onGround) {
            vy = 0;
        }
    }
    
    public boolean isOnGround() {
        return onGround;
    }
}
