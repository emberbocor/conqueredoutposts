package io.github.emberbocor.conqueredoutposts;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(ConqueredOutposts.MODID)
public class ConqueredOutposts {
    public static final String MODID = "conqueredoutposts";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ConqueredOutposts(IEventBus modEventBus, ModContainer modContainer) {
    }
}
