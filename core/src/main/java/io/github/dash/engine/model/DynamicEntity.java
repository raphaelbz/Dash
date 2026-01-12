package io.github.dash.engine.model;

/**
 * Base class for entities that move and are affected by physics.
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

    @Override
    public void update(float delta) {
        // By default, dynamic entities might not need gravity if not specified, 
        // but usually we call updatePhysics from the subclass or GameWorld.
    }
    
    public void setVelocity(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
    }
    
    public float getVy() { return vy; }
    public void setVy(float vy) { this.vy = vy; }
}
