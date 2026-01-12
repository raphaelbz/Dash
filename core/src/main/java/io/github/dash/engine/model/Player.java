package io.github.dash.engine.model;

/**
 * Joueur avec physique style Geometry Dash.
 */
public class Player extends DynamicEntity {
    private boolean alive = true;
    private boolean onGround = false;

    // Physique du saut
    private static final float RISE_GRAVITY_MULTIPLIER = 2f;
    private static final float FALL_GRAVITY_MULTIPLIER = 2.8f;
    private static final float MAX_FALL_SPEED = -2800f;

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
            if (onJumpCallback != null) {
                onJumpCallback.run();
            }
        }
    }

    @Override
    public void updatePhysics(float delta, float gravity) {
        float effectiveGravity = gravity;
        if (vy > 0) {
            effectiveGravity *= RISE_GRAVITY_MULTIPLIER;
        } else if (vy < 0) {
            effectiveGravity *= FALL_GRAVITY_MULTIPLIER;
        }

        vy += effectiveGravity * delta;

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

    public boolean isAlive() { return alive; }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
        if (onGround) vy = 0;
    }

    public boolean isOnGround() { return onGround; }
}
