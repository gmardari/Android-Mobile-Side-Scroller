package com.mardarcu.pixeldash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;


/**
 * Created by Owner on 10/10/2015.
 */
public class LoadingScreen implements Screen {

    private final PixelDash game;
    private final float extraLoadTime = 0.25f;
    private float progress;
    Texture splashLogo;
    Texture graySplashLogo;

    public LoadingScreen(final PixelDash game){
        this.game = game;
    }

    @Override
    public void show() {
        progress = 0f;
        splashLogo = new Texture("splash_logo.png");
        graySplashLogo = new Texture("splash_logo_gray.png");
        queueAssets();
    }

    public void queueAssets(){
        game.assets.load("splash_logo.png", Texture.class);
        game.assets.load("levelselect_splash.png", Texture.class);
        game.assets.load("night_background.jpg",Texture.class);
        game.assets.load("boy_running.png", Texture.class);
        game.assets.load("settings_splash.png", Texture.class);
        game.assets.load("gray_button.png", Texture.class);
        game.assets.load("default_button.png", Texture.class);
        game.assets.load("royalorange_button.png", Texture.class);
        game.assets.load("blocks.png", Texture.class);

        game.assets.load("ui/uiskin.atlas", TextureAtlas.class);
    }

    @Override
    public void render(float delta) {
        update(delta);
        game.batch.begin();
        game.batch.draw(graySplashLogo, Gdx.graphics.getWidth() / 2 - splashLogo.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - splashLogo.getHeight() / 2);
        game.batch.draw(splashLogo, Gdx.graphics.getWidth() / 2 - splashLogo.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - splashLogo.getHeight() / 2,
                0, 0, (int) (splashLogo.getWidth() * progress), splashLogo.getHeight());
        game.batch.end();
    }

    public void update(float delta){
        if(game.assets.update()){
            game.setScreen(game.splashScreen);
        }

        progress = game.assets.getProgress();
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
        splashLogo.dispose();
        graySplashLogo.dispose();
    }

    @Override
    public void dispose() {

    }
}
