package net.crimssoon.createmaintenancerequired.mixin;

import net.crimssoon.createmaintenancerequired.RustTargets;
import net.crimssoon.createmaintenancerequired.RustTracker;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {

    @Inject(method = "setBlockEntity", at = @At("HEAD"), require = 1)
    private void createmaintenancerequired$track(BlockEntity blockEntity, CallbackInfo ci) {
        String name = blockEntity.getBlockState().getBlock().toString();
        if (RustTargets.isRustable(blockEntity.getBlockState().getBlock())) {
            RustTracker.add(blockEntity);
        }
    }
}