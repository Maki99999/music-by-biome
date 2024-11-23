package io.github.maki99999.biomebeats.service;

import com.google.auto.service.AutoService;
import io.github.maki99999.biomebeats.Constants;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

@AutoService(IPlatformHelper.class)
public class PlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getModConfigFolder() {
        return FabricLoader.getInstance().getConfigDir().resolve(Constants.MOD_ID);
    }
}
