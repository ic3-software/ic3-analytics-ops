package ic3.analyticsops.test.task.mdx;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.reply.mdx.AORestApiMdxScriptResult;
import ic3.analyticsops.restapi.request.AORestApiExecuteMdxRequest;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;

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
    private String schema;

    private String data;

    private List<String> statements;

    @Override
    public String getKind()
    {
        return "GenerateMDXes";
    }

    @Override
    public void run(AOTaskContext context)
            throws AORestApiException
    {
        final File data = context.getMDXesDataFolder(this.data);

        final File container = data.getParentFile();
        final String pattern = data.getName();

        if (container.exists())
        {
            throw new AORestApiException("existing data folder (remove first) " + container.getAbsolutePath());
        }

        if (!container.mkdirs())
        {
            throw new AORestApiException("could not created the data folder " + container.getAbsolutePath());
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

            final File mdx = new File(container, pattern + "-" + ii + ".mdx.txt");

            try
            {
                Files.writeString(mdx.toPath(), statement, StandardCharsets.UTF_8);
            }
            catch (IOException ex)
            {
                throw new AORestApiException("could not write the MDX[" + ii + "] statement " + mdx.getAbsolutePath());
            }

            final File result = new File(container, pattern + "-" + ii + ".mdx.json.zip");

            try (final ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(result))))
            {
                final String name = result.getName();

                zip.putNextEntry(new ZipEntry(name.substring(0, name.length() - 4)));
                zip.write(json.getBytes(StandardCharsets.UTF_8));
            }
            catch (IOException ex)
            {
                throw new AORestApiException("could not write the MDX[" + ii + "] result " + mdx.getAbsolutePath());
            }
        }
    }
}
