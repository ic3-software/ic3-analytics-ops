package ic3.analyticsops.test.task.reporting;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.test.*;
import ic3.analyticsops.test.assertion.AOOpenReportAssertion;
import ic3.analyticsops.utils.AOLog4jUtils;
import ic3.analyticsops.utils.AOStringUtils;
import io.webfolder.cdp.command.Network;
import io.webfolder.cdp.event.network.LoadingFinished;
import io.webfolder.cdp.event.network.RequestWillBeSent;
import io.webfolder.cdp.exception.CdpException;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.type.network.GetResponseBodyResult;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AOOpenReportTask extends AOTask<AOOpenReportAssertion>
{
    private final String reportPath;

    @Nullable
    private final Integer timeoutS;

    /**
     * When present, MDX (and their results) from the report will be extracted and saved there.
     * <pre>
     * E.g., data/Sales is using the files 'Sales-N.mdx.txt(and .json)' into the 'data' folder.
     * E.g., data/ is using the files 'ReportName-N.mdx.txt(and .json)' into the 'data' folder.
     * </pre>
     *
     * @see ic3.analyticsops.test.task.mdx.AOGenerateMDXesTask
     * @see ic3.analyticsops.test.task.mdx.AOMDXesTask
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
            final File data = context.getMDXesDataFolder(fixDataForReportName(this.reportPath, this.data));

            final File container = data.getParentFile();

            if (container.exists())
            {
                // Several OpenReport in the same folder...
                if (!context.hasCreatedContainerFolder(container))
                {
                    throw new AOException("existing data folder (remove first) " + container.getAbsolutePath());
                }
            }
            else
            {
                if (!container.mkdirs())
                {
                    throw new AOException("could not created the data folder " + container.getAbsolutePath());
                }

                context.onContainerFolderCreated(container);
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

        // Populated from an event-listener.
        final ConcurrentMap<String, String> statements = new ConcurrentHashMap<>();
        final ConcurrentMap<String, String> results = new ConcurrentHashMap<>();

        try
        {
            try (Session session = context.createBrowserSession(browserContext))
            {
                if (withMdxGeneration || isWithMdxAssertions())
                {
                    generateMDXes(context, session, statements, results);
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
                        assertion.assertOk(context, reportPath, nonExisting[0], printReady[0], statements, results);
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

            // Ensure first statements/results are consistent.

            if (results.size() != statements.size())
            {
                throw new AOException("unexpected result count [results : " + results.size() + "] [statements : " + statements.size() + "]");
            }

            final File data = context.getMDXesDataFolder(fixDataForReportName(this.reportPath, this.data));

            final File container = data.getParentFile();
            final String pattern = data.getName();

            int mdxNb = 0;

            for (Map.Entry<String, String> entry : statements.entrySet())
            {
                final String requestId = entry.getKey();

                final String statement = entry.getValue();
                final String response = results.get(requestId);

                if (response == null)
                {
                    throw new AOException("missing result for request [" + requestId + "]");
                }

                final File mdx = new File(container, pattern + "." + mdxNb + ".mdx.txt");

                try
                {
                    Files.writeString(mdx.toPath(), statement, StandardCharsets.UTF_8);
                }
                catch (IOException ex)
                {
                    throw new AOException("could not write the MDX[" + mdxNb + "] statement " + mdx.getAbsolutePath());
                }

                final File result = new File(container, pattern + "." + mdxNb + ".mdx.json.zip");

                try (final ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(result))))
                {
                    final String name = result.getName();

                    zip.putNextEntry(new ZipEntry(name.substring(0, name.length() - 4)));
                    zip.write(response.getBytes(StandardCharsets.UTF_8));
                }
                catch (IOException ex)
                {
                    throw new AOException("could not write the MDX[" + mdxNb + "] result " + result.getAbsolutePath());
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

    protected void generateMDXes(AOTaskContext context, Session session, ConcurrentMap<String, String> statements, ConcurrentMap<String, String> results)
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

                    final String[] parts = request.getRequest().getPostData().split("&");

                    String statement = null;
                    String widget = null;
                    String tidyMaxRowCount = null;
                    String initialSelection = null;

                    for (String part : parts)
                    {
                        if (part.startsWith("mdx="))
                        {
                            statement = part.substring("mdx=".length());
                            statement = URLDecoder.decode(statement, StandardCharsets.UTF_8);
                        }
                        else if (part.startsWith("widgetUId="))
                        {
                            widget = part;
                            widget = URLDecoder.decode(widget, StandardCharsets.UTF_8);
                        }
                        else if (part.startsWith("tidyMaxRowCount="))
                        {
                            tidyMaxRowCount = part;
                            tidyMaxRowCount = URLDecoder.decode(tidyMaxRowCount, StandardCharsets.UTF_8);
                        }
                        else if (part.startsWith("initialSelection="))
                        {
                            initialSelection = part;
                            initialSelection = URLDecoder.decode(initialSelection, StandardCharsets.UTF_8);
                        }
                    }

                    if (statement != null)
                    {
                        String prefix = "";

                        if (widget != null)
                        {
                            // Troubleshooting purpose.
                            prefix += "//" + widget.replace("=", ":") + "\n";
                        }
                        if (tidyMaxRowCount != null)
                        {
                            // Sent to the TidyExecuteMdxScript as a regular parameter (see MDXes)
                            prefix += "//" + tidyMaxRowCount.replace("=", ":") + "\n";
                        }

                        String suffix = "";

                        if (initialSelection != null)
                        {
                            // Sent to the server that is extracting this info. while parsing the MDX.
                            suffix += "\n//" + initialSelection;
                        }

                        statement = prefix + statement + suffix;

                        statements.put(request.getRequestId(), statement);
                    }
                }
            }
            else if (event.name.equals("loadingFinished"))
            {
                if (value instanceof LoadingFinished loading && statements.containsKey(loading.getRequestId()))
                {
                    final GetResponseBodyResult body = network.getResponseBody(loading.getRequestId());
                    final String json = body.getBody();

                    // Converting from the GVI 'executeMDX' response to a REST API 'TidyExecuteMdxScript' response...

                    final String dataSetPrefix = """
                            {"version":1,"status":"ok","payload":{"results":[{"dataSet":""";

                    final String tablePrefix = """
                            {"version":1,"status":"ok","table":""";

                    // icCube 8.6.0 : extra. info field ...

                    final String tablePrefix860 = """
                            {"version":1,"status":"ok","info":{""";

                    if (json.startsWith(tablePrefix))
                    {
                        final String table = json.substring(tablePrefix.length(), json.length() - 1);

                        results.put(loading.getRequestId(), dataSetPrefix + table + "}]}}");
                    }
                    else if (json.startsWith(tablePrefix860))
                    {
                        final int pos = json.indexOf("\"table\":{");
                        final String table = json.substring(pos + 8, json.length() - 1);

                        results.put(loading.getRequestId(), dataSetPrefix + table + "}]}}");
                    }
                    else
                    {
                        AOLog4jUtils.CHROME.error("[chrome] unexpected result format : {}", json.substring(0, 64));
                    }
                }
            }
        });
    }

    public static String fixDataForReportName(String reportPath, String data)
    {
        if (data.endsWith("/"))
        {
            final String[] parts = reportPath.split("/");
            final String name = parts[parts.length - 1];

            return data + name;
        }

        return data;
    }

    protected boolean isWithMdxAssertions()
    {
        final List<AOOpenReportAssertion> assertions = getOptionalAssertions();

        if (assertions != null && !assertions.isEmpty())
        {
            for (AOOpenReportAssertion assertion : assertions)
            {
                if (assertion.isWithMdxAssertion())
                {
                    return true;
                }
            }
        }

        return false;
    }

}
