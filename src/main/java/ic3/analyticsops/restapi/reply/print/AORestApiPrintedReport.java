package ic3.analyticsops.restapi.reply.print;

import org.jetbrains.annotations.Nullable;

public class AORestApiPrintedReport
{
    public boolean done;

    @Nullable
    public String pdfName;

    @Nullable
    public byte[] pdfContent;
}
