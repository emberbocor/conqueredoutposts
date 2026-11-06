package io.github.emberbocor.conqueredoutposts.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.conqueredoutposts.claim.OutpostClaims;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Block.class)
abstract class BlockMixin {
    @Inject(method = "setPlacedBy", at = @At("HEAD"))
    private void conqueredoutposts$bannerPlaced(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        if ((Object) this instanceof AbstractBannerBlock && placer instanceof Player && level instanceof ServerLevel serverLevel) {
            OutpostClaims.bannerPlaced(serverLevel, pos, stack);
        }
    }
}
