package io.github.dash.engine.model;

import com.badlogic.gdx.math.Rectangle;

/**
 * Classe de base pour toutes les entites du jeu.
 */
public abstract class Entity {
    protected float x, y;
    protected float width, height;

    public Entity(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update(float delta) {}

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
}
