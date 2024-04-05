package ic3.analyticsops.test.task.reporting;

import ic3.analyticsops.common.AOLoggers;
import ic3.analyticsops.utils.AOStringUtils;
import io.webfolder.cdp.Constant;
import io.webfolder.cdp.Options;
import io.webfolder.cdp.channel.ChannelFactory;
import io.webfolder.cdp.command.Browser;
import io.webfolder.cdp.exception.CdpException;
import io.webfolder.cdp.logger.CdpLoggerType;
import io.webfolder.cdp.process.AdaptiveProcessManager;
import io.webfolder.cdp.process.ProcessManager;
import io.webfolder.cdp.session.Command;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import io.webfolder.cdp.type.browser.GetVersionResult;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Support for OpenReport : using a local Chrome|Chromium instance.
 */
public class AOChromeProxy
{
    private final ReentrantLock restartLOCK = new ReentrantLock();

    @Nullable
    private volatile Options options;

    @Nullable
    private volatile ChannelFactory webSocketFactory;

    @Nullable
    private volatile AOChromeLauncher launcher;

    @Nullable
    private volatile SessionFactory sessionFactory;

    @Nullable
    private volatile GetVersionResult version;

    /**
     * True if Chrome has been launched successfully at least once.
     */
    private volatile boolean launched;

    public AOChromeProxy()
    {
        this.launched = false;
    }

    public int getReadingTimeoutMS()
    {
        return 60_000 /* dunno yet */;
    }

    public File getRemoteProfileData()
            throws AOChromeException
    {
        final long nowMS = System.currentTimeMillis();
        final SimpleDateFormat df = new SimpleDateFormat("HH_mm_ss");
        final String ts = df.format(nowMS);

        final File tmp = new File(System.getProperty("java.io.tmpdir") + "/chrome-remote-profile", ts);

        if (!tmp.mkdirs())
        {
            throw new AOChromeException("could not create the remote-profile-data folder : " + tmp.getAbsolutePath());
        }

        return tmp;
    }

    @Nullable
    public String getExec()
    {
        return null /* dunno yet */;
    }

    @Nullable
    public String getExecOptions()
    {
        return null /* dunno yet */;
    }

    public String createBrowserContext()
            throws AOChromeException
    {
        final SessionFactory sf = getOrCreateSessionFactory();

        if (sf == null)
        {
            throw new AOChromeException("could not create browser-context (session-factory null)");
        }

        if (sf.closed())
        {
            throw new AOChromeException("could not create browser-context (session-factory closed)");
        }

        return sf.createBrowserContext();
    }

    public void disposeBrowserContext(String browserContext)
    {
        if (browserContext == null)
        {
            return;
        }

        final SessionFactory sf = sessionFactory;

        if (sf == null)
        {
            return;
        }

        if (sf.closed())
        {
            return;
        }

        sf.disposeBrowserContext(browserContext);
    }

    public Session createBrowserSession(String browserContext)
            throws AOChromeException
    {
        final SessionFactory sf = sessionFactory;

        if (sf == null)
        {
            return null;
        }

        if (sf.closed())
        {
            throw new AOChromeException("could not create browser-session (session-factory closed)");
        }

        return sf.create(browserContext);
    }

    @Nullable
    protected SessionFactory getOrCreateSessionFactory()
    {
        SessionFactory sf = sessionFactory;

        if (sf == null)
        {
            AOLoggers.CHROME.debug("[chrome] getOrCreateSessionFactory: the session-factory is null");
        }
        else if (sf.closed())
        {
            AOLoggers.CHROME.debug("[chrome] getOrCreateSessionFactory: the sessionFactory is closed");

            sf = null;
        }

        if (sf == null)
        {
            AOLoggers.CHROME.debug("[chrome] getOrCreateSessionFactory: restarting Chrome");

            try
            {
                restart(false);

                sf = sessionFactory;

                AOLoggers.CHROME.debug("[chrome] getOrCreateSessionFactory: Chrome restarted [SF:{}]", sf != null);

            }
            catch (AOChromeException ex)
            {
                AOLoggers.CHROME.error("[chrome] getOrCreateSessionFactory: failed to restart Chrome", ex);

                sf = null;
            }
        }

        return sf;
    }

    protected void restart(boolean forced)
            throws AOChromeException
    {
        try
        {
            restartLOCK.lockInterruptibly();

            try
            {
                final SessionFactory sf = sessionFactory;

                if (forced || sf == null || sf.closed())
                {
                    AOLoggers.CHROME.debug("[chrome] getOrCreateSessionFactory: restarting Chrome: still required [forced:{}]", forced);

                    shutdown();
                    setupAndLaunch();
                }
                else
                {
                    AOLoggers.CHROME.debug("[chrome] getOrCreateSessionFactory: restarting Chrome: not required");
                }
            }
            finally
            {
                restartLOCK.unlock();
            }
        }
        catch (InterruptedException ignored)
        {
        }
    }

