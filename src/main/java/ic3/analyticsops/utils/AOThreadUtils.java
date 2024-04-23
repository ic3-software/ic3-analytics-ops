package ic3.analyticsops.utils;

public abstract class AOThreadUtils
{
    private AOThreadUtils()
    {
    }

    public static Thread startNewThread(String name, Runnable runnable)
    {
        return Thread.ofVirtual().name(name).start(runnable);
    }
}
