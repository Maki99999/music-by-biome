package io.github.maki99999.biomebeats;

import io.github.maki99999.biomebeats.util.MenuChangeListener;
import io.github.maki99999.biomebeats.gui.ConfigScreen;
import io.github.maki99999.biomebeats.service.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.world.entity.player.Player;
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
import java.util.HashSet;
import java.util.Set;

public class BiomeBeatsCommon {
    private static final Logger DEBUG_LOGGER = LoggerFactory.getLogger(Constants.LOG.getName() + "Debug");
    private static boolean initAfterSetupDone = false;
    private static boolean firstTickWithLevel = true;
    private static final Set<MenuChangeListener> menuChangeListeners = new HashSet<>();

    public static void init() {
        // sadly the sound library uses System.out, so I'm disabling it here.
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {}
        }));

        if (Services.PLATFORM.getPlatformName().equals("Fabric") && Services.PLATFORM.isDevelopmentEnvironment()) {
            // setLevel does not work for some reason so here is a roundabout way
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

        BiomeBeatsCommon.addMenuChangeListener(BiomeBeatsCommon::onMenuChanged);
    }

    public static void tick() {
        if (firstTickWithLevel && Minecraft.getInstance().level != null) {
            firstTickWithLevel = false;
            initWithLevel();
        }

        Constants.CONDITION_MANAGER.tick();

        while (Constants.CONFIG_KEY_MAPPING.consumeClick()) {
            Minecraft.getInstance().setScreen(new ConfigScreen());
        }
    }

    public static void initAfterSetup() {
        Constants.MUSIC_MANAGER.init();
        Constants.CONDITION_MANAGER.init();
        Constants.CONDITION_MUSIC_MANAGER.init();
        Constants.CONFIG_IO.loadConfig();
    }

    public static void initWithLevel() {
        Constants.CONDITION_MANAGER.initWithLevel();
        Constants.CONFIG_IO.updateConfigListeners();
    }

    public static void close() {
        try {
            Constants.MUSIC_MANAGER.close();
        } catch (Exception e) {
            Constants.LOG.error("Failed to close the music player", e);
        }
    }

    public static void addMenuChangeListener(MenuChangeListener listener) {
        menuChangeListeners.add(listener);
    }

    public static void notifyMenuChangeListeners(Screen screen, Player player) {
        for (MenuChangeListener listener : menuChangeListeners) {
            listener.onMenuChanged(screen, player);
        }
    }

    public static void onMenuChanged(Screen screen, Player player) {
        if (!initAfterSetupDone && screen.getClass() == TitleScreen.class) {
            initAfterSetupDone = true;
            BiomeBeatsCommon.initAfterSetup();
            notifyMenuChangeListeners(screen, player);
        }
    }
}