    public void setupAndLaunch()
            throws AOChromeException
    {
        AOLoggers.CHROME.debug("[chrome] ");
        AOLoggers.CHROME.debug("[chrome] Chrome|Chromium headless setup (and launch)");
        AOLoggers.CHROME.debug("[chrome] ");
        AOLoggers.CHROME.debug("[chrome]                       OS : " + Constant.OS_NAME);
        AOLoggers.CHROME.debug("[chrome]                    Linux : " + Constant.LINUX);
        AOLoggers.CHROME.debug("[chrome]                  Windows : " + Constant.WINDOWS);
        AOLoggers.CHROME.debug("[chrome]                      OSX : " + Constant.OSX);
        AOLoggers.CHROME.debug("[chrome] ");
        AOLoggers.CHROME.debug("[chrome]   ICCUBE_NO_SAFE_PROCESS : " + System.getenv("ICCUBE_NO_SAFE_PROCESS"));
        AOLoggers.CHROME.debug("[chrome] ICCUBE_CHROME_NO_SANDBOX : " + System.getenv("ICCUBE_CHROME_NO_SANDBOX"));
        AOLoggers.CHROME.debug("[chrome] ");
        AOLoggers.CHROME.debug("[chrome]          reading timeout : " + getReadingTimeoutMS() + "ms");
        AOLoggers.CHROME.debug("[chrome] ");

        options = setupOptions();

        AOLoggers.CHROME.debug("[chrome] Chrome|Chromium options");
        AOLoggers.CHROME.debug("[chrome] ");
        AOLoggers.CHROME.debug("[chrome] --user-data-dir=" + options.userDataDir());

        for (String argument : options.arguments())
        {
            AOLoggers.CHROME.debug("[chrome] " + argument);
        }

        AOLoggers.CHROME.debug("[chrome] ");

        webSocketFactory = AOChromeLauncher.createChannelFactory();

        launcher = new AOChromeLauncher(options, webSocketFactory);

        if (!launcher.isChromeInstalled())
        {
            throw new AOChromeException("Chrome is not installed");
        }

        AOLoggers.CHROME.debug("[chrome]          chrome : " + launcher.getChromePath());
        AOLoggers.CHROME.debug("[chrome]       listening : " + launcher.getListening());

        launchChrome();
    }

    public Options setupOptions()
            throws AOChromeException
    {
        final File remoteProfileData = getRemoteProfileData();

        Options.Builder builder = Options.builder()
                .headless(true)
                .loggerType(CdpLoggerType.Log4j)
                .readTimeout(getReadingTimeoutMS())
                .userDataDir(remoteProfileData.toPath());

        // -------------------------------------------------------------------------------------------------------------

        final String noSafeProcess = System.getenv("ICCUBE_NO_SAFE_PROCESS");

        if ("1".equals(noSafeProcess))
        {
            builder.safeProcessPath("");
        }

        // -------------------------------------------------------------------------------------------------------------

        final String exec = getExec();

        if (AOStringUtils.isNotEmpty(exec))
        {
            builder = builder.browserExecutablePath(exec);
        }

        // -------------------------------------------------------------------------------------------------------------

        final List<String> arguments = new ArrayList<>();
        {
            arguments.add("--disable-gpu");
        }

        final String noSandbox = System.getenv("ICCUBE_CHROME_NO_SANDBOX");

        if ("1".equals(noSandbox))
        {
            arguments.add("--no-sandbox");
        }

        final String execOptions = getExecOptions();

        if (AOStringUtils.isNotEmpty(execOptions))
        {
            arguments.addAll(Arrays.asList(execOptions.split(" ")));
        }

        builder = builder.arguments(arguments);

        return builder.build();
    }

