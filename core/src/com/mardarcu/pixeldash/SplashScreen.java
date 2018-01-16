package com.mardarcu.pixeldash;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by Owner on 10/10/2015.
 */
public class SplashScreen implements Screen{

    private final PixelDash game;
    Stage stage;
    Image splashImage;
    OrthographicCamera camera;

    public SplashScreen(final PixelDash game){
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        stage = new Stage(new FitViewport(800, 480, camera));

        Texture splashTexture = (Texture) game.assets.get("splash_logo.png", Texture.class);
        splashImage = new Image(splashTexture);
        splashImage.setPosition(stage.getWidth() / 2 - splashTexture.getWidth() / 2, stage.getHeight() / 2 + splashTexture.getHeight() / 8);
        splashImage.setOrigin(splashImage.getWidth() / 2, splashImage.getHeight() / 2);

        splashImage.addAction(sequence(alpha(0f), parallel(fadeIn(1f),
                        moveTo(stage.getWidth() / 2 - splashImage.getWidth() / 2, stage.getHeight() / 2 - splashImage.getHeight() / 4, 1f)), fadeOut(1f),
                run(new Runnable() {
                    @Override
                    public void run() {
                        game.splashStart.stop();
                        game.setScreen(game.menuScreen);
                    }
                })));
        stage.addActor(splashImage);
        game.splashStart.play();
    }

    @Override
    public void render(float delta) {
        update(delta);
        stage.draw();
    }

    public void update(float delta){
        stage.act(delta);
    }
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        stage.dispose();

    }

    @Override
    public void dispose() {

    }
}
