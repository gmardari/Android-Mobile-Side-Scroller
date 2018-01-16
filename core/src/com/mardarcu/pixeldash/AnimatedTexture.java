package com.mardarcu.pixeldash;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import static com.mardarcu.pixeldash.PlayScreen.PPM;

/**
 * Created by Owner on 10/3/2015.
 */
public class AnimatedTexture {

    TextureRegion[] regions;
    //Texture texture;
    int totalStates;
    int currentState;
    int stateInterval;
    private long lastTime;
    int stateWidth;
    int stateHeight;
    float width;
    float height;

    public AnimatedTexture(Texture texture, int totalStates, int stateInterval, float width, float height){
        this.totalStates = totalStates;
        //this.texture = texture;
        this.stateInterval = stateInterval;
        this.width = width;
        this.height = height;
        //setup regions
        regions = new TextureRegion[totalStates];
        stateWidth = texture.getWidth() /  totalStates;
        stateHeight = texture.getHeight();
       // System.out.println(texture.getWidth() + " stateWidth: " + stateWidth);
        for(int i = 0; i < totalStates; i++){
            regions[i] = new TextureRegion(texture, i * stateWidth, 0, stateWidth, stateHeight);

        }

        currentState = 0;
    }

    public void setTime(long currentTime){
        lastTime = currentTime;
    }

    public void startTime(){
        lastTime = System.currentTimeMillis();
    }

    public void render(SpriteBatch batch, float x, float y){
        //System.out.println("x: " + x + " y: " + y + " " + regions[currentState].getRegionWidth() + " " + regions[currentState].getRegionWidth());
        //batch.draw(regions[currentState], x - stateWidth / 4 + 15f , y - stateHeight / 4 + 3f, width, height);
        batch.draw(regions[currentState], x - width / 2, y - height / 2, width, height);
        //if currentState is at last state
        if(System.currentTimeMillis() - lastTime >= stateInterval) {
            //on last state
            if (currentState == (totalStates - 1)) {
                currentState = 0;
            } else {
                currentState++;
            }
            lastTime = System.currentTimeMillis();
        }
    }

}
