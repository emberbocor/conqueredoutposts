package io.github.emberbocor.conqueredoutposts.config;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.slf4j.helpers.MessageFormatter;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import io.github.emberbocor.conqueredoutposts.ConqueredOutposts;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

final class ServerConfigReader {
    private static final Set<String> KEYS = Set.of("structures", "showMessages");

    private final Path file;
    private final Registry<Structure> structureRegistry;

    ServerConfigReader(Path file, Registry<Structure> structureRegistry) {
        this.file = file;
        this.structureRegistry = structureRegistry;
    }

    ServerConfig.Settings read(@Nullable UnmodifiableConfig config) {
        ServerConfig.Settings defaults = ServerConfig.DEFAULTS;
        if (config == null) {
            return defaults;
        }
        for (UnmodifiableConfig.Entry entry : config.entrySet()) {
            if (!KEYS.contains(entry.getKey())) {
                warn("unknown option '{}', ignored", entry.getKey());
            }
        }
        return new ServerConfig.Settings(structures(config, defaults.structures()),
                showMessages(config, defaults.showMessages()));
    }

    private Set<ResourceLocation> structures(UnmodifiableConfig config, Set<ResourceLocation> fallback) {
        Object value = config.get("structures");
        if (value == null) {
            return fallback;
        }
        if (!(value instanceof List<?> list)) {
            warn("structures must be a list of structure ids, got '{}'; using {}", value, fallback);
            return fallback;
        }
        Set<ResourceLocation> structures = new LinkedHashSet<>();
        for (Object entry : list) {
            ResourceLocation id = entry instanceof String text ? ResourceLocation.tryParse(text) : null;
            if (id == null) {
                warn("'{}' in structures is not a structure id, ignored", entry);
            } else if (!structureRegistry.containsKey(id)) {
                warn("unknown structure '{}' in structures, ignored", id);
            } else {
                structures.add(id);
            }
        }
        return Set.copyOf(structures);
    }

    private boolean showMessages(UnmodifiableConfig config, boolean fallback) {
        Object value = config.get("showMessages");
        if (value == null) {
            return fallback;
        }
        if (value instanceof Boolean show) {
            return show;
        }
        warn("showMessages must be true or false, got '{}'; using {}", value, fallback);
        return fallback;
    }

    private void warn(String message, Object... args) {
        ConqueredOutposts.LOGGER.warn("{}: {}", file, MessageFormatter.arrayFormat(message, args).getMessage());
    }
}
