package ic3.analyticsops.test.assertion;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.client.AORestApiClient;
import ic3.analyticsops.restapi.reply.mdx.AORestApiMdxScriptResult;
import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTable;
import ic3.analyticsops.test.AOAssertion;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.AOTestValidationException;
import ic3.analyticsops.test.task.mdx.AOMDXesTask;
import ic3.analyticsops.test.task.reporting.AOOpenReportTask;
import ic3.analyticsops.utils.AOLog4jUtils;
import ic3.analyticsops.utils.AOStringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AOOpenReportAssertion extends AOAssertion
{
    /**
     * Nullable to ensure it has been defined by the JSON5 for sure.
     */
    @Nullable
    private final Boolean missing;

    /**
     * When present, MDX (and their results) from the report will be asserted for non-regression.
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

    /**
     * When present, ignore these MDX requests.
     */
    @Nullable
    private final Set<Integer> ignored;

    protected AOOpenReportAssertion()
    {
        // JSON deserialization

        this.missing = null;
        this.data = null;
        this.ignored = null;
    }

    @Override
    public void validate()
            throws AOTestValidationException
    {
        super.validate();

        if (AOStringUtils.isEmpty(data))
        {
            validateNonEmptyField(validateFieldPathPrefix() + "missing", missing);
        }
    }

    public boolean isWithMdxAssertion()
    {
        return AOStringUtils.isNotEmpty(data);
    }

    public void assertOk(AOTaskContext context, String reportPath, boolean nonExisting, boolean printReady, @Nullable Map<String, String> statements, @Nullable Map<String, String> results)
            throws AOException
    {
        if (isWithMdxAssertion())
        {
            AOAssertion.assertFalse("report-not-existing:" + reportPath, nonExisting);
            AOAssertion.assertTrue("open-report:" + reportPath, printReady);

            assertOkForNonRegression(context, reportPath, statements, results);
        }
        else
        {
            if (missing == null /* should have been validated by now */)
            {
                throw new AssertionError("unexpected missing field 'missing'");
            }

            if (missing)
            {
                AOAssertion.assertTrue("report-not-existing:" + reportPath, nonExisting);
                AOAssertion.assertFalse("open-report:" + reportPath, printReady);
            }
            else
            {
                AOAssertion.assertFalse("report-not-existing:" + reportPath, nonExisting);
                AOAssertion.assertTrue("open-report:" + reportPath, printReady);
            }
        }
    }

    private void assertOkForNonRegression(AOTaskContext context, String reportPath, @Nullable Map<String, String> statements, @Nullable Map<String, String> results)
            throws AOException
    {
        if (AOStringUtils.isEmpty(data))
        {
            throw new RuntimeException("internal error : missing OpenReport data configuration");
        }

        if (statements == null || statements.isEmpty())
        {
            throw new RuntimeException("internal error : missing OpenReport actual statements");
        }

        if (results == null || results.isEmpty())
        {
            throw new RuntimeException("internal error : missing OpenReport actual results");
        }

        if (statements.size() != results.size())
        {
            throw new RuntimeException("internal error : OpenReport statements/results count mismatch [" + statements.size() + "] [" + results.size() + "]");
        }

        final List<Integer> mdxNBs = AOMDXesTask.mdxNumbers(
                context, AOOpenReportTask.fixDataForReportName(reportPath, data), null
        );

        AOAssertion.assertEquals("open-report statement count", mdxNBs.size(), statements.size());

        for (int mdxNB : mdxNBs)
        {
            if (context.isOnError())
            {
                break /* i.e., another actor on error */;
            }

            if (ignored != null && ignored.contains(mdxNB))
            {
                continue;
            }

            try
            {
                final String expectedMdx = getExpectedMdx(context, reportPath, this.data, mdxNB);

                // System.out.println(expectedMdx);

                final AORestApiMdxScriptResult expectedReply = getExpectedReply(context, reportPath, this.data, mdxNB);
                final AORestApiTidyTable<?> expectedResult = AOMDXesTask.assertOnlyDataset(expectedReply);

                // expectedResult.prettyPrint(System.out);

                final AORestApiMdxScriptResult actualReply = getActualResult(expectedMdx, statements, results);
                final AORestApiTidyTable<?> actualResult = AOMDXesTask.assertOnlyDataset(actualReply);

                // actualResult.prettyPrint(System.out);

                // Needs more works : wait & see.
                final double delta = 0;

                expectedResult.assertEquals(actualResult, delta);
            }
            catch (Throwable ex)
            {
                AOLog4jUtils.ACTOR.error("[actor] 'OpenReport' : unexpected error while processing the MDX[{}] Report[{}]", mdxNB, reportPath);
                throw ex;
            }
        }
    }

    private static String getExpectedMdx(AOTaskContext context, String reportPath, String data, int mdxNB)
            throws AOException
    {
        final File dataF = context.getMDXesDataFolder(AOOpenReportTask.fixDataForReportName(reportPath, data));

        final File container = dataF.getParentFile();
        final String pattern = dataF.getName();

        try
        {
            final List<String> lines = AOMDXesTask.getMdx(container, pattern, mdxNB);

            if (lines == null || lines.isEmpty())
            {
                throw new AOException("could not retrieve the MDX file " + container.getAbsolutePath() + "/" + pattern + ":" + mdxNB);
            }

            return String.join("\n", lines);
        }
        catch (IOException ex)
        {
            throw new AOException("could not retrieve the MDX file " + container.getAbsolutePath() + "/" + pattern + ":" + mdxNB, ex);
        }
    }

    private static AORestApiMdxScriptResult getExpectedReply(AOTaskContext context, String reportPath, String data, int mdxNB)
            throws AOException
    {
        final File dataF = context.getMDXesDataFolder(AOOpenReportTask.fixDataForReportName(reportPath, data));

        final File container = dataF.getParentFile();
        final String pattern = dataF.getName();

        try
        {
            return AOMDXesTask.getResult(container, pattern, mdxNB);
        }
        catch (IOException ex)
        {
            throw new AOException("could not retrieve the MDX result file " + container.getAbsolutePath() + "/" + pattern + ":" + mdxNB, ex);
        }
    }

    private static AORestApiMdxScriptResult getActualResult(String mdx, Map<String, String> statements, Map<String, String> results)
            throws AOException
    {
        mdx = mdx.trim();

        for (Map.Entry<String, String> entry : statements.entrySet())
        {
            final String actualMdx = entry.getValue().trim();

            if (mdx.equals(actualMdx))
            {
                final String requestId = entry.getKey();
                final String result = results.get(requestId);

                final InputStream is = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));

                try
                {
                    return AORestApiClient.parseReply(AORestApiMdxScriptResult.class, is, false);
                }
                catch (IOException ex)
                {
                    throw new AOException("could not retrieve the MDX result", ex);
                }
            }
        }

        throw new AOException("could not retrieve the MDX result");
    }

}
