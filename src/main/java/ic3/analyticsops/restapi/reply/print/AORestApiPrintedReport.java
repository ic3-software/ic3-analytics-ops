package ic3.analyticsops.restapi.reply.print;

import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class AORestApiPrintedReport
{
    public boolean done;

    @Nullable
    public String pdfName;

    @Nullable
    public byte[] pdfContent;

    public void prettyPrint(PrintStream out)
    {
        out.println(done + " : " + pdfName + " : " + (pdfContent != null ? pdfContent.length : 0));
    }
}
