package io.github.emberbocor.conqueredoutposts.claim;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import io.github.emberbocor.conqueredoutposts.config.ServerConfig;
import io.github.emberbocor.conqueredoutposts.mixin.StructureManagerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public final class OutpostClaims {
    private static final Component CLAIMED = Component.literal("Outpost claimed");
    private static final Component LOST = Component.literal("Outpost lost");

    private OutpostClaims() {
    }

    public static void bannerPlaced(ServerLevel level, BlockPos pos, ItemStack banner) {
        Set<ResourceLocation> claimable = ServerConfig.settings().structures();
        if (claimable.isEmpty() || isOminousBanner(level, banner)) {
            return;
        }
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        StructureManager structures = level.structureManager();
        ClaimData data = ClaimData.get(level);
        structures.getAllStructuresAt(pos).forEach((structure, references) -> {
            ResourceLocation id = registry.getKey(structure);
            if (id != null && claimable.contains(id)) {
                structures.fillStartsForStructure(structure, references, start -> {
                    BoundingBox box = start.getBoundingBox();
                    if (box.isInside(pos) && data.add(key(id, start), box, pos.asLong())) {
                        announce(level, box, CLAIMED);
                    }
                });
            }
        });
    }

    public static void bannerRemoved(ServerLevel level, BlockPos pos) {
        ClaimData data = ClaimData.get(level);
        if (!data.isEmpty()) {
            data.removeBanner(pos.asLong()).forEach(claim -> announce(level, claim.box(), LOST));
        }
    }

    public static Consumer<StructureStart> unclaimedStarts(StructureManager structures, Structure structure, Consumer<StructureStart> consumer) {
        if (!(((StructureManagerAccessor) structures).conqueredoutposts$getLevel() instanceof ServerLevel level)) {
            return consumer;
        }
        ClaimData data = ClaimData.get(level);
        if (data.isEmpty()) {
            return consumer;
        }
        ResourceLocation id = structures.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(structure);
        if (id == null || !ServerConfig.settings().structures().contains(id)) {
            return consumer;
        }
        return start -> {
            if (!isClaimed(level, data, key(id, start))) {
                consumer.accept(start);
            }
        };
    }

    private static boolean isClaimed(ServerLevel level, ClaimData data, ClaimData.Key key) {
        ClaimData.Claim claim = data.get(key);
        if (claim == null) {
            return false;
        }
        if (data.removeBanners(key, banner -> isGone(level, BlockPos.of(banner)))) {
            announce(level, claim.box(), LOST);
            return false;
        }
        return true;
    }

    private static boolean isGone(ServerLevel level, BlockPos pos) {
        return level.isLoaded(pos) && !(level.getBlockState(pos).getBlock() instanceof AbstractBannerBlock);
    }

    private static boolean isOminousBanner(ServerLevel level, ItemStack banner) {
        ItemStack ominous = Raid.getLeaderBannerInstance(level.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN));
        return banner.is(ominous.getItem())
                && Objects.equals(banner.get(DataComponents.BANNER_PATTERNS), ominous.get(DataComponents.BANNER_PATTERNS));
    }

    private static ClaimData.Key key(ResourceLocation structure, StructureStart start) {
        return new ClaimData.Key(structure, start.getChunkPos().toLong());
    }

    private static void announce(ServerLevel level, BoundingBox box, Component message) {
        if (!ServerConfig.settings().showMessages()) {
            return;
        }
        for (ServerPlayer player : level.players()) {
            if (box.isInside(player.blockPosition())) {
                player.displayClientMessage(message, true);
            }
        }
    }
}
