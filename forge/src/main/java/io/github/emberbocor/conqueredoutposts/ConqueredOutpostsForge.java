package io.github.emberbocor.conqueredoutposts;

import io.github.emberbocor.conqueredoutposts.config.ServerConfig;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(ConqueredOutposts.MODID)
public class ConqueredOutpostsForge {
    public ConqueredOutpostsForge() {
        ServerAboutToStartEvent.BUS.addListener(event -> ServerConfig.load(event.getServer().registryAccess()));
    }
}
