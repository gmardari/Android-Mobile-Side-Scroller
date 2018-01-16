package com.mardarcu.pixeldash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;


import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class PixelDash extends Game {

	final public static String APP_VERSION = "0.2";

	SpriteBatch batch;
	AssetManager assets;
	SaveStateManager saveManager;
	SaveState state;

	BitmapFont defaultFont;
	Sound hitSound;
	Sound levelSound;
	Sound splashStart;
	Skin skin;

	LoadingScreen loadingScreen;
	SplashScreen splashScreen;
	MenuScreen menuScreen;
	PlayScreen playScreen;

	boolean debugScene2d;
	boolean debugBox2d;
    boolean retryLevel;

	@Override
	public void create (){
		batch = new SpriteBatch();
		assets = new AssetManager();
		saveManager = new SaveStateManager();

		defaultFont = new BitmapFont();
		hitSound = Gdx.audio.newSound(Gdx.files.internal("sfx/hit.wav"));
		levelSound = Gdx.audio.newSound(Gdx.files.internal("sfx/levelselect.wav"));
		splashStart = Gdx.audio.newSound(Gdx.files.internal("sfx/splash_start.ogg"));

		state = saveManager.readSaveFile("save.pds", true);

		if(state == null){
			System.err.println("[Pixel Dash] Save file is null. Creating new save file.");
			state = saveManager.newSaveFile(true);

		}
		if(!state.targetVersion.equals(APP_VERSION)){
			System.err.println("[WARN] Loaded save file target version does not meet current version.\n" +
					"Target Version: " + state.targetVersion + " || Current Version: " + APP_VERSION);
		}

		saveManager.loadStateIntoMemory(state);

		debugScene2d = saveManager.readBooleanPreference(state, "debugScene2d");
		debugBox2d = saveManager.readBooleanPreference(state, "debugBox2d");
        retryLevel = saveManager.readBooleanPreference(state, "retryLevel");

		skin = new Skin();
		skin.add("defaultFont", defaultFont);


		Gdx.input.setCatchBackKey(true);

		loadingScreen = new LoadingScreen(this);
		splashScreen = new SplashScreen(this);
		menuScreen = new MenuScreen(this);
		//setScreen(menuScreen);
		setScreen(loadingScreen);

	}



	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}


	@Override
	public void dispose() {
		//super.dispose();
		if(menuScreen != null)menuScreen.dispose();
		if(playScreen != null) playScreen.dispose();
		if(splashScreen != null) splashScreen.dispose();
		batch.dispose();
		assets.dispose();

	}
}
