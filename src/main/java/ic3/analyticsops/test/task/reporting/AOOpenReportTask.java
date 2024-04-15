package ic3.analyticsops.test.task.reporting;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.test.*;
import ic3.analyticsops.test.assertion.AOOpenReportAssertion;
import ic3.analyticsops.utils.AOLog4jUtils;
import ic3.analyticsops.utils.AOStringUtils;
import io.webfolder.cdp.command.Network;
import io.webfolder.cdp.event.network.RequestWillBeSent;
import io.webfolder.cdp.exception.CdpException;
import io.webfolder.cdp.session.Session;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AOOpenReportTask extends AOTask<AOOpenReportAssertion>
{
    private final String reportPath;

    @Nullable
    private final Integer timeoutS;

    /**
     * Similar to the GenerateMDXes task : when present, MDX from the report will be extracted and saved there.
     */
    @Nullable
    private final String data;

    protected AOOpenReportTask()
    {
        // JSON deserialization

        this.reportPath = null;
        this.timeoutS = null;
        this.data = null;
    }

    @Override
    public void validateProps()
            throws AOTestValidationException
    {
        super.validateProps();

        validateNonEmptyField(validateFieldPathPrefix() + "reportPath", reportPath);
    }

    @Override
    public String getKind()
    {
        return "OpenReport";
    }

    @Override
    public AOAssertionMode getAssertionsMode()
    {
        return AOAssertionMode.OPTIONAL;
    }

    public void run(AOTaskContext context)
            throws AOException
    {
        final boolean withMdxGeneration = AOStringUtils.isNotEmpty(data);

        if (withMdxGeneration)
        {
            final File data = context.getMDXesDataFolder(this.data);

            final File container = data.getParentFile();

            if (container.exists())
            {
                throw new AOException("existing data folder (remove first) " + container.getAbsolutePath());
            }

            if (!container.mkdirs())
            {
                throw new AOException("could not created the data folder " + container.getAbsolutePath());
            }
        }

        final String restApiURL = context.getRestApiURL();

        // -------------------------------------------------------------------------------------------------------------
        // Mimicking 'Print in Browser' to ensure all widgets are rendered :
        //      regular /viewer is not rendering widgets until they're visible
        //      assertion: ic3printStatus=ic3reportRendered:true
        // -------------------------------------------------------------------------------------------------------------

        final String reportViewerURL = restApiURL.replace("/icCube/api", "/icCube/report/viewer");

        final String reportURL = reportViewerURL
                + "?ic3report=" + encode(reportPath)
                + "&ic3printParams=" + encode(new AOPrintParams(reportViewerURL).toJson());

        final int timeoutS = this.timeoutS != null ? this.timeoutS : 30;

        final String browserContext = context.createBrowserContext();

        final ConcurrentMap<String, String> statements = new ConcurrentHashMap<>();

        try
        {
            try (Session session = context.createBrowserSession(browserContext))
            {
                if (withMdxGeneration)
                {
                    generateMDXes(context, session, statements);
                }

                final AOAuthenticator authenticator = context.getAuthenticator();

                AOLog4jUtils.CHROME.debug("[chrome] navigating to : {}", reportURL);

                if (authenticator.isHeadersAuth())
                {
                    performHeadersAuthLogin(context, authenticator, session);
                }

                session.navigate(reportURL);

                final int timeOutInMillis = 1000 * timeoutS;

                final long start = System.currentTimeMillis();

                session.waitDocumentReady(timeOutInMillis);

                if (authenticator.isFormAuth())
                {
                    performFormAuthLogin(context, authenticator, session);
                }

                final int waitPeriodMS = 1000;

                int newTimoutMillis = Math.max(waitPeriodMS * 2, timeOutInMillis - (int) (System.currentTimeMillis() - start));

                final boolean[] nonExisting = new boolean[]{false};
                final boolean[] printReady = new boolean[]{false};

                final boolean gotIt = session.waitUntil(
                        s ->
                        {
                            if (context.isOnError() /* i.e., another actor on error */)
                            {
                                throw new RuntimeException("interrupted because of other error.");
                            }

                            // Check either for one of this variable :
                            //      ic3openReportStatus = DOCS_REPORT_OPEN_NON_EXISTING
                            //      ic3printStatus      = ic3reportRendered:true

                            if (safeCompareVariable(s, "ic3openReportStatus", "DOCS_REPORT_OPEN_NON_EXISTING"))
                            {
                                nonExisting[0] = true;
                                return true;
                            }

                            if (safeCompareVariable(s, "ic3printStatus", "ic3reportRendered:true"))
                            {
                                printReady[0] = true;
                                return true;
                            }

                            return false;
                        },
                        newTimoutMillis,
                        waitPeriodMS /* Cannot find context with specified id */,
                        true
                );

                AOAssertion.assertTrue("open-report-failed:" + reportPath, gotIt);

                final List<AOOpenReportAssertion> assertions = getOptionalAssertions();

                if (assertions != null && !assertions.isEmpty())
                {
                    for (AOOpenReportAssertion assertion : assertions)
                    {
                        assertion.assertOk(reportPath, nonExisting[0], printReady[0]);
                    }
                }
                else
                {
                    AOAssertion.assertFalse("report-not-existing:" + reportPath, nonExisting[0]);
                    AOAssertion.assertTrue("open-report:" + reportPath, printReady[0]);
                }
            }
        }
        finally
        {
            context.disposeBrowserContext(browserContext);
        }

        if (withMdxGeneration)
        {
            // How to wait for all the events ? But I guess all the 'requestWillBeSent' have been fully processed
            // by now, so it's ok to save them into their respective files.

            final File data = context.getMDXesDataFolder(this.data);

            final File container = data.getParentFile();
            final String pattern = data.getName();

            int mdxNb = 0;

            for (String statement : statements.values())
            {
                final File mdx = new File(container, pattern + "." + mdxNb + ".mdx.txt");

                try
                {
                    Files.writeString(mdx.toPath(), statement, StandardCharsets.UTF_8);
                }
                catch (IOException ex)
                {
                    throw new AOException("could not write the MDX[" + mdxNb + "] statement " + mdx.getAbsolutePath());
                }

                mdxNb++;
            }
        }
    }

    protected void performFormAuthLogin(AOTaskContext context, AOAuthenticator authenticator, Session session)
    {
        final long start = System.currentTimeMillis();

        final int waitPeriodMS = 500;
        final int timeOutInMillis = 1000 * 5;

        int newTimoutMillis = Math.max(waitPeriodMS * 2, timeOutInMillis - (int) (System.currentTimeMillis() - start));

        AOLog4jUtils.SHELL.debug("[chrome] FORM auth.");

        final boolean gotIt = session.waitUntil(
                s ->
                {
                    if (context.isOnError() /* i.e., another actor on error */)
                    {
                        throw new RuntimeException("interrupted because of other error.");
                    }

                    return safeCompareVariable(s, "ic3formAuth", true);
                },
                newTimoutMillis,
                waitPeriodMS /* Cannot find context with specified id */,
                true
        );

        AOLog4jUtils.SHELL.debug("[chrome] FORM auth. : completed");

        AOAssertion.assertTrue("open-report-login-form-failed:" + reportPath, gotIt);

        final String username = authenticator.getUser();
        final String password = authenticator.getPassword();

        AOLog4jUtils.SHELL.debug("[chrome] FORM auth. : login ");

        session.evaluate(
                String.format(
                        "{ document.getElementsByName('j_username').item(0).value = '%s'; document.getElementsByName('j_password').item(0).value = '%s'; document.getElementsByTagName('button').item(0).click(); }",
                        username,
                        password
                )
        );

        AOLog4jUtils.SHELL.debug("[chrome] FORM auth. : login completed");

        AOLog4jUtils.SHELL.debug("[chrome] FORM auth. : wait for document ready");

        session.waitDocumentReady(timeOutInMillis) /* Cannot find context with specified id */;

        AOLog4jUtils.SHELL.debug("[chrome] FORM auth. : wait for document ready completed");
    }

    protected void performHeadersAuthLogin(AOTaskContext context, AOAuthenticator authenticator, Session session)
    {
        final Network network = session.getCommand().getNetwork();

        final Map<String, Object> headers = new HashMap<>();


        for (AOHeader header : authenticator.getHeaders())
        {
            headers.put(header.name, header.value);
        }

        network.setExtraHTTPHeaders(headers);
        network.enable();

        AOLog4jUtils.SHELL.debug("[chrome] headers auth. : completed");
    }

    protected <VALUE> boolean safeCompareVariable(Session session, String name, VALUE expectedValue)
    {
        try
        {
            if (expectedValue instanceof String expectedValueS)
            {
                return expectedValueS.equalsIgnoreCase(session.getVariable(name, String.class));
            }
            else if (expectedValue instanceof Boolean expectedValueB)
            {
                return expectedValueB == session.getVariable(name, Boolean.class);
            }
            else
            {
                throw new RuntimeException("internal error : unexpected value type : " + expectedValue.getClass());
            }
        }
        catch (NullPointerException ex)
        {
            AOLog4jUtils.CHROME.warn("[chrome] could not access {} variable (NPE)", name, ex);
            return false;
        }
        catch (CdpException ex)
        {
            final String error = ex.getMessage();

            if (error != null && error.endsWith("is not defined"))
            {
                AOLog4jUtils.CHROME.debug("[chrome] could not access {} variable (not defined)", name);
                return false;
            }

            AOLog4jUtils.CHROME.debug("[chrome] could not access {} variable (exception)", name, ex);
            return false;
        }
    }

    protected String encode(String value)
    {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * Did not find how to get the response data.
     */
    protected void generateMDXes(AOTaskContext context, Session session, ConcurrentMap<String, String> statements)
    {
        final Network network = session.getCommand().getNetwork();

        network.enable();

        session.addEventListener((event, value) ->
        {
            if (event.name.equals("requestWillBeSent"))
            {
                if (value instanceof RequestWillBeSent request && request.getRequest().getUrl().contains("/icCube/gvi") && request.getRequest().getPostData().contains("executeMdx"))
                {
//                    AOLog4jUtils.CHROME.warn(
//                            "[chrome] event : {} {} {} {}",
//                            event.name,
//                            request.getRequestId(),
//                            request.getRequest().getUrl(),
//                            request.getRequest().getPostData()
//                    );

                    final String decoded = URLDecoder.decode(request.getRequest().getPostData(), StandardCharsets.UTF_8);
                    final String[] parts = decoded.split("&");

                    String statement = null;
                    String initialSelection = null;

                    for (String part : parts)
                    {
                        if (part.startsWith("mdx="))
                        {
                            statement = part.substring("mdx=".length());
                        }
                        else if (part.startsWith("initialSelection="))
                        {
                            initialSelection = part;
                        }
                    }

                    if (statement != null)
                    {
                        if (initialSelection != null)
                        {
                            statement += "\n//" + initialSelection;
                        }
                        statements.put(request.getRequestId(), statement);
                    }
                }
            }
        });
    }

}
