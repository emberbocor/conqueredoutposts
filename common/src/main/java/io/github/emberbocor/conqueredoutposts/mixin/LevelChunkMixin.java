package io.github.emberbocor.conqueredoutposts.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.conqueredoutposts.claim.OutpostClaims;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

@Mixin(LevelChunk.class)
abstract class LevelChunkMixin {
    // Every block change on the server, whatever its update flags; the return value is the replaced state.
    @Inject(method = "setBlockState", at = @At("RETURN"))
    private void conqueredoutposts$bannerRemoved(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<BlockState> cir) {
        BlockState replaced = cir.getReturnValue();
        if (replaced != null && replaced.getBlock() instanceof AbstractBannerBlock && !(state.getBlock() instanceof AbstractBannerBlock)
                && ((LevelChunk) (Object) this).getLevel() instanceof ServerLevel level) {
            OutpostClaims.bannerRemoved(level, pos);
        }
    }
}
