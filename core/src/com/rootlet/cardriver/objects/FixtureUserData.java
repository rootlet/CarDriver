package com.rootlet.cardriver.objects;

import com.rootlet.cardriver.helpers.FixtureUserDataType;

/**
 * Created by pavlenko on 11/15/17.
 */

public class FixtureUserData {
    FixtureUserDataType type;

    protected FixtureUserData(FixtureUserDataType type) {
        this.type = type;
    }

    public FixtureUserData(){};

    public FixtureUserDataType getType() { return type; }
}
