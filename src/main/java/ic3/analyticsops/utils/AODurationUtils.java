package ic3.analyticsops.utils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public abstract class AODurationUtils
{
    private AODurationUtils()
    {
    }

    public static String formatMillis(long millis)
    {
        final Locale locale = Locale.ENGLISH;
        final String separator = "";

        // < 10 seconds
        if (millis < 10 * 1000)
        {
            return String.format(locale, "%,dms", millis);
        }
        // < 1 minute
        if (millis < 60 * 1000)
        {
            return String.format(locale, "%.2fs", millis / 1000.0);
        }

        // guess a little more nanos it's fine if it takes more than 1 minute
        final long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        final long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        // >= 24 hours
        if (days != 0)
        {
            return String.format(locale, "%dd" + separator + "%02dh" + separator + "%02dm" + separator + "%02ds", days, hours, minutes, seconds);
        }
        // >= 1 hour
        if (hours != 0)
        {
            return String.format(locale, "%dh" + separator + "%02dm" + separator + "%02ds", hours, minutes, seconds);
        }
        // < 1 hour && > 1minute
        else
        {
            return String.format(locale, "%dm" + separator + "%02ds", minutes, seconds);
        }
    }

}
