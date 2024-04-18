package ic3.analyticsops.common;

import ic3.analyticsops.test.AOSerializable;

public abstract class AOPause extends AOSerializable
{
    public abstract long pauseMS();

    @Override
    public abstract String toString();

}
