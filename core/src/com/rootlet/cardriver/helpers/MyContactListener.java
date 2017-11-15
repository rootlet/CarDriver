package com.rootlet.cardriver.helpers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.rootlet.cardriver.screen.MainScreen;

/**
 * Created by pavlenko on 11/16/17.
 */

public class MyContactListener implements ContactListener {

    MainScreen mainScreen;

    public MyContactListener(MainScreen mainScreen) {
        super();
        this.mainScreen = mainScreen;
    }

    @Override
    public void beginContact(Contact contact) {
        mainScreen.handleContact(contact, true);
        System.out.println("beginContact");
    }

    @Override
    public void endContact(Contact contact) {
        mainScreen.handleContact(contact, false);
        System.out.println("endContact");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
