package com.mardarcu.pixeldash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by Owner on 9/27/2015.
 */
public class MenuScreen implements Screen {

    PixelDash game;
    Stage stage;
    MenuScreenState screenState;

    StretchViewport viewport;
    OrthographicCamera camera;

    Table rootTable;
    Table headerTable;
    Table mainTable;

    Table levelRootTable;
    Table levelHeader;
    Table levelMain;

    Table settingsRootTable;
    Table settingsHeader;
    Table settingsMain;

    TextButton playGameButton;
    TextButton settingsButton;
    TextButton.TextButtonStyle defaultButtonStyle;
    TextButton.TextButtonStyle royalOrangeButtonStyle;
    TextButton.TextButtonStyle grayButtonStyle;
    LinkedHashMap<Integer, TextButton> levelButtons;
    LinkedHashMap<String, TextButton> settingsButtons;


    int levelCounter;

    Sprite worldBackground;
    Image worldImage;
    Image splashLogo;
    Image levelSelectSplash;
    Image settingsSplash;

    public MenuScreen(PixelDash game){
        this.game = game;
    }

    @Override
    public void show() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        camera.update();
        viewport = new StretchViewport(800, 480, camera);
        stage = new Stage(viewport, game.batch);

        screenState = new MenuScreenState(this, MenuScreenState.MAIN_SCREEN);

        //debug = Boolean.parseBoolean(game.state.preferences.get("debugScene2d").toString());

