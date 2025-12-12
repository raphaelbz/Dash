You are an AI coding assistant working inside a fresh LibGDX Java project generated with the official LibGDX setup tool.

GOAL
-----
Your goal is to implement a minimal but complete MVP of a 2D auto-runner game engine similar to Geometry Dash, with:

- One-button jump.
- Auto-scroll / auto-run toward the right.
- Obstacles and solid blocks.
- Death and restart.
- Level completion through an "end flag".
- Levels defined via Tiled maps (extensible without changing Java code).
- Clear MVC architecture, good OOP design (inheritance, interfaces, abstract classes), and at least one simple design pattern (Factory, Singleton).


IMPORTANT CONSTRAINTS
----------------------
1. Use ONLY Java (no Kotlin).
2. Do NOT modify Gradle configuration except if absolutely necessary.
3. Keep everything compatible with the existing LibGDX project structure.
4. Assume the base package is: com.mygdx.game
5. Follow LibGDX conventions: use Screen, Game, SpriteBatch, OrthographicCamera, etc.
6. The engine must be separated logically (MVC) and extensible via Tiled.
7. If any assets (maps, textures) are missing, implement clean fallbacks: the code must compile and run, even if rendering is minimal.


HIGH-LEVEL ARCHITECTURE
------------------------
Implement a small engine organized as follows:

Packages:
- com.mygdx.game.engine.model      -> game world and entities (Model)
- com.mygdx.game.engine.view       -> rendering code (View)
- com.mygdx.game.engine.controller -> game logic, input, level management (Controller)
- com.mygdx.game.engine.tiled      -> loading from Tiled, JSON/TMX parsing, factories
- com.mygdx.game.game              -> LibGDX Game and Screens that use the engine

Gameplay concept:
- Side-view 2D auto-runner.
- Player moves automatically to the right.
- Player can jump using a single input (SPACE key or left mouse click).
- Obstacles and solid blocks defined in a Tiled map.
- Touching a "killer" obstacle or falling below a certain Y kills the player and restarts the level.
- Reaching an "end flag" entity completes the level. For MVP, just go back to main menu or restart.

TILED INTEGRATION (MVP LEVEL)
------------------------------
Define a convention for Tiled maps (we assume the user will later create them):

- Map is a standard orthogonal tilemap (TMX).
- Tileset defines:
    - solid blocks (ground/platforms)
    - optional background tiles
- One "Objects" layer (object layer) with map objects having a "type" property:
    - type = "PlayerStart"  -> starting position of the player
    - type = "Spike"        -> obstacle that kills on contact
    - type = "Solid"        -> solid obstacle/block
    - type = "EndFlag"      -> marks the end of the level
- Map properties (global properties):
    - scrollSpeed : float (e.g. 5.0)
    - gravity     : float (e.g. -20.0)
    - levelName   : String

For the MVP:
- Implement the loader and engine assuming a TMX file exists at: assets/maps/level1.tmx
- If the file is missing or cannot be loaded:
    - Log a clear message.
    - Fallback to a very simple "hard-coded" level built in Java so the game still runs.


STEP 1: MAIN GAME CLASS AND SCREENS
------------------------------------
1. Transform the default main class (e.g. MyGdxGame) into a proper Game subclass, or create a new one.

- Create: com.mygdx.game.game.GeometryDashGame
    - extends com.badlogic.gdx.Game
    - Fields:
        - public SpriteBatch batch;
    - Methods:
        - create():
            - initialize batch
            - setScreen(new MainMenuScreen(this));
        - render():
            - call super.render();
        - dispose():
            - dispose batch
            - dispose current screen.

- Update desktop launcher to launch GeometryDashGame instead of default.

2. Create Screens:

- MainMenuScreen (com.mygdx.game.game.MainMenuScreen)
    - Implements Screen.
    - Shows a minimal text ("Press ENTER to start") using BitmapFont.
    - On ENTER key:
        - setScreen(new GameScreen(game, "maps/level1.tmx"));

- GameScreen (com.mygdx.game.game.GameScreen)
    - Implements Screen.
    - Fields:
        - GeometryDashGame game;
        - GameWorld gameWorld;
        - GameController gameController;
        - WorldRenderer worldRenderer;
    - Constructor:
        - Takes GeometryDashGame and a level path (String levelPath).
        - Uses a TiledLevelLoader to load a Level from the provided path.
        - Creates GameWorld, GameController, and WorldRenderer.
    - render(float delta):
        - Clear screen.
        - Update controller/world with delta.
        - Render world via WorldRenderer.
        - Handle transitions:
            - If world/gameController reports "player died", restart current level.
            - If world/gameController reports "level completed", go back to MainMenuScreen or reload the level.


