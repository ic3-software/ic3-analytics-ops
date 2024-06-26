package ic3.analyticsops.test.task.mdx;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.common.AOPause;
import ic3.analyticsops.restapi.client.AORestApiClient;
import ic3.analyticsops.restapi.error.AORestApiErrorException;
import ic3.analyticsops.restapi.reply.mdx.AORestApiMdxScriptResult;
import ic3.analyticsops.restapi.reply.tidy.AORestApiTidyTable;
import ic3.analyticsops.restapi.request.AORestApiExecuteMdxRequest;
import ic3.analyticsops.test.*;
import ic3.analyticsops.utils.AOLog4jUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipInputStream;

public class AOMDXesTask extends AOTask
{
    private final String schema;

    /**
     * E.g., data/Sales is using the files 'Sales-N.mdx.txt(and .json)' into the 'data' folder.
     */
    private final String data;

    private final boolean shuffle;

    /**
     * An optional pause applied after the processing of each MDX statement.
     */
    @Nullable
    private final AOPause pauses;

    /**
     * An optional filter : execute this MDX number only.
     */
    @Nullable
    private final Integer filter;

    protected AOMDXesTask()
    {
        // JSON deserialization

        this.schema = null;
        this.data = null;
        this.shuffle = false;
        this.pauses = null;
        this.filter = null;
    }

    @Override
    public void validateProps()
            throws AOTestValidationException
    {
        super.validateProps();

        validateNonEmptyField(validateFieldPathPrefix() + "schema", schema);
        validateNonEmptyField(validateFieldPathPrefix() + "data", data);
    }

    @Override
    public String getKind()
    {
        return "MDXes";
    }

    @Override
    public AOAssertionMode getAssertionsMode()
    {
        return AOAssertionMode.NONE;
    }

    @Override
    public void run(AOTaskContext context)
            throws AOException
    {
        final File data = context.getMDXesDataFolder(this.data);

        final File container = data.getParentFile();
        final String pattern = data.getName();

        // Resolved (//ignore + filter).
        final List<Integer> mdxNBs = mdxNumbers(context, this.data, this.filter);

        if (mdxNBs.isEmpty())
        {
            throw new AOException("no run for MDX for data " + container.getAbsolutePath() + "/" + pattern);
        }

        if (shuffle)
        {
            Collections.shuffle(mdxNBs);
        }

        for (final int mdxNB : mdxNBs)
        {
            try
            {
                if (context.isOnError())
                {
                    break /* i.e., another actor on error */;
                }

                Integer tidyMaxRowCount = null;
                double delta = 0;

                String mdx;

                try
                {
                    final List<String> lines = getMdx(container, pattern, mdxNB);

                    if (lines == null || lines.isEmpty())
                    {
                        break;
                    }

                    for (String line : lines)
                    {
                        if (line.startsWith("//delta:"))
                        {
                            final String input = line.substring("//delta:".length());
                            delta = Double.parseDouble(input);
                        }
                        else if (line.startsWith("//tidyMaxRowCount:"))
                        {
                            final String input = line.substring("//tidyMaxRowCount:".length());
                            tidyMaxRowCount = Integer.parseInt(input);
                        }
                    }

                    mdx = String.join("\n", lines);
                }
                catch (IOException ex)
                {
                    throw new AOException("could not retrieve the MDX file " + container.getAbsolutePath() + "/" + pattern + ":" + mdxNB, ex);
                }

                // Expected Result.

                context.markForExpectedResult();

                AORestApiMdxScriptResult expectedReply;

                try
                {
                    expectedReply = getResult(container, pattern, mdxNB);

                    if (expectedReply == null)
                    {
                        throw new AOException("could not retrieve the MDX result file " + container.getAbsolutePath() + "/" + pattern + ":" + mdxNB);
                    }

                    context.prettyPrint(expectedReply);
                }
                catch (IOException ex)
                {
                    throw new AOException("could not retrieve the MDX result file " + container.getAbsolutePath() + "/" + pattern + ":" + mdxNB, ex);
                }

                final AORestApiTidyTable<?> expectedResult = assertOnlyDataset(expectedReply);

                // Single dataset only for now.

                context.markForActualResult();

                final AORestApiMdxScriptResult actualReply = context.sendRequest(

                        new AORestApiExecuteMdxRequest()
                                .schema(schema)
                                .tidyMaxRowCount(tidyMaxRowCount)
                                .mdx(mdx)

                );

                final AORestApiTidyTable<?> actualResult = assertOnlyDataset(actualReply);

                expectedResult.assertEquals(actualResult, delta);

                final Long pauseMS = pauses != null ? pauses.pauseMS() : null;

                if (pauseMS != null)
                {
                    final long startPauseMS = System.currentTimeMillis();

                    context.pause(pauseMS);

                    context.onRunPaused(System.currentTimeMillis() - startPauseMS);
                }
            }
            catch (Throwable ex)
            {
                AOLog4jUtils.ACTOR.error("[actor] 'MDXes' : unexpected error while processing the MDX[{}]", mdxNB);
                throw ex;
            }
        }
    }

