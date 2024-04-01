package ic3.analyticsops.test.task.reporting;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import io.webfolder.cdp.exception.CdpException;
import io.webfolder.cdp.session.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AOOpenReportTask extends AOTask
{
    public static final Logger LOGGER = LogManager.getLogger();

    private String reportPath;

    @Nullable
    private Integer timeoutS;

    @Override
    public String getKind()
    {
        return "OpenReport";
    }

    public void run(AOTaskContext context)
            throws AORestApiException,
                   AOChromeException
    {
        final String restApiURL = context.getRestApiURL();

        // -------------------------------------------------------------------------------------------------------------
        // Very temporary code (needs more works) : more like a POC for now.
        //      - print-in-browser : to ensure all widgets are rendered
        //              regular /viewer is not rendering widgets until they're visible
        //              assertion: ic3printStatus=ic3reportRendered:true
        //      - authentication : assuming icCube is configured w/ &ic3demo=
        // -------------------------------------------------------------------------------------------------------------

        final String reportViewerURL = restApiURL.replace("/icCube/api", "/icCube/report/viewer");

        final String reportURL = reportViewerURL
                + "?ic3demo="
                + "&ic3report=" + encode(reportPath)
                + "&ic3printParams=" + encode(new AOPrintParams(reportViewerURL).toJson());

        final int timeoutS = this.timeoutS != null ? this.timeoutS : 30;

        final String browserContext = context.createBrowserContext();

        try
        {
            try (Session session = context.createBrowserSession(browserContext))
            {
                session.navigate(reportURL);

                final int timeOutInMillis = 1000 * timeoutS;

                final long start = System.currentTimeMillis();

                session.waitDocumentReady(timeOutInMillis);

                final int waitPeriodMS = 1000;

                int newTimoutMillis = Math.max(waitPeriodMS * 2, timeOutInMillis - (int) (System.currentTimeMillis() - start));

                final boolean gotIt = session.waitUntil(
                        s -> safeCompareVariable(s, "ic3printStatus"),
                        newTimoutMillis,
                        waitPeriodMS /* Cannot find context with specified id */,
                        true
                );

                if (!gotIt)
                {
                    throw new AOChromeException("OpenReport failed : " + reportPath);
                }
            }
        }
        finally
        {
            context.disposeBrowserContext(browserContext);
        }
    }

    private static boolean safeCompareVariable(Session session, String name)
    {
        try
        {
            return "ic3reportRendered:true".equalsIgnoreCase(session.getVariable(name, String.class));

        }
        catch (NullPointerException ex)
        {
            LOGGER.warn("[chrome] could not access " + name + " variable (NPE)", ex);
            return false;
        }
        catch (CdpException ex)
        {
            final String error = ex.getMessage();

            if (error != null && error.endsWith("is not defined"))
            {
                LOGGER.debug("[chrome] could not access " + name + " variable (not defined)");
                return false;
            }

            LOGGER.debug("[chrome] could not access " + name + " variable (exception)", ex);
            return false;
        }
    }

    private static String encode(String value)
    {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

}
