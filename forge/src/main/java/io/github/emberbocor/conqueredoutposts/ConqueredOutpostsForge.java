package io.github.emberbocor.conqueredoutposts;

import io.github.emberbocor.conqueredoutposts.config.ServerConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(ConqueredOutposts.MODID)
public class ConqueredOutpostsForge {
    public ConqueredOutpostsForge() {
        MinecraftForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) -> ServerConfig.load(event.getServer().registryAccess()));
    }
}
