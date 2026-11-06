package io.github.emberbocor.conqueredoutposts.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.emberbocor.conqueredoutposts.claim.OutpostClaims;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

@Mixin(ChunkGenerator.class)
abstract class ChunkGeneratorMixin {
    @Redirect(method = "getMobsAt", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/StructureManager;fillStartsForStructure(Lnet/minecraft/world/level/levelgen/structure/Structure;Lit/unimi/dsi/fastutil/longs/LongSet;Ljava/util/function/Consumer;)V"))
    private void conqueredoutposts$skipClaimedStarts(StructureManager structures, Structure structure, LongSet references, Consumer<StructureStart> consumer) {
        structures.fillStartsForStructure(structure, references, OutpostClaims.unclaimedStarts(structures, structure, consumer));
    }
}
