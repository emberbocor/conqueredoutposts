package io.github.emberbocor.conqueredoutposts;

import io.github.emberbocor.conqueredoutposts.config.ServerConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ConqueredOutpostsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> ServerConfig.load(server.registryAccess()));
    }
}
