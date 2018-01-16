package com.mardarcu.pixeldash;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by Owner on 10/4/2015.
 */
public class CollisionListener implements ContactListener {

    public int numFootContacts;
    PlayScreen playScreen;

    //Collision Listener tends to call FINISH game 2 times in a row.

    public CollisionListener(PlayScreen playScreen){
        this.playScreen = playScreen;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture a  = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if(a.getUserData() != null ){
            //System.out.println("foot hit ground");
            if(a.getUserData().equals(PlayScreen.BIT_BOUNDS)){
                //System.out.println("hit bounds");
                playScreen.outOfBounds();
            } else if((a.getUserData().equals(PlayScreen.BIT_FINISH))){
                if(playScreen.playing) playScreen.won();
                //System.out.println("COLLISION LISTENER CALLS WIN!");
            }else if(a.getUserData().equals(PlayScreen.BIT_FOOT)){
                //System.out.println("foot hit ground");
                numFootContacts++;
            }
        } else if(b.getUserData() != null){
            //System.out.println("foot hit ground");
            if(b.getUserData().equals(PlayScreen.BIT_BOUNDS)){
               // System.out.println("hit bounds");
                playScreen.outOfBounds();
            } else if(b.getUserData().equals(PlayScreen.BIT_FINISH)){
                //System.out.println("COLLISION LISTENER CALLS WIN!");
                if(playScreen.playing) playScreen.won();

            }else if(b.getUserData().equals(PlayScreen.BIT_FOOT)){
                //System.out.println("foot hit ground");
                numFootContacts++;
            }
        }



    }

    @Override
    public void endContact(Contact contact) {
        Fixture a  = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if(a.getUserData() != null ){
            //System.out.println("foot hit ground");
            if(a.getUserData().equals(PlayScreen.BIT_FOOT)) {
               // System.out.println("foot left ground");
                numFootContacts--;
            }
        } else if(b.getUserData() != null) {
                //System.out.println("foot hit ground");
                if (b.getUserData().equals(PlayScreen.BIT_FOOT)) {
                    //System.out.println("foot left ground");
                    numFootContacts--;
                }
        }

    }

    public boolean onGround(){ return numFootContacts > 0; }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
