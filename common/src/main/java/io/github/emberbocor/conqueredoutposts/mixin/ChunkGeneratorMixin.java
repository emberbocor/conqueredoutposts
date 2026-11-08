package io.github.emberbocor.conqueredoutposts.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.emberbocor.conqueredoutposts.claim.OutpostClaims;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;

@Mixin(ChunkGenerator.class)
abstract class ChunkGeneratorMixin {
    @ModifyVariable(method = "getMobsAt", at = @At("STORE"), ordinal = 0)
    private Map<Structure, LongSet> conqueredoutposts$skipClaimedStarts(Map<Structure, LongSet> references, Level level,
            StructureManager structures, MobCategory category, BlockPos pos) {
        return OutpostClaims.withoutClaimedStarts(structures, references, category);
    }
}