    public void launchChrome()
    {
        AOLoggers.CHROME.debug("[chrome] ");
        AOLoggers.CHROME.debug("[chrome] Starting Chrome|Chromium");
        AOLoggers.CHROME.debug("[chrome] ");

        try
        {
            sessionFactory = launcher.launch();

            if (sessionFactory != null)
            {
                launched = true;

                final ProcessManager manager = options.processManager();
                final String pid = pid();

                AOLoggers.CHROME.debug("[chrome] process manager : {}", manager);
                AOLoggers.CHROME.debug("[chrome] process ID      : {}", pid);

                AOLoggers.CHROME.debug("[chrome] ");
                AOLoggers.CHROME.debug("[chrome] Chrome|Chromium has been started");
                AOLoggers.CHROME.debug("[chrome] ");

                final GetVersionResult v = (version = extractChromeVersion());

                if (v != null)
                {
                    AOLoggers.CHROME.debug("[chrome] ");
                    AOLoggers.CHROME.debug("[chrome] Chrome|Chromium information");
                    AOLoggers.CHROME.debug("[chrome]               product : {}", v.getProduct());
                    AOLoggers.CHROME.debug("[chrome]              revision : {}", v.getRevision());
                    AOLoggers.CHROME.debug("[chrome]            user agent : {}", v.getUserAgent());
                    AOLoggers.CHROME.debug("[chrome]      protocol version : {}", v.getProtocolVersion());
                    AOLoggers.CHROME.debug("[chrome]            JS version : {}", v.getJsVersion());
                    AOLoggers.CHROME.debug("[chrome] ");
                }
            }
        }
        catch (CdpException ex)
        {
            AOLoggers.CHROME.error("[chrome] could not start Chrome", ex);
        }
    }

    @Nullable
    public GetVersionResult extractChromeVersion()
    {
        try
        {
            return doExtractChromeVersion();
        }
        catch (Exception exception)
        {
            AOLoggers.CHROME.debug("[chrome] could not get Chrome|Chromium version", exception);
            return null;
        }
    }

    @Nullable
    public GetVersionResult doExtractChromeVersion() throws Exception
    {
        final String browserContext = createBrowserContext();

        try
        {
            if (browserContext != null)
            {
                try (Session session = createBrowserSession(browserContext))
                {
                    if (session != null)
                    {
                        final Command command = session.getCommand();
                        final Browser browser = command.getBrowser();

                        return browser.getVersion();
                    }
                }
            }
        }
        finally
        {
            disposeBrowserContext(browserContext);
        }

        return null;
    }

    public void shutdown()
    {
        if (!launched)
        {
            return;
        }

        AOLoggers.CHROME.debug("[chrome] shutdown: closing the session-factory");

        try
        {
            final SessionFactory sf = sessionFactory;

            if (sf != null)
            {
                sf.close();
            }

            sessionFactory = null;
        }
        catch (RuntimeException ex)
        {
            AOLoggers.CHROME.warn("[chrome] shutdown error: closing the session-factory", ex);
        }

        AOLoggers.CHROME.debug("[chrome] shutdown: killing the Chrome process [{}]", pid());

        try
        {
            final AOChromeLauncher l = launcher;

            if (l != null)
            {
                l.kill();
            }

            launcher = null;
        }
        catch (RuntimeException ex)
        {
            AOLoggers.CHROME.warn("[chrome] shutdown error: killing the Chrome process", ex);
        }

        AOLoggers.CHROME.debug("[chrome] shutdown: delete the user-data-dir");

        try
        {
            final Options opts = options;

            if (opts != null)
            {
                final Path userDataDir = opts.userDataDir();

                AOLoggers.CHROME.debug("[chrome] shutdown: delete the user-data-dir: {}", userDataDir);

                FileUtils.deleteQuietly(userDataDir.toFile());
            }
        }
        catch (RuntimeException ex)
        {
            AOLoggers.CHROME.warn("[chrome] shutdown error: deleting the user-data-dir", ex);
        }

        AOLoggers.CHROME.debug("[chrome] shutdown: done");
    }

    @Nullable
    public String pid()
    {
        final Options opts = options;

        if (opts == null)
        {
            AOLoggers.CHROME.debug("[chrome] could not retrieve the PID (no options)");
            return null;
        }

        ProcessManager manager = opts.processManager();

        if (manager == null)
        {
            AOLoggers.CHROME.debug("[chrome] could not retrieve the PID (no process manager)");
            return null;
        }

        if (manager instanceof AdaptiveProcessManager)
        {
            try
            {
                final Field processManagerF = manager.getClass().getDeclaredField("processManager");
                processManagerF.setAccessible(true);

                manager = (ProcessManager) processManagerF.get(manager);

            }
            catch (Exception ex)
            {
                AOLoggers.CHROME.debug("[chrome] could not retrieve the PID", ex);
                return null;
            }
        }

        try
        {
            final Field pidF = manager.getClass().getDeclaredField("pid");
            pidF.setAccessible(true);

            final Object pid = pidF.get(manager);
            return pid.toString();

        }
        catch (Exception ex)
        {
            AOLoggers.CHROME.debug("[chrome] could not retrieve the PID", ex);
            return null;
        }
    }

}
