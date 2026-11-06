package io.github.emberbocor.conqueredoutposts.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.conqueredoutposts.claim.OutpostClaims;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockBehaviour.class)
abstract class BlockBehaviourMixin {
    @Inject(method = "onRemove", at = @At("HEAD"))
    private void conqueredoutposts$bannerRemoved(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
        if ((Object) this instanceof AbstractBannerBlock && !(newState.getBlock() instanceof AbstractBannerBlock)
                && level instanceof ServerLevel serverLevel) {
            OutpostClaims.bannerRemoved(serverLevel, pos);
        }
    }
}
