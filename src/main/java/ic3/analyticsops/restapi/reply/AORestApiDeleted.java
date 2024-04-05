package ic3.analyticsops.restapi.reply;

import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class AORestApiDeleted
{
    public boolean done;

    @Nullable
    public String message;

    public void prettyPrint(PrintStream out)
    {
        out.println(done + " : " + message);
    }
}
