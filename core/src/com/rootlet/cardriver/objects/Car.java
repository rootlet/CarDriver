package com.rootlet.cardriver.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.utils.Box2DBuild;
import com.rootlet.cardriver.helpers.Control;

/**
 * Created by pavlenko on 11/15/17.
 */

public class Car {

    final float DEGTORAD = 0.0174532925199432957f;
    final float RADTODEG = 57.295779513082320876f;

    Body car;
    java.util.Vector<Tire> tires;
    RevoluteJoint flJoint, frJoint;

    public Car(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        car = world.createBody(bodyDef);

        Vector2 vertices[] = {
                new Vector2(1.5f, 0.0f),
                new Vector2(3.0f, 2.5f),
                new Vector2(2.8f, 5.5f),
                new Vector2(1.0f, 10.0f),
                new Vector2(-1.0f, 10.0f),
                new Vector2(-2.8f, 5.5f),
                new Vector2(-3.0f, 2.5f),
                new Vector2(-1.5f, 0.0f)};
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertices);
        Fixture fixture = car.createFixture(polygonShape, 0.1f);

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = car;
        jointDef.enableLimit = true;
        jointDef.lowerAngle = 0;//with both these at zero...
        jointDef.upperAngle = 0;//...the joint will not move
        jointDef.localAnchorB.setZero();//joint anchor in tire is always center

        tires = new java.util.Vector<Tire>();
        //flJoint = new RevoluteJointDef();
        //frJoint = new RevoluteJoint();


        Tire tire = new Tire(world);
        jointDef.bodyB = tire.tireBody;
        jointDef.localAnchorA.set( -3, 0.75f );
        world.createJoint(jointDef);
        tires.add(tire);

        tire = new Tire(world);
        jointDef.bodyB = tire.tireBody;
        jointDef.localAnchorA.set( 3, 0.75f );
        world.createJoint(jointDef);
        tires.add(tire);

        tire = new Tire(world);
        jointDef.bodyB = tire.tireBody;
        jointDef.localAnchorA.set( -3, 8.5f );
        flJoint = (RevoluteJoint)world.createJoint(jointDef);
        tires.add(tire);

        tire = new Tire(world);
        jointDef.bodyB = tire.tireBody;
        jointDef.localAnchorA.set( 3, 8.5f );
        frJoint = (RevoluteJoint)world.createJoint(jointDef);
        tires.add(tire);

    }

    public void update() {
        for (int i = 0; i < tires.size(); i++)
            tires.get(i).updateFriction();
        for (int i = 0; i < tires.size(); i++)
            tires.get(i).updateDrive();


        float lockAngle = 40 * DEGTORAD;
        float turnSpeedPerSec = 320 * DEGTORAD;//from lock to lock in 0.25 sec
        float turnPerTimeStep = turnSpeedPerSec / 60.0f;
        float desiredAngle = 0;
        if (Control.left) desiredAngle = lockAngle;
        if (Control.right) desiredAngle = -lockAngle;
        /*switch ( Control.left ) {
            case LEFT:  desiredAngle = lockAngle;  break;
            case RIGHT: desiredAngle = -lockAngle; break;
            default: ;//nothing
        }*/
        float angleNow = flJoint.getJointAngle();
        float angleToTurn = desiredAngle - angleNow;
        angleToTurn = MathUtils.clamp( angleToTurn, -turnPerTimeStep, turnPerTimeStep );
        float newAngle = angleNow + angleToTurn;
        flJoint.setLimits( newAngle, newAngle );
        frJoint.setLimits( newAngle, newAngle );
    }
}
