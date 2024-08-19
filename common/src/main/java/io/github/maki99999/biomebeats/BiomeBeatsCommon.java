package io.github.maki99999.biomebeats;

import io.github.maki99999.biomebeats.gui.ConfigScreen;
import io.github.maki99999.biomebeats.service.Services;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintStream;

public class BiomeBeatsCommon {
    private static final Logger DEBUG_LOGGER = LoggerFactory.getLogger(Constants.LOG.getName() + "Debug");
    private static boolean firstTick = true;
    private static boolean firstTickWithLevel = true;

    public static void init() {
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {}
        }));

        if (Services.PLATFORM.getPlatformName().equals("Fabric") && Services.PLATFORM.isDevelopmentEnvironment()) {
            //setLevel does not work for some reason so here is a roundabout way
            final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            final Configuration config = ctx.getConfiguration();

            AbstractAppender appender = new AbstractAppender("DebugToInfoAppender", null,
                    PatternLayout.createDefaultLayout(config), true, null) {
                @Override
                public void append(LogEvent event) {
                    if (event.getLevel() == Level.DEBUG) {
                        DEBUG_LOGGER.info(event.getMessage().getFormattedMessage());
                    }
                }
            };

            appender.start();

            LoggerConfig loggerConfig = new LoggerConfig(Constants.LOG.getName(), Level.DEBUG, true);
            loggerConfig.addAppender(appender, Level.DEBUG, null);
            config.addLogger(Constants.LOG.getName(), loggerConfig);
            ctx.updateLoggers();

            Constants.LOG.debug("Debug logging mode.");
        }
    }

    public static void tick() {
        if (firstTick) {
            firstTick = false;
            BiomeBeatsCommon.initAfterSetup();
        }
        if (firstTickWithLevel && Minecraft.getInstance().level != null) {
            firstTickWithLevel = false;
            initWithLevel();
        }

        Constants.CONDITION_MANAGER.tick();

        while (Constants.CONFIG_KEY_MAPPING.consumeClick()) {
            Minecraft.getInstance().setScreen(new ConfigScreen());
        }
    }

    public static void initAfterSetup() {}

    public static void initWithLevel() {
        //TODO: split inits into two, so that the conditions that dont need a level are there after setup (menu music!)
        Constants.MUSIC_MANAGER.init();
        Constants.CONDITION_MANAGER.init();
        Constants.CONDITION_MUSIC_MANAGER.init();
        Constants.CONFIG_IO.loadConfig();
    }

    public static void close() {
        try {
            Constants.MUSIC_MANAGER.close();
        } catch (Exception e) {
            Constants.LOG.error("Failed to close the music player", e);
        }
    }
}