package io.github.emberbocor.conqueredoutposts;

import io.github.emberbocor.conqueredoutposts.config.ServerConfig;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@Mod(ConqueredOutposts.MODID)
public class ConqueredOutpostsNeoForge {
    public ConqueredOutpostsNeoForge() {
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) -> ServerConfig.load(event.getServer().registryAccess()));
    }
}
