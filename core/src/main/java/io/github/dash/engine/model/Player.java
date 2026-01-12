package io.github.dash.engine.model;

/**
 * The player character with Geometry Dash-style jump physics.
 */
public class Player extends DynamicEntity {
    private boolean alive = true;
    private boolean onGround = false;

    // Geometry Dash style physics
    private static final float FALL_GRAVITY_MULTIPLIER = 1.8f;  // Faster falling
    private static final float MAX_FALL_SPEED = -2500f;         // Terminal velocity

    // Jump callback for sound
    private Runnable onJumpCallback;

    public Player(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    public void setOnJumpCallback(Runnable callback) {
        this.onJumpCallback = callback;
    }

    public void jump(float jumpForce) {
        if (onGround && alive) {
            vy = jumpForce;
            onGround = false;

            // Trigger jump callback (for sound)
            if (onJumpCallback != null) {
                onJumpCallback.run();
            }
        }
    }

    /**
     * Updates physics with Geometry Dash style gravity.
     * Falling is faster than rising for a snappier feel.
     */
    @Override
    public void updatePhysics(float delta, float gravity) {
        // Apply stronger gravity when falling for snappier descent
        float effectiveGravity = gravity;
        if (vy < 0) {
            effectiveGravity *= FALL_GRAVITY_MULTIPLIER;
        }

        vy += effectiveGravity * delta;

        // Clamp fall speed
        if (vy < MAX_FALL_SPEED) {
            vy = MAX_FALL_SPEED;
        }

        x += vx * delta;
        y += vy * delta;
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
