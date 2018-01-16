package com.mardarcu.pixeldash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import java.util.LinkedHashMap;

import javax.xml.soap.Text;

/**
 * Created by Owner on 10/3/2015.
 */
public class PlayScreen implements Screen, InputProcessor {

    PixelDash game;
    public static final int NORMAL_SET = 1;
    public static final float PPM = 100;
    public static final short BIT_OBJECTS = 2;
    public static final short BIT_PLAYER = 4;
    public static final short BIT_RED = 8;
    public static final short BIT_GREEN = 16;
    public static final short BIT_BLUE = 32;
    public static final short BIT_FOOT = 2;
    public static final short BIT_BOUNDS = 4;
    public static final short BIT_FINISH = 8;
    int set;
    int level;
    float zoom = 0.8f;
    OrthographicCamera camera;
    OrthographicCamera backCamera;
    OrthographicCamera b2dcamera;
    TiledMapRenderer tiledMapRenderer;
    TiledMap levelMap;
    World world;
    CollisionListener cl;
    Box2DDebugRenderer debugRenderer;
    MapProperties prop;
    TiledMapTileLayer blockLayer;
    Stage stage;
    Skin skin;
    Table playTable;
    Table rootTable;
    Label.LabelStyle defaultLabel;
    Label attemptsLabel;
    Label winsLabel;
    TextButton retryButton;
    TextButton toMainMenu;
    LinkedHashMap<String, TextButton> stageButtons;


    int mapWidth;
    int mapHeight;
    int tilePixelWidth;
    int tilePixelHeight;
    int mapPixelWidth;
    int mapPixelHeight;
    boolean playing;
    boolean touchLeft;
    int attempts;
    int wins;

    Player player;
    Sprite background;

    public PlayScreen(PixelDash game, Sprite background, int set, int level){
        this.game = game;
        this.set = set;
        this.level = level;
        this.background = background;
    }

    @Override
    public void show() {
        if(set == NORMAL_SET){
            try {
                levelMap = new TmxMapLoader().load("levels/normal/level" + level + ".tmx");
                tiledMapRenderer = new OrthogonalTiledMapRenderer(levelMap);
            } catch(SerializationException e){
                System.err.println("Can't load level: " + set + ":" + level);
                game.setScreen(game.menuScreen);
                return;
            }
        } else{
            System.err.println("Could not find and load set: " + set);
            return;
        }

        attempts = game.saveManager.readLevelDataInteger(set, level, "attempts", 0, false);
        wins = game.saveManager.readLevelDataInteger(set, level, "wins", 0, false);

        prop = levelMap.getProperties();
       // redLayer = (TiledMapTileLayer) levelMap.getLayers().get("red");
       // greenLayer = (TiledMapTileLayer) levelMap.getLayers().get("green");
        ///blueLayer = (TiledMapTileLayer) levelMap.getLayers().get("blue");
        blockLayer = (TiledMapTileLayer) levelMap.getLayers().get("blockLayer");
        mapWidth = prop.get("width", Integer.class);
        mapHeight = prop.get("height", Integer.class);
        tilePixelWidth = prop.get("tilewidth", Integer.class);
        tilePixelHeight = prop.get("tileheight", Integer.class);

        mapPixelWidth = mapWidth * tilePixelWidth;
        mapPixelHeight = mapHeight * tilePixelHeight;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800 * zoom, 480 * zoom);
        camera.update();
        backCamera = new OrthographicCamera();
        backCamera.setToOrtho(false, 800 * zoom, 480 * zoom);
        backCamera.update();
        b2dcamera = new OrthographicCamera();
        b2dcamera.setToOrtho(false, (800 * zoom) / PPM, (480 * zoom) / PPM);
        b2dcamera.update();

