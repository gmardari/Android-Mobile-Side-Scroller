package com.mardarcu.pixeldash;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import static com.mardarcu.pixeldash.PlayScreen.BIT_GREEN;
import static com.mardarcu.pixeldash.PlayScreen.PPM;
/**
 * Created by Owner on 10/3/2015.
 */
public class Player {

    PlayScreen playScreen;
    Body body;
    BodyDef bodyDef;
    AnimatedTexture animTexture;
    TextureRegion[] hudBlock;
    float width = 10f;
    float height = 22f;
    int blockWidth;
    int blockHeight;
    int currentColour;

    public Player(PlayScreen playScreen, Texture texture, Texture hudBlock)
    {
        this.playScreen = playScreen;
        this.hudBlock = new TextureRegion[3];
        blockWidth = hudBlock.getWidth() /  3;
        blockHeight = hudBlock.getHeight();
        // System.out.println(texture.getWidth() + " stateWidth: " + stateWidth);
        for(int i = 0; i < 3; i++){
            this.hudBlock[i] = new TextureRegion(hudBlock, i * blockWidth, 0, blockWidth, blockHeight);

        }
        animTexture = new AnimatedTexture(texture, 8, (int) (1000 * 0.1f), 24f + 24f * playScreen.zoom, 30f + 30f * playScreen.zoom);
    }

    public void setupBody(){
        bodyDef = new BodyDef();
// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
// Set our body's starting position in the world
        //bodyDef.position.set(-playScreen.tilePixelWidth / PPM, 50 / PPM);
        bodyDef.position.set(-16f / PPM, 100f / PPM);

// Create our body in the world using our body definition
        body = playScreen.world.createBody(bodyDef);
        body.setLinearVelocity(220f / PPM, 0f);

// Create a circle shape and set its radius to 6
        PolygonShape box = new PolygonShape();
        box.setAsBox(width / PPM, height / PPM);

// Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = 0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.filter.categoryBits = PlayScreen.BIT_PLAYER;
        fixtureDef.filter.maskBits = PlayScreen.BIT_RED | PlayScreen.BIT_OBJECTS;

        body.createFixture(fixtureDef);

        box.setAsBox((width ) / PPM, 9f / PPM, new Vector2(0, -height / PPM), 0);

        fixtureDef.shape = box;
        fixtureDef.density = 8f;
        fixtureDef.filter.categoryBits = PlayScreen.BIT_PLAYER;
        fixtureDef.filter.maskBits = PlayScreen.BIT_RED;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData(playScreen.BIT_FOOT);


// Create our fixture and attach it to the body


// Remember to dispose of any shapes after you're done with them!
// BodyDef and FixtureDef don't need disposing, but shapes do.
        box.dispose();
    }

    public void render(SpriteBatch batch){
        //batch.draw(hudBlock[currentColour], playScreen.camera.position.x - playScreen.camera.viewportWidth / 2 + blockWidth,
            //    playScreen.camera.viewportHeight - blockHeight * 2f, blockWidth, blockHeight);
        animTexture.render(batch, body.getPosition().x * PPM - 7f, body.getPosition().y * PPM);
    }

    public void drawHudBlock(SpriteBatch batch){
        batch.draw(hudBlock[currentColour], blockWidth,
                playScreen.camera.viewportHeight - blockHeight * 2f, blockWidth, blockHeight);
    }

    public boolean checkVelocity(){
        if(body.getLinearVelocity().x <= 0){
            return true;
        }
        return false;
    }

    public void jump(){
        if(playScreen.cl.onGround()) {
            body.setLinearVelocity(body.getLinearVelocity().x, 0f);
            //body.applyForceToCenter(0.0f, 60f, true);
            body.applyLinearImpulse(new Vector2(0f, 2f), body.getWorldCenter(), true);
        }
    }

    public void nextColour(){
        //System.out.println(currentColour);
        if(currentColour == 2){
            currentColour = 0;
        } else {
            currentColour++;
        }
        //red -> green -> blue
        Filter filter = body.getFixtureList().get(0).getFilterData();
        short bits = filter.maskBits;
        //check for red
        if((bits & PlayScreen.BIT_RED) != 0){
            //unset red
            bits &= ~PlayScreen.BIT_RED;
            //set green
            bits |= PlayScreen.BIT_GREEN;
        } else if((bits & PlayScreen.BIT_GREEN) != 0){
            //unset green
            bits &= ~PlayScreen.BIT_GREEN;
            //set blue
            bits |= PlayScreen.BIT_BLUE;
        } else if((bits & PlayScreen.BIT_BLUE) != 0){
            //unset blue
            bits &= ~PlayScreen.BIT_BLUE;
            //set red
            bits |= PlayScreen.BIT_RED;
        }
        //set mask bits
        filter.maskBits = bits;
        body.getFixtureList().first().setFilterData(filter);
        body.getFixtureList().get(1).setFilterData(filter);
    }

}
