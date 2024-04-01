package ic3.analyticsops.test.task.reporting;

import com.google.gson.GsonBuilder;

public class AOPrintParams
{
    public final boolean inBrowser = true;
    public final boolean withState = false;
    /**
     * http://localhost:8282/icCube/report/editor
     */
    public final String appUrl;
    public final String appLocalUrl = "dft";
    public final String appCustomizationUrl = "-";
    public final int timeoutS = 30;
    public final int debugWaitingTimeS = 0;
    public final int scale = 1;
    public final String pageSizeName = "A4";
    public final String pageOrientation = "portrait";
    public final String pageSizeUnits = "mm";
    public final int pageWidth = 210;
    public final int pageHeight = 297;
    public final int marginTop = 10;
    public final int marginRight = 10;
    public final int marginBottom = 10;
    public final int marginLeft = 1;

    public AOPrintParams(String reportViewerURL)
    {
        this.appUrl = reportViewerURL.replace("/viewer", "/editor");
    }

    public String toJson()
    {
        return new GsonBuilder().create().toJson(this);
    }
}
