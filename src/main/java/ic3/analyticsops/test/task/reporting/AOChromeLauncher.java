package ic3.analyticsops.test.task.reporting;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.Options;
import io.webfolder.cdp.channel.ChannelFactory;
import io.webfolder.cdp.channel.Connection;
import io.webfolder.cdp.channel.JreWebSocketFactory;
import io.webfolder.cdp.channel.WebSocketConnection;
import io.webfolder.cdp.exception.CdpException;
import io.webfolder.cdp.logger.CdpLogger;
import io.webfolder.cdp.logger.CdpLoggerFactory;
import io.webfolder.cdp.process.CdpProcess;
import io.webfolder.cdp.session.SessionFactory;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import static java.lang.Long.toHexString;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static java.util.concurrent.ThreadLocalRandom.current;

public class AOChromeLauncher extends Launcher
{
    private final static ExecutorService executor = ForkJoinPool.commonPool();

    /**
     * Reference to our parent options (private).
     */
    private final Options options;

    /**
     * Reference to our parent channelFactory (private) to override launchWithProcessBuilder().
     */
    private final ChannelFactory channelFactory;

    /**
     * Reference to our parent log (private).
     */
    private final CdpLogger log;

    private String chromePath = "";

    @Nullable
    private String listening;

    public AOChromeLauncher(Options options, ChannelFactory webSocketFactory)
    {
        super(options, webSocketFactory);

        this.options = options;
        this.channelFactory = webSocketFactory;
        this.log = new CdpLoggerFactory(options).getLogger("cdp4j.launcher");
    }

    public static ChannelFactory createChannelFactory()
    {
        return new JreWebSocketFactory(executor);
    }

    @Override
    public String findChrome()
    {
        return (chromePath = super.findChrome());
    }

    public String getChromePath()
    {
        return chromePath;
    }

    @Nullable
    public String getListening()
    {
        return listening;
    }

    /**
     * Same as our parent.launchWithProcessBuilder but with more logs (Docker investigation).
     */
    @Override
    protected SessionFactory launchWithProcessBuilder(List<String> arguments)
    {
        for (int ii = 0; ii < arguments.size(); ii++)
        {
            final String arg = arguments.get(ii);

            if (arg.equals("--headless"))
            {
                arguments.set(ii, "--headless=new");
            }
        }

        final String cdp4jId = toHexString(current().nextLong());
        arguments.add(format("--cdp4jId=%s", cdp4jId));
        Connection connection = null;
        ProcessBuilder builder = new ProcessBuilder(arguments);
        builder.environment().put("CDP4J_ID", cdp4jId);

        log.info("launcher: {}", "ProcessBuilder:arguments:" + arguments);

        try
        {
            final Process process = builder.start();

            try (Scanner scanner = new Scanner(process.getErrorStream()))
            {
                while (scanner.hasNext())
                {
                    final String line = scanner.nextLine().trim();

                    log.info("launcher: {}", "ProcessBuilder:stderr:" + line);

                    if (line.isEmpty())
                    {
                        continue;
                    }

                    if (line.toLowerCase(ENGLISH).startsWith("devtools listening on"))
                    {
                        final int start = line.indexOf("ws://");
                        final String url = line.substring(start);

                        connection = new WebSocketConnection(url);
                        listening = url;

                        break;
                    }
                }

                if (connection == null)
                {
                    throw new CdpException("Could not retrieve the WebSocket connection URL (missing Chrome/Chromium process?).");
                }
            }

            if (!process.isAlive())
            {
                throw new CdpException("The Chrome/Chromium process is not alive.");
            }

            options.processManager().setProcess(new CdpProcess(process, cdp4jId));

            final URL url = new URL(connection.getUrl().replace("ws://", "http://"));
            log.info("DevTools remote debugging URL: http://{}:{}", url.getHost(), url.getPort());

            return new SessionFactory(options, channelFactory, connection);

        }
        catch (IOException e)
        {
            throw new CdpException(e);
        }
    }

}
