package io.github.emberbocor.conqueredoutposts.config;

import java.nio.file.Path;
import java.util.Set;

import io.github.emberbocor.conqueredoutposts.platform.Services;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public final class ServerConfig {
    static final Settings DEFAULTS = new Settings(Set.of(Identifier.withDefaultNamespace("pillager_outpost")), true);

    private static volatile Settings settings = DEFAULTS;

    private ServerConfig() {
    }

    public static void load(RegistryAccess registries) {
        Path file = Services.PLATFORM.getConfigDirectory().resolve(ServerConfigFile.FILE_NAME);
        settings = new ServerConfigReader(file, registries.lookupOrThrow(Registries.STRUCTURE))
                .read(ServerConfigFile.load(file).orElse(null));
    }

    public static Settings settings() {
        return settings;
    }

    public record Settings(Set<Identifier> structures, boolean showMessages) {
    }
}
