package io.github.dash;

public class PlayerCube {

    private float x;
    private float y;
    private float width;
    private float height;

    private float vx;       // vitesse horizontale
    private float vy;       // vitesse verticale
    private boolean onGround;

    public PlayerCube(float startX, float startY, float size) {
        this.x = startX;
        this.y = startY;
        this.width = size;
        this.height = size;

        this.vx = PhysicsConfig.FORWARD_SPEED;
        this.vy = 0f;
        this.onGround = true;
    }

    public void update(float delta) {
        //mouvement horizontal constant
        x += vx * delta;

        //la gravité
        vy += PhysicsConfig.GRAVITY * delta;

        // limiter la vitesse de chute
        if (vy < PhysicsConfig.MAX_FALL_SPEED) {
            vy = PhysicsConfig.MAX_FALL_SPEED;
        }

        // mise à jour de la position verticale
        y += vy * delta;

        //collision simple avec le sol
        if (y <= PhysicsConfig.GROUND_Y) {
            y = PhysicsConfig.GROUND_Y;
            vy = 0f;
            onGround = true;
        } else {
            onGround = false;
        }
    }

    public void jump() {
        if (onGround) {
            vy = PhysicsConfig.JUMP_SPEED;
            onGround = false;
        }
    }

    // Getters pour le Main
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
