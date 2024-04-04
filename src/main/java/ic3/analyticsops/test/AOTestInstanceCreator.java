package ic3.analyticsops.test;

import com.google.gson.InstanceCreator;

import java.io.File;
import java.lang.reflect.Type;

public class AOTestInstanceCreator implements InstanceCreator<AOTest>
{
    private final File json;

    public AOTestInstanceCreator(File json)
    {
        this.json = json;
    }

    @Override
    public AOTest createInstance(Type type)
    {
        return new AOTest(json);
    }

}
