package io.github.dash;

public class PhysicsConfig {

    // vitesse horizontale du cube
    public static final float FORWARD_SPEED = 300f;

    // gravité
    public static final float GRAVITY = -1000f;

    // vitesse verticale initiale quand on saute
    public static final float JUMP_SPEED = 100f;

    // position du sol
    public static final float GROUND_Y = 100f;

    // vitesse de chute max
    public static final float MAX_FALL_SPEED = -2500f;

    private PhysicsConfig() {
        // on ne veut pas instancier cette classe
    }
}
