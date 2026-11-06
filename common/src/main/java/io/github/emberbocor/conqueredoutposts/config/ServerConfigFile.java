package io.github.emberbocor.conqueredoutposts.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

import io.github.emberbocor.conqueredoutposts.ConqueredOutposts;

final class ServerConfigFile {
    static final String FILE_NAME = ConqueredOutposts.MODID + ".toml";

    private static final String DEFAULT_TEXT = """
            # Conquered Outposts config.
            # Created on the first server or world start.
            # Restart the server or reopen the singleplayer world to apply changes.

            # Structures that players can claim by placing a banner inside them (any banner except an Ominous Banner).
            # While at least one claim banner stays in place, the structure no longer spawns its own mobs
            # (pillagers for outposts); the normal mobs of the biome still spawn there.
            # Structure ids, for example "minecraft:pillager_outpost". Unknown ids are ignored with a warning.
            structures = ["minecraft:pillager_outpost"]

            # Show "Outpost claimed" and "Outpost lost" above the hotbar of the players inside the outpost.
            showMessages = true
            """;

    private ServerConfigFile() {
    }

    static Optional<UnmodifiableConfig> load(Path file) {
        if (!Files.exists(file)) {
            try {
                Files.createDirectories(file.getParent());
                Files.writeString(file, DEFAULT_TEXT, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
                ConqueredOutposts.LOGGER.info("Created {}", file);
            } catch (IOException e) {
                ConqueredOutposts.LOGGER.warn("Could not create {}, using the default settings: {}", file, e.getMessage());
            }
            return parse(file, DEFAULT_TEXT);
        }

        try {
            String text = Files.readString(file, StandardCharsets.UTF_8);
            return parse(file, text.startsWith("﻿") ? text.substring(1) : text);
        } catch (IOException e) {
            ConqueredOutposts.LOGGER.warn("Could not read {}, using the default settings; file left unchanged: {}", file, e.getMessage());
            return Optional.empty();
        }
    }

    private static Optional<UnmodifiableConfig> parse(Path file, String text) {
        try {
            return Optional.of(TomlFormat.instance().createParser().parse(text));
        } catch (RuntimeException e) {
            ConqueredOutposts.LOGGER.warn("Could not parse {}, using the default settings; file left unchanged: {}", file, e.getMessage());
            return Optional.empty();
        }
    }
}
