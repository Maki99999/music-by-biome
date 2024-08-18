package biomebeats.service;

import com.google.auto.service.AutoService;
import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.service.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@AutoService(IPlatformHelper.class)
public class PlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Path getModConfigFolder() {
        return FMLPaths.CONFIGDIR.get().resolve(Constants.MOD_ID);
    }
}