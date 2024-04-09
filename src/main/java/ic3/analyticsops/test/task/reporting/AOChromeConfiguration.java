package ic3.analyticsops.test.task.reporting;

import org.jetbrains.annotations.Nullable;

public class AOChromeConfiguration
{
    /**
     * By default, the shell is attempting to locate Chrome in the system.
     * <p>
     * To prevent that lookup, use the 'exec' field to reference the absolute path of the executable
     * to launch (e.g., you can use chromium instead).
     */
    @Nullable
    public final String exec;

    /**
     * Optional extra. command line arguments for the Chrome|Chromium process.
     */
    @Nullable
    public final String execOptions;

    public AOChromeConfiguration()
    {
        // JSON deserialization

        this.exec = null;
        this.execOptions = null;
    }
}
