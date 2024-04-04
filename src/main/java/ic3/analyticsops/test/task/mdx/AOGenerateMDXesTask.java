package ic3.analyticsops.test.task.mdx;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.reply.mdx.AORestApiMdxScriptResult;
import ic3.analyticsops.restapi.request.AORestApiExecuteMdxRequest;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.AOTestValidationException;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AOGenerateMDXesTask extends AOTask
{
    private final String schema;

    private final String data;

    @Nullable
    private final List<String> statements;

    protected AOGenerateMDXesTask()
    {
        // JSON deserialization

        this.schema = null;
        this.data = null;
        this.statements = null;
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
        return "GenerateMDXes";
    }

    @Override
    public boolean withAssertions()
    {
        return false;
    }

    @Override
    public void run(AOTaskContext context)
            throws AOException
    {
        if (statements != null)
        {
            runWithStatements(context);
        }
        else
        {
            runWithoutStatements(context);
        }
    }

    protected void runWithStatements(AOTaskContext context)
            throws AOException
    {
        final File data = context.getMDXesDataFolder(this.data);

        final File container = data.getParentFile();
        final String pattern = data.getName();

        if (container.exists())
        {
            throw new AOException("existing data folder (remove first) " + container.getAbsolutePath());
        }

        if (!container.mkdirs())
        {
            throw new AOException("could not created the data folder " + container.getAbsolutePath());
        }

        for (int ii = 0; ii < statements.size(); ii++)
        {
            final String statement = statements.get(ii);

            final AORestApiMdxScriptResult reply = context.sendRequestWithJson(

                    new AORestApiExecuteMdxRequest()
                            .schema(schema)
                            .mdx(statement)

            );

            final String json = reply.json;

            final File mdx = new File(container, pattern + "." + ii + ".mdx.txt");

            try
            {
                Files.writeString(mdx.toPath(), statement, StandardCharsets.UTF_8);
            }
            catch (IOException ex)
            {
                throw new AOException("could not write the MDX[" + ii + "] statement " + mdx.getAbsolutePath());
            }

            final File result = new File(container, pattern + "." + ii + ".mdx.json.zip");

            try (final ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(result))))
            {
                final String name = result.getName();

                zip.putNextEntry(new ZipEntry(name.substring(0, name.length() - 4)));
                zip.write(json.getBytes(StandardCharsets.UTF_8));
            }
            catch (IOException ex)
            {
                throw new AOException("could not write the MDX[" + ii + "] result " + result.getAbsolutePath());
            }
        }
    }

    /**
     * NO statements defined : (re-)generate the result files from existing input files.
     */
    protected void runWithoutStatements(AOTaskContext context)
            throws AOException
    {
        final File data = context.getMDXesDataFolder(this.data);

        final File container = data.getParentFile();
        final String pattern = data.getName();

        if (!container.exists())
        {
            throw new AOException("missing data folder " + container.getAbsolutePath());
        }

        // Similarly to the MDXes task, locate existing MDX input files.

        int mdxNb = 0;

        while (true)
        {
            boolean ignore = false;

            String mdx;

            try
            {
                final List<String> lines = AOMDXesTask.getMdx(container, pattern, mdxNb);

                if (lines == null || lines.isEmpty())
                {
                    break;
                }

                for (String line : lines)
                {
                    if (line.startsWith("//ignore"))
                    {
                        ignore = true;
                    }
                }

                mdx = String.join("\n", lines);

            }
            catch (IOException ex)
            {
                throw new AOException("could not retrieve the MDX file " + container.getAbsolutePath() + "/" + pattern + ":" + mdxNb, ex);
            }

            if (!ignore)
            {
                final AORestApiMdxScriptResult reply = context.sendRequestWithJson(

                        new AORestApiExecuteMdxRequest()
                                .schema(schema)
                                .mdx(mdx)

                );

                final String json = reply.json;

                final File result = new File(container, pattern + "." + mdxNb + ".mdx.json.zip");

                try (final ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(result))))
                {
                    final String name = result.getName();

                    zip.putNextEntry(new ZipEntry(name.substring(0, name.length() - 4)));
                    zip.write(json.getBytes(StandardCharsets.UTF_8));
                }
                catch (IOException ex)
                {
                    throw new AOException("could not write the MDX[" + mdxNb + "] result " + result.getAbsolutePath());
                }
            }

            mdxNb++;
        }
    }
}
