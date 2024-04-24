package ic3.analyticsops.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AOTimestampUtils
{
    private static final SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SSS z");

    private AOTimestampUtils()
    {
    }

    public static String formatTimestamp(long timestamp)
    {
        final Date ts = new Date(timestamp);

        synchronized (df)
        {
            return df.format(ts);
        }
    }

}