STEP 2: MODEL CLASSES (ENGINE.MODEL)
-------------------------------------
Create the following classes in com.mygdx.game.engine.model:

1. Entity (abstract)
    - Implements basic interfaces (you can define them, see below).
    - Fields:
        - float x, y;
        - float width, height;
    - Methods:
        - update(float delta)  -> default empty (can be overridden).
        - Rectangle getBounds() -> returns new Rectangle(x, y, width, height);
        - onCollision(Entity other) -> default empty (override in subclasses as needed).

2. DynamicEntity (abstract) extends Entity
    - Fields:
        - float vx, vy;
    - Methods:
        - updatePhysics(float delta, float gravity):
            - vy += gravity * delta;
            - x += vx * delta;
            - y += vy * delta;
        - update(float delta) can call updatePhysics by default.

3. Player extends DynamicEntity
    - Fields:
        - boolean alive;
        - boolean onGround;
    - Methods:
        - jump(float jumpForce):
            - if onGround, set vy = jumpForce and onGround = false.
        - kill():
            - alive = false;
        - isAlive(): boolean
        - setOnGround(boolean)
    - In update(), apply physics via updatePhysics(delta, gravity from GameWorld) and any player-specific logic.

4. Obstacle extends Entity
    - Fields:
        - enum ObstacleType { SOLID, KILLER }
        - ObstacleType type;
    - Methods:
        - constructor with x, y, w, h, type
        - getters for type.

5. EndFlag extends Entity
    - Simple entity marking the end of the level.

6. Level
    - Fields:
        - List<Entity> entities;
        - Player player;
        - float scrollSpeed;
        - float gravity;
        - String levelName;
    - Methods:
        - getters and setters.
        - methods to add entities.

7. GameWorld
    - Fields:
        - Level currentLevel;
        - boolean levelCompleted;
        - boolean playerDead;
        - float deathYThreshold (= some constant like -50f);
    - Methods:
        - constructor(Level level)
        - update(float delta):
            - update player and all entities.
            - apply auto-scroll (see controller).
            - check collisions between player and obstacles:
                - if intersects KILLER -> playerDead = true.
                - if intersects EndFlag -> levelCompleted = true.
            - check if player fell below deathYThreshold -> playerDead = true.
        - reset():
            - reinitialize player position (based on Level data).
            - reset flags (playerDead, levelCompleted).
        - getters for levelCompleted, playerDead, player, entities.


STEP 3: CONTROLLER CLASSES
---------------------------
Create in com.mygdx.game.engine.controller:

1. InputController
    - Uses LibGDX InputAdapter.
    - Fields:
        - Player player;
        - float jumpForce (e.g. 12f).
    - On keyDown():
        - if SPACE pressed -> player.jump(jumpForce).
    - Optionally also react to touchDown() for mouse / touch.

2. GameController
    - Fields:
        - GameWorld gameWorld;
        - InputController inputController;
        - OrthographicCamera camera;
    - Constructor:
        - takes GameWorld and camera
        - creates InputController with gameWorld.getPlayer()
        - sets Gdx.input.setInputProcessor(inputController)
    - Methods:
        - update(float delta):
            - apply auto-scroll:
                - either move player in +x,
                - or move camera position.x at scrollSpeed.
            - call gameWorld.update(delta).
        - getters for gameWorld.

3. LevelManager
    - For MVP, can be simple:
        - Given a levelPath (String), load a Level via TiledLevelLoader.
    - Later can be extended to handle multiple levels.


STEP 4: VIEW CLASSES (RENDERING)
---------------------------------
Create in com.mygdx.game.engine.view:

1. WorldRenderer
    - Fields:
        - GameWorld gameWorld;
        - OrthographicCamera camera;
        - SpriteBatch batch; // Use game.batch from GeometryDashGame if desired.
        - TiledMap tiledMap;
        - OrthogonalTiledMapRenderer tiledMapRenderer;
    - Constructor:
        - Takes GameWorld, camera, SpriteBatch, and TiledMap.
        - Creates an OrthogonalTiledMapRenderer with the TiledMap and batch.
    - Methods:
        - render():
            - set camera matrices (update() camera, set projection matrix).
            - render TiledMap via tiledMapRenderer.
            - begin SpriteBatch and draw entities:
                - For MVP, you can draw simple colored rectangles using ShapeRenderer OR use a 1x1 white texture scaled to width/height.
                - Draw player (different color).
                - Draw obstacles.
                - Draw EndFlag.
            - end batch.

The MVP does NOT need fancy sprites; minimal shapes are fine as long as it's clear.


STEP 5: TILED LOADING & FACTORY
--------------------------------
Create in com.mygdx.game.engine.tiled:

1. EntityFactory
    - Methods:
        - public static Player createPlayer(float x, float y, float w, float h)
        - public static Obstacle createSolid(float x, float y, float w, float h)
        - public static Obstacle createSpike(float x, float y, float w, float h)
        - public static EndFlag createEndFlag(float x, float y, float w, float h)

2. TiledLevelLoader
    - Fields:
        - AssetManager or direct TmxMapLoader.
    - Methods:
        - public Level load(String tmxPath)
            - Use TmxMapLoader to load TiledMap.
            - Read map properties (scrollSpeed, gravity, levelName).
            - Parse object layer "Objects":
                - For each MapObject:
                    - Get "type" property.
                    - If type == "PlayerStart":
                        - create Player via EntityFactory and set as Level.player.
                    - If type == "Spike":
                        - create KILLER Obstacle.
                    - If type == "Solid":
                        - create SOLID Obstacle.
                    - If type == "EndFlag":
                        - create EndFlag.
                    - Add all created entities to Level.
            - If anything goes wrong (file not found, etc.):
                - Log error.
                - Create a fallback Level:
                    - gravity = -20;
                    - scrollSpeed = 5;
                    - player starting near (0,0);
                    - a simple solid block floor and one EndFlag.
            - Return the Level and keep a reference to the TiledMap somewhere accessible
              (GameScreen can retrieve it to pass to WorldRenderer).

    - Option:
        - TiledLevelLoader can return a small wrapper containing both Level and TiledMap.
          For MVP you can define a simple class:
          class LoadedLevel {
          public final Level level;
          public final TiledMap map;
          }


STEP 6: HOOK EVERYTHING UP
---------------------------
1. In GameScreen:
    - Create an OrthographicCamera with a fixed viewport (e.g. 800x450 world units).
    - Use TiledLevelLoader to load the map and Level (with TiledMap).
    - Instantiate GameWorld with the Level.
    - Instantiate GameController with GameWorld and camera.
    - Instantiate WorldRenderer with GameWorld, camera, game.batch, and the TiledMap.

2. In render(delta):
    - Call GameController.update(delta).
    - Clear screen.
    - Call WorldRenderer.render().
    - If gameWorld.isPlayerDead() -> reload the same GameScreen (restart level).
    - If gameWorld.isLevelCompleted() -> for MVP, go back to MainMenuScreen.

3. Ensure the game starts at MainMenuScreen and then transitions correctly to GameScreen.


STEP 7: POLISH, COMMENTS AND CLEANUP
-------------------------------------
1. Add Javadoc-style comments on at least:
    - Entity, Player, GameWorld, TiledLevelLoader, GameController, WorldRenderer.
2. Use clear naming for methods and fields.
3. Make sure the code compiles and runs:
    - Even if the map file is missing, fallback should allow the window to open and show a minimal level.
4. Log important events:
    - Level loaded.
    - Fallback used.
    - Player death.
    - Level completion.


FINAL OBJECTIVE
----------------
At the end of these steps, the project must:

- Compile and run without manual edits.
- Open a window with a main menu.
- Allow starting a level.
- Show a player character that auto-runs, can jump, collides with the ground, dies on spike or falling, and can reach an end flag.
- Support Tiled-defined levels via a TMX file, with clear conventions, while also providing a fallback level if the file is missing.
- Have a clear separation of concerns (MVC) and demonstrate:
    - Inheritance and abstract classes (Entity, DynamicEntity, Player, Obstacle, EndFlag).
    - Interfaces if you choose to define them (e.g. Updatable, Renderable).
    - At least one design pattern (Factory for entities, optionally Singleton for configuration or assets).