        levelCounter = 1;
        worldBackground = new Sprite(new Texture("night_background.jpg"));
        worldBackground.setPosition(0, 0);
        worldBackground.setSize(stage.getViewport().getScreenWidth(), stage.getViewport().getScreenHeight());
        worldImage = new Image(new SpriteDrawable(worldBackground));
        worldImage.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        worldImage.setPosition(0, 0);
        splashLogo = new Image(new SpriteDrawable(new Sprite(game.assets.get("splash_logo.png", Texture.class))));
        splashLogo.setSize(600, 200);
        levelSelectSplash = new Image(new SpriteDrawable(new Sprite(game.assets.get("levelselect_splash.png", Texture.class))));
        levelSelectSplash.setSize(600, 200);
        settingsSplash = new Image(new SpriteDrawable(new Sprite(game.assets.get("settings_splash.png", Texture.class))));
        settingsSplash.setSize(600, 200);
        SpriteDrawable defaultButtonUp = new SpriteDrawable(new Sprite(game.assets.get("default_button.png", Texture.class)));
        SpriteDrawable royalOrangeButtonUp = new SpriteDrawable(new Sprite(game.assets.get("royalorange_button.png", Texture.class)));
        SpriteDrawable grayButtonUp = new SpriteDrawable(new Sprite(game.assets.get("gray_button.png", Texture.class)));
        defaultButtonStyle = new TextButton.TextButtonStyle(defaultButtonUp, defaultButtonUp, defaultButtonUp, new BitmapFont());
        royalOrangeButtonStyle = new TextButton.TextButtonStyle(royalOrangeButtonUp, royalOrangeButtonUp, royalOrangeButtonUp, new BitmapFont());
        grayButtonStyle = new TextButton.TextButtonStyle(grayButtonUp, grayButtonUp, grayButtonUp, new BitmapFont());
        playGameButton  = new TextButton("Play", defaultButtonStyle);
        playGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenState.setScreen(MenuScreenState.LEVEL_SCREEN);
                rootTable.setVisible(!rootTable.isVisible());
                levelRootTable.setVisible(!levelRootTable.isVisible());
            }
        });
        settingsButton = new TextButton("Settings", defaultButtonStyle);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenState.setScreen(MenuScreenState.SETTINGS_SCREEN);
                rootTable.setVisible(!rootTable.isVisible());
                settingsRootTable.setVisible(!settingsRootTable.isVisible());
            }
        });
        setupTables();


        Gdx.input.setInputProcessor(stage);
        stage.setDebugAll(game.debugScene2d);

    }

    public void setupTables(){

        rootTable = new Table();
        rootTable.setFillParent(true);
        headerTable = new Table();
        mainTable = new Table();
        rootTable.setFillParent(true);
        rootTable.add(headerTable).maxHeight(Gdx.graphics.getHeight() / 3f).top();
        rootTable.row();
        rootTable.add(mainTable).expandY();

        headerTable.add(splashLogo).padTop(50f).top();

        mainTable.add(playGameButton).top().row();
        mainTable.add(settingsButton).padTop(10f);


        levelRootTable = new Table();
        levelRootTable.setFillParent(true);
        levelHeader = new Table();
        levelHeader.add(levelSelectSplash).expandX().top();
        levelRootTable.add(levelHeader).expandX().top();
        levelRootTable.row();
        levelMain = new Table();
        loadLevels();

        levelRootTable.add(levelMain);
        levelRootTable.setVisible(false);

        settingsRootTable = new Table();
        settingsRootTable.setFillParent(true);
        settingsHeader = new Table();
        settingsHeader.add(settingsSplash).expandX().top();
        settingsRootTable.add(settingsHeader).maxHeight(Gdx.graphics.getHeight() / 3f).padTop(20f).top();
        settingsRootTable.row();
        settingsMain = new Table();
        settingsRootTable.add(settingsMain).expandY();
        settingsButtons = new LinkedHashMap<String, TextButton>();
        TextButton toggle_debugScene2d = new TextButton("Debug Scene2d: " + game.debugScene2d, defaultButtonStyle);
        TextButton enableLevel1 = new TextButton("Enable lvl 1", defaultButtonStyle);
        TextButton toggle_debugBox2d = new TextButton("Debug Box2d: " + game.debugBox2d, defaultButtonStyle);
        TextButton toggle_retryLevel = new TextButton("Debug Retry Level: " + game.retryLevel, defaultButtonStyle);
        settingsButtons.put("toggle_debugScene2d", toggle_debugScene2d);
        settingsButtons.put("enableLevel1", enableLevel1);
        settingsButtons.put("toggle_debugBox2d", toggle_debugBox2d);
        settingsButtons.put("toggle_retryLevel", toggle_retryLevel);
        toggle_debugScene2d.addListener(new ClickListener() {
            TextButton button = settingsButtons.get("toggle_debugScene2d");

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.debugScene2d = !game.debugScene2d;
                stage.setDebugAll(game.debugScene2d);
                game.state.preferences.put("debugScene2d", game.debugScene2d);
                button.setText("Debug Scene2d: " + game.debugScene2d);
            }
        });
        enableLevel1.addListener(new ClickListener() {
            TextButton button = settingsButtons.get("enableLevel1");

            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.state.passLevel(PlayScreen.NORMAL_SET, 1);
                game.saveManager.passLevel(PlayScreen.NORMAL_SET, 1);
            }
        });
        toggle_debugBox2d.addListener(new ClickListener() {
            TextButton button = settingsButtons.get("toggle_debugBox2d");

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.debugBox2d = !game.debugBox2d;
                game.state.preferences.put("debugBox2d", game.debugBox2d);
                button.setText("Debug Box2d: " + game.debugBox2d);
            }
        });
        toggle_retryLevel.addListener(new ClickListener() {
            TextButton button = settingsButtons.get("toggle_retryLevel");

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.retryLevel = !game.retryLevel;
                game.state.preferences.put("retryLevel", game.retryLevel);
                button.setText("Debug Retry Level: " + game.retryLevel);
            }
        });
        Collection buttons = settingsButtons.values();
        Iterator ir = buttons.iterator();
        while(ir.hasNext()){
            settingsMain.add((TextButton) ir.next()).top().row();
        }

        settingsRootTable.setVisible(false);

        stage.addActor(worldImage);
        stage.addActor(rootTable);
        stage.addActor(levelRootTable);
        stage.addActor(settingsRootTable);
    }



    public void loadLevels(){
        levelMain.clearChildren();
        levelCounter = 1;
        //ArrayList<Boolean> levelData = game.state.getPassedLevelData(PlayScreen.NORMAL_SET);
        //boolean[] levelData = game.state.getPassedLevelData(PlayScreen.NORMAL_SET);
        boolean[] levelData = game.saveManager.getPassedLevelData(PlayScreen.NORMAL_SET);

        //levelButtons = new TextButton[30];
        levelButtons = new LinkedHashMap<Integer, TextButton>();
        for(int i = 0; i < 30; i++){
            TextButton levelButton;

            //System.out.println(levelData.get(i));
            if(levelData[i]) {
                //levelButtons[i] = new TextButton("" + (i + 1), royalOrangeButtonStyle);
                levelButton = new TextButton("" + (i + 1), royalOrangeButtonStyle);

                levelButton.addListener(new ClickListener() {
                    int level = levelCounter;

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.playScreen = new PlayScreen(game, worldBackground, PlayScreen.NORMAL_SET, level);
                        game.setScreen(game.playScreen);
                    }
                });
            } else{
                levelButton = new TextButton("" + (i + 1), grayButtonStyle);
            }
            levelButtons.put(i, levelButton);
            levelCounter++;
            levelMain.add(levelButton).size(stage.getViewport().getWorldWidth() / 12).pad(20f).padTop(2f).padBottom(2f);
            if((i+1) % 6 == 0) levelMain.row();



        }


    }

    @Override
    public void render(float delta) {
        switch(Gdx.app.getType()){
            case Android:
                if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
                    screenState.setMainScreen();
                }
            case Desktop:
                if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
                    screenState.setMainScreen();
                }
        }



        stage.act(delta);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        /*
        game.batch.begin();
            worldBackground.draw(game.batch);

        game.batch.end();
        */
        stage.draw();
    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
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
}
