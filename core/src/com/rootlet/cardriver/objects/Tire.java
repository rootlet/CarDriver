package com.rootlet.cardriver.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.rootlet.cardriver.helpers.Control;

import org.jbox2d.common.Settings;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by rootlet on 11.11.2017.
 */

public class Tire {
    private World world;
    public Body tireBody;
    private PolygonShape tireShape;
    public Control conrrolState;
    Set<GroundAreaFUD> groundAreas;

    private float currentTraction;

    float maxForwardSpeed = 100;  // 100;
    float maxBackwardSpeed = -20; // -20;
    float maxDriveForce = 150;    // 150;

    public Tire(World world) {
        this.world = world;
        currentTraction = 1;
        createTire();
    }

    private void createTire() {
        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.DynamicBody;
        //bDef.position.set(50, 50);
        tireBody = world.createBody(bDef);

        tireShape = new PolygonShape();
        tireShape.setAsBox(0.5f, 1.25f);
        Fixture fixture = tireBody.createFixture(tireShape, 1);
        fixture.setUserData(new CarTireFUD());
        //tireBody.createFixture(tireShape, 1);
        tireBody.setUserData(this);
        groundAreas = new HashSet<GroundAreaFUD>();
    }

    private Vector2 getLeteralVelocity() {
        Vector2 currentRightNormal = tireBody.getWorldVector(new Vector2(1,0));
        return new Vector2(currentRightNormal).scl(currentRightNormal.dot(tireBody.getLinearVelocity()));
    }

    private Vector2 getForwardVelocity() {
        Vector2 currentForwardNormal = tireBody.getWorldVector(new Vector2(0,1));
        return new Vector2(currentForwardNormal).scl(currentForwardNormal.dot(tireBody.getLinearVelocity()));
    }

    public void updateFriction() {
        Vector2 impulse = new Vector2(getLeteralVelocity()).scl(-tireBody.getMass());
        //-------------------Занос---------------------------------------------
        if ( impulse.len() > 2.5f )
            impulse = new Vector2(impulse).scl(2.5f / impulse.len());
        //----------------------------------------------------------------------
        tireBody.applyLinearImpulse(new Vector2(impulse).scl(currentTraction), tireBody.getWorldCenter(), true);

        tireBody.applyAngularImpulse(currentTraction * 0.1f * tireBody.getInertia() * -tireBody.getAngularVelocity(), true);

        Vector2 currentForwardNormal = getForwardVelocity();
        float currentForwardSpeed = normalize(currentForwardNormal);
        float dragForceMagnitude = -2 * currentForwardSpeed;
        tireBody.applyForce(new Vector2(currentForwardNormal).scl(dragForceMagnitude * currentTraction), tireBody.getWorldCenter(), true );
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

    //tireBody class function
    public void updateDrive(Control controlState) {
        //find desired speed
        float desiredSpeed = 0;
        switch (controlState) {
            case UP:   desiredSpeed = maxForwardSpeed; break;
            case DOWN: desiredSpeed = maxBackwardSpeed; break;
            default: return;//do nothing
        }

        //find current speed in forward direction
        Vector2 currentForwardNormal = tireBody.getWorldVector( new Vector2(0,1) );
        float currentSpeed = getForwardVelocity().dot(currentForwardNormal);

        //apply necessary force
        float force = 0;
        if ( desiredSpeed > currentSpeed )
            force = maxDriveForce;
        else if ( desiredSpeed < currentSpeed )
            force = -maxDriveForce;
        else
            return;
        tireBody.applyForce(new Vector2(currentForwardNormal).scl(force * currentTraction), tireBody.getWorldCenter(), true);
    }

    public void updateTurn(Control controlState) {
        float desiredTorque = 0;
        switch (controlState) {
            case LEFT:  desiredTorque = 15;  break;
            case RIGHT: desiredTorque = -15; break;
            default: break;//nothing
        }
        tireBody.applyTorque( desiredTorque, true );
    }

    public void addGroundArea(GroundAreaFUD ga) { groundAreas.add(ga); updateTraction(); }
    public void removeGroundArea(GroundAreaFUD ga) { groundAreas.remove(ga); updateTraction(); }
    void updateTraction()
    {
        if (groundAreas.isEmpty())
            currentTraction = 1;
        else {
            //find area with highest traction
            currentTraction = 0;

            Iterator<GroundAreaFUD> it = groundAreas.iterator();
            while (it.hasNext()) {
                GroundAreaFUD ga = it.next();
                if (ga.frictionModifier > currentTraction)
                    currentTraction = ga.frictionModifier;
            }
        }
    }

    public PolygonShape getTireShape() {
        return tireShape;
    }

    public float getCurrentTraction() {
        return currentTraction;
    }
}