    public static List<Integer> mdxNumbers(AOTaskContext context, String data, @Nullable Integer filter)
            throws AOException
    {
        final File dataF = context.getMDXesDataFolder(data);

        final File container = dataF.getParentFile();
        final String pattern = dataF.getName();

        final List<Integer> mdxNumbers = new ArrayList<>();

        int mdxNb = 0;

        while (true)
        {
            boolean ignore = false;

            try
            {
                final List<String> lines = getMdx(container, pattern, mdxNb);

                if (lines == null || lines.isEmpty())
                {
                    break;
                }

                for (String line : lines)
                {
                    if (line.startsWith("//ignore"))
                    {
                        ignore = true;
                        break;
                    }
                }
            }
            catch (IOException ex)
            {
                throw new AOException("could not retrieve the MDX file " + container.getAbsolutePath() + "/" + pattern + ":" + mdxNb, ex);
            }

            if (filter != null && filter != mdxNb)
            {
                ignore = true;
            }

            if (!ignore)
            {
                mdxNumbers.add(mdxNb++);
            }
        }

        return mdxNumbers;
    }

    public static AORestApiTidyTable<?> assertOnlyDataset(AORestApiMdxScriptResult result)
    {
        if (result.results == null)
        {
            if (result.error != null)
            {
                throw new AssertionError("missing results (unexpected-error) : " + result.error);
            }
            throw new AssertionError("missing results");
        }

        AOAssertion.assertEquals("result size", 1, result.results.size());

        return result.results.getFirst().dataSet;
    }

    public static List<String> getMdx(File container, String pattern, int mdxNb)
            throws IOException
    {
        final File content = new File(container, pattern + "." + mdxNb + ".mdx.txt");

        if (!content.exists())
        {
            return null;
        }

        return Files.readAllLines(content.toPath(), StandardCharsets.UTF_8);
    }

    public static AORestApiMdxScriptResult getResult(File container, String pattern, int mdxNb)
            throws AORestApiErrorException,
                   IOException
    {
        final String name = pattern + "." + mdxNb + ".mdx.json";

        File content = new File(container, name + ".zip");

        // Zip'd file ?

        if (content.exists())
        {
            try (final ZipInputStream is = new ZipInputStream(new BufferedInputStream(new FileInputStream(content))))
            {
                if (is.getNextEntry() == null)
                {
                    throw new IOException("missing entry from " + content.getAbsolutePath());
                }

                return AORestApiClient.parseReply(AORestApiMdxScriptResult.class, is, false);
            }
        }

        content = new File(container, name);

        // Unzip'd file ?

        if (content.exists())
        {
            try (final InputStream is = new BufferedInputStream(new FileInputStream(content)))
            {
                return AORestApiClient.parseReply(AORestApiMdxScriptResult.class, is, false);
            }
        }

        return null;
    }
}
