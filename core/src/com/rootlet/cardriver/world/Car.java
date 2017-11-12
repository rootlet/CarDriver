package com.rootlet.cardriver.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import org.jbox2d.common.Settings;

/**
 * Created by rootlet on 11.11.2017.
 */

public class Car {
    private World world;
    private Body tire;
    private PolygonShape tireShape;
    public Control conrrolState;

    public Car(World world) {
        this.world = world;
        createTire();
    }

    private void createTire() {
        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.DynamicBody;
        //bDef.position.set(10, 10);
        tire = world.createBody(bDef);

        tireShape = new PolygonShape();
        tireShape.setAsBox(0.5f, 1.25f);
        tire.createFixture(tireShape, 1);
        tire.setUserData(this);

    }

    private Vector2 getLeteralVelocity() {
        Vector2 currentRightNormal = tire.getWorldVector(new Vector2(1,0));
        return currentRightNormal.scl(currentRightNormal.dot(tire.getLinearVelocity()));
    }

    private Vector2 getForwardVelocity() {
        Vector2 currentForwardNormal = tire.getWorldVector(new Vector2(0,1));
        System.out.println("currentForwardNormal = " + currentForwardNormal);
        System.out.println("tire.getLinearVelocity() = " + tire.getLinearVelocity());
        System.out.println("currentForwardNormal.dot(tire.getLinearVelocity()) = " + currentForwardNormal.dot(tire.getLinearVelocity()));
        return currentForwardNormal.scl(currentForwardNormal.dot(tire.getLinearVelocity()));
    }

    public void updateFriction() {
        System.out.println(getForwardVelocity());
        Vector2 impulse = getLeteralVelocity().scl(-tire.getMass());
        //-------------------Занос---------------------------------------------
        //if ( impulse.len() > 2.5f )
        //    impulse = impulse.scl(2.5f / impulse.len());
        //----------------------------------------------------------------------
        tire.applyLinearImpulse(impulse, tire.getWorldCenter(), false);

        tire.applyAngularImpulse(0.1f * tire.getInertia() * -tire.getAngularVelocity(), false);

        Vector2 currentForwardNormal = getForwardVelocity();
        float currentForwardSpeed = normalize(currentForwardNormal);
        //value = currentForwardNormal.len();
        float dragForceMagnitude = -2 * currentForwardSpeed;
        tire.applyForce( currentForwardNormal.scl(dragForceMagnitude ), tire.getWorldCenter(), false );
    }

    /// Convert this vector into a unit vector. Returns the length.
    private float normalize(Vector2 vector2)
    {
        float length = vector2.len();
        if (length < Settings.EPSILON)
        {
            return 0.0f;
        }
        float invLength = 1.0f / length;
        vector2.x *= invLength;
        vector2.y *= invLength;

        return length;
    }

    //tire class variables
    float maxForwardSpeed = 100;  // 100;
    float maxBackwardSpeed = -2; // -20;
    float maxDriveForce = 150;    // 150;

    public enum Control{
        NONE,
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
    //tire class function
    public void updateDrive(Control controlState) {
        //find desired speed
        float desiredSpeed = 0;
        switch (controlState) {
            case UP:   desiredSpeed = maxForwardSpeed; break;
            case DOWN: desiredSpeed = maxBackwardSpeed; break;
            default: return;//do nothing
        }

        //find current speed in forward direction
        Vector2 currentForwardNormal = tire.getWorldVector( new Vector2(0,1) );
        float currentSpeed = getForwardVelocity().dot(currentForwardNormal);

        //apply necessary force
        float force = 0;
        if ( desiredSpeed > currentSpeed )
            force = maxDriveForce;
        else if ( desiredSpeed < currentSpeed )
            force = -maxDriveForce;
        else
            return;

       // if (currentSpeed == 0) {
            tire.applyForce(new Vector2(0, force), tire.getWorldCenter(), true);
        //} else {
           // tire.applyForce(currentForwardNormal.scl(force),tire.getWorldCenter(), true);
        //}
    }

    public void updateTurn(Control controlState) {
        float desiredTorque = 0;
        switch (controlState) {
            case LEFT:  desiredTorque = 15;  break;
            case RIGHT: desiredTorque = -15; break;
            default: break;//nothing
        }
        tire.applyTorque( desiredTorque, false );
    }


    public World getWorld() {
        return world;
    }

    public Body getTire() {
        return tire;
    }

    public PolygonShape getTireShape() {
        return tireShape;
    }
}
