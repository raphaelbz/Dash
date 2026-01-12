package io.github.dash.engine.model;

/**
 * Entite dynamique soumise a la physique.
 */
public abstract class DynamicEntity extends Entity {
    protected float vx, vy;

    public DynamicEntity(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    public void updatePhysics(float delta, float gravity) {
        vy += gravity * delta;
        x += vx * delta;
        y += vy * delta;
    }

    public void setVelocity(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public float getVy() { return vy; }
    public void setVy(float vy) { this.vy = vy; }
}
