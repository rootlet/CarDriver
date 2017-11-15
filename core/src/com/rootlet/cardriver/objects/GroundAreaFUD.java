package com.rootlet.cardriver.objects;

import com.rootlet.cardriver.helpers.FixtureUserDataType;

/**
 * Created by pavlenko on 11/15/17.
 */

public class GroundAreaFUD extends FixtureUserData {

    public float frictionModifier;
    public boolean outOfCourse;

    public GroundAreaFUD(float frictionModifier, boolean outOfCourse) {
        super(FixtureUserDataType.GROUND_AREA);
        this.frictionModifier = frictionModifier;
        this.outOfCourse = outOfCourse;
    }
}
