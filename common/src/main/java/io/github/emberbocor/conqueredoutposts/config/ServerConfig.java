package io.github.emberbocor.conqueredoutposts.config;

import java.nio.file.Path;
import java.util.Set;

import io.github.emberbocor.conqueredoutposts.platform.Services;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

public final class ServerConfig {
    static final Settings DEFAULTS = new Settings(Set.of(ResourceLocation.tryBuild("minecraft", "pillager_outpost")), true);

    private static volatile Settings settings = DEFAULTS;

    private ServerConfig() {
    }

    public static void load(RegistryAccess registries) {
        Path file = Services.PLATFORM.getConfigDirectory().resolve(ServerConfigFile.FILE_NAME);
        settings = new ServerConfigReader(file, registries.registryOrThrow(Registries.STRUCTURE))
                .read(ServerConfigFile.load(file).orElse(null));
    }

    public static Settings settings() {
        return settings;
    }

    public record Settings(Set<ResourceLocation> structures, boolean showMessages) {
    }
}