        background.setSize(800, 480);
        background.setPosition(0, 0);

        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0f, -20f), true);
        cl = new CollisionListener(this);
        world.setContactListener(cl);
        player = new Player(this, game.assets.get("boy_running.png", Texture.class), game.assets.get("blocks.png", Texture.class));
        player.setupBody();
        SetupBodies();
        setupStage();

        InputMultiplexer im = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(im);
        playing = true;
        touchLeft = true;
        player.animTexture.startTime();
    }

    public void SetupBodies(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(-tilePixelWidth / PPM, tilePixelHeight * 2 / PPM);

        PolygonShape box = new PolygonShape();
        box.setAsBox(tilePixelWidth / PPM, tilePixelHeight * 2 / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.filter.categoryBits = BIT_RED;
        fixtureDef.filter.maskBits = BIT_PLAYER;
        fixtureDef.friction = 0f;

        world.createBody(bodyDef).createFixture(fixtureDef);
        box.dispose();

        createAllLayers();

        bodyDef.position.set(0f / PPM, 0f / PPM);
        ChainShape cs = new ChainShape();
        Vector2[] v = new Vector2[2];
        v[0] = new Vector2(0f / PPM, 0f / PPM - player.height / PPM);
        v[1] = new Vector2(mapPixelWidth / PPM, 0f / PPM - player.height / PPM);
       // v[2] = new Vector2(mapPixelWidth / PPM, mapPixelHeight / PPM);
        cs.createChain(v);
        fixtureDef.shape = cs;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = BIT_OBJECTS;

        world.createBody(bodyDef).createFixture(fixtureDef).setUserData(BIT_BOUNDS);

        v = new Vector2[2];
        v[0] = new Vector2(mapPixelWidth / PPM + player.width / 2 / PPM, 0f / PPM);
        v[1] = new Vector2(mapPixelWidth / PPM + player.width / 2 / PPM, mapPixelHeight / PPM);
        cs = new ChainShape();
        cs.createChain(v);
        fixtureDef.shape = cs;

        world.createBody(bodyDef).createFixture(fixtureDef).setUserData(BIT_FINISH);

        cs.dispose();
    }

    public void setupStage(){
        stage = new Stage(new StretchViewport(backCamera.viewportWidth, backCamera.viewportHeight, backCamera), game.batch);
        skin = new Skin();
        skin.addRegions(game.assets.get("ui/uiskin.atlas", TextureAtlas.class));
        skin.add("default-font", game.defaultFont);
        skin.load(Gdx.files.internal("ui/uiskin.json"));
        stageButtons = new LinkedHashMap<String, TextButton>();
        defaultLabel = new Label.LabelStyle(game.skin.getFont("defaultFont"), Color.WHITE);



        rootTable = new Table();
        //rootTable.setPosition(0, stage.getHeight());
        rootTable.center();
        rootTable.setFillParent(true);


        stage.addActor(rootTable);

        stage.setDebugAll(game.debugScene2d);

    }



    public void createAllLayers(){
        BodyDef groundBodyDef =new BodyDef();
        FixtureDef fdef = new FixtureDef();
        for(int row = 0; row < blockLayer.getHeight(); row++){
            for(int col = 0; col < blockLayer.getWidth(); col++){
                TiledMapTileLayer.Cell cell = blockLayer.getCell(col, row);
                if(cell == null) continue;
                if(cell.getTile() == null) continue;

                short bits;
                //System.out.println(cell.getTile().getId());
                switch (cell.getTile().getId()){
                    case 1:
                        bits = BIT_RED;
                        break;
                    case 2:
                        bits = BIT_GREEN;
                        break;
                    case 3:
                        bits = BIT_BLUE;
                        break;
                    default:
                        bits = BIT_RED;
                        break;
                }

                groundBodyDef.type = BodyDef.BodyType.StaticBody;
                groundBodyDef.position.set((col + 0.5f) * tilePixelWidth / PPM, (row + 0.5f) * tilePixelWidth / PPM);

                ChainShape cs = new ChainShape();
                Vector2[] v = new Vector2[3];
                v[0] = new Vector2(-tilePixelWidth / 2 / PPM, -tilePixelWidth / 2 / PPM);
                v[1] = new Vector2(-tilePixelWidth / 2 / PPM, tilePixelWidth / 2 / PPM);
                v[2] = new Vector2(tilePixelWidth / 2 / PPM, tilePixelWidth / 2 / PPM);
                cs.createChain(v);
                fdef.shape = cs;
                fdef.friction = 0f;
                fdef.filter.categoryBits = bits;
                fdef.filter.maskBits = BIT_PLAYER;
                world.createBody(groundBodyDef).createFixture(fdef);
                cs.dispose();
            }
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        /*
        if(playing){
            world.step(1 / 60f, 6, 2);
            //check if lost by hitting block and stopping motion
            if(player.checkVelocity()){
                game.hitSound.play(0.8f);
                lost();
                //goToMainScreen(false, game.retryLevel);
            }

            float camx = MathUtils.clamp(player.body.getPosition().x * PPM + (camera.viewportWidth / 4), camera.viewportWidth / 2, mapPixelWidth - camera.viewportWidth / 2);
            float b2dx = MathUtils.clamp(player.body.getPosition().x + (b2dcamera.viewportWidth / 4), b2dcamera.viewportWidth / 2, mapPixelWidth / PPM - b2dcamera.viewportWidth / 2);

            camera.position.set(camx, camera.viewportHeight / 2, 0);

            b2dcamera.position.set(b2dx, b2dcamera.viewportHeight / 2, 0);

        }
        */

        camera.update();
        b2dcamera.update();
        backCamera.update();


        game.batch.setProjectionMatrix(backCamera.combined);

        game.batch.begin();
            background.draw(game.batch);

            //game.batch.draw(player.animTexture.regions[0], 10, 10, 40, 50);
        game.batch.setProjectionMatrix(camera.combined);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        game.batch.end();

        //tiledMapRenderer.setView(camera);
        //tiledMapRenderer.render();

        if(playing) {
            game.batch.setProjectionMatrix(camera.combined);
            game.batch.begin();
            //player.render(game.batch);
            player.render(game.batch);
            game.batch.setProjectionMatrix(backCamera.combined);
            player.drawHudBlock(game.batch);
            game.batch.end();
        }
        //game.batch.setProjectionMatrix(backCamera.combined);
        if(game.debugBox2d)
            debugRenderer.render(world, b2dcamera.combined);
        stage.draw();
        backCamera.update();

       //

    }

    public void update(float delta){
        if(playing){
            for(int i = 0; i < 2; i++) {
                if(Gdx.input.isTouched(i)){
                    int screenX = Gdx.input.getX(i);
                    int screenY = Gdx.input.getY(i);

                    boolean tappedLeft = (screenX < camera.viewportWidth / 2) ? true : false;
                    //tapped right side of screen. Jump
                    if(!tappedLeft){
                        player.jump();
                    } else if(touchLeft){
                        player.nextColour();
                        touchLeft = false;
                    }
                }
            }

            world.step(1 / 60f, 6, 2);
            //check if lost by hitting block and stopping motion
            if(player.checkVelocity()){
                game.hitSound.play(0.8f);
                lost();
                //goToMainScreen(false, game.retryLevel);
            }

            float camx = MathUtils.clamp(player.body.getPosition().x * PPM + (camera.viewportWidth / 4), camera.viewportWidth / 2, mapPixelWidth - camera.viewportWidth / 2);
            float b2dx = MathUtils.clamp(player.body.getPosition().x + (b2dcamera.viewportWidth / 4), b2dcamera.viewportWidth / 2, mapPixelWidth / PPM - b2dcamera.viewportWidth / 2);

            camera.position.set(camx, camera.viewportHeight / 2, 0);
            b2dcamera.position.set(b2dx, b2dcamera.viewportHeight / 2, 0);
        }
        stage.act(delta);
    }

    public void showResults(boolean WON){
        //sync attempts and wins
        //attemptsLabel.setText("Attempts: " + attempts);
        //winsLabel.setText("Wins: " + wins);
        playTable = new Table(skin);

        //playTable.setOrigin(playTable.getWidth() / 2, playTable.getHeight() / 2);
        playTable.setBackground("default-round-large");
        playTable.setWidth(stage.getWidth() / 2);
        playTable.center();

        playTable.defaults().padBottom(5f).padTop(5f).padLeft(5f).padRight(5f);
        attemptsLabel = new Label("Attempts: " + attempts, skin);
        winsLabel = new Label("Wins: " + wins, skin);
        retryButton = new TextButton("Retry", skin);
        toMainMenu = new TextButton("Main Menu", skin);
        //Image panel = new Image(skin, "default");

        //stageButtons.put("retryButton", retryButton);
        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goToMainScreen(true, true);
            }
        });
        toMainMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goToMainScreen(true, false);
            }
        });
        //label.setPosition(100, 100);

        if(WON) {
            Label wonLabel = new Label("Level " + level + " complete!", skin);
            playTable.add(wonLabel).center().padTop(15f).row();
            playTable.add(attemptsLabel).width(100f).center();
            playTable.add(toMainMenu).width(100f).height(25f).center().row();
            playTable.add(winsLabel).width(100f).padBottom(15f).center();
        } else {
            playTable.add(attemptsLabel).width(100f).center().padTop(15f);
            playTable.add(retryButton).width(100f).height(25f).row();
            playTable.add(winsLabel).width(100f).center();
            playTable.add(toMainMenu).width(100f).height(25f).padBottom(15f).center().row();
        }


        //rootTable.add(playTable).width(220f);
        //rootTable.addActor(playTable);
        //playTable.align(Align.center);

        playTable.pack();

        playTable.setPosition(stage.getWidth() / 2 - playTable.getWidth() / 2, stage.getHeight() * 1.5f);

        //rootTable.setTouchable(Touchable.disabled);

        stage.addActor(playTable);
        playTable.addAction(moveTo(stage.getWidth() / 2 - playTable.getWidth() / 2, stage.getHeight() / 2 - playTable.getHeight() / 2, 0.5f,Interpolation.swing));

    }

    public void lost(){
        System.out.println("lost");
        attempts++;
        game.saveManager.writeLevelDataInteger(set, level, "attempts", attempts);
        showResults(false);

        playing = false;
    }

    public void outOfBounds(){
        //lost
        System.out.println("Out of bounds");
        attempts++;
        game.saveManager.writeLevelDataInteger(set, level, "attempts", attempts);
        showResults(false);
        playing = false;
    }

    public void won(){
        System.out.println("won");
        if(playing){
            game.levelSound.play(0.8f);
        }
        playing = false;
        //game.state.passLevel(1, level + 1);
        wins++; attempts++;
        game.saveManager.writeLevelDataInteger(set, level, "attempts", attempts);
        game.saveManager.writeLevelDataInteger(set, level, "wins", wins);
        game.saveManager.passLevel(1, level + 1);
        showResults(true);
        //goToMainScreen(true, false);
    }

    public void goToMainScreen(boolean SAVE_STATE, boolean RETRY_LEVEL){

        if(SAVE_STATE)
            game.saveManager.writeSaveFile(game.state, false);
        if(RETRY_LEVEL)
            game.setScreen(game.playScreen);
        else
            game.setScreen(game.menuScreen);
    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if(stage != null) stage.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
           goToMainScreen(true, false);
        }
        if(keycode == Input.Keys.ESCAPE){
            goToMainScreen(true, false);
        }
        if(keycode == Input.Keys.X){
           //System.out.println("SPACE!");
            player.jump();

        }
        if(keycode == Input.Keys.C){
            player.nextColour();

        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        /*
        if(!playing) {
            goToMainScreen(false, game.retryLevel);
            return  false;
        }
        //System.out.println("Pressed on screen");
        boolean tappedLeft = (screenX < camera.viewportWidth / 2) ? true : false;
        //tapped right side of screen. Jump
        if(!tappedLeft){
            player.jump();
        } else{
            player.nextColour();
        }
        */
        return false;

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //System.out.println("Released screen touch");
        touchLeft = true;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
