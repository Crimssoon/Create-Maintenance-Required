package net.crimssoon.createmaintenancerequired.mixin;

import net.crimssoon.createmaintenancerequired.rust.helpers.ModAttachments;
import net.crimssoon.createmaintenancerequired.rust.RustDurability;
import net.crimssoon.createmaintenancerequired.rust.RustTargets;
import net.crimssoon.createmaintenancerequired.rust.RustTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void createmaintenancerequired$init(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            CallbackInfo ci
    ) {
        if (!RustTargets.isRustable(state.getBlock())) {
            return;
        }

        BlockEntity self = (BlockEntity) (Object) this;

        self.setData(
                ModAttachments.INSTANCE.getDURABILITY(),
                RustDurability.MAX
        );
    }

    @Inject(
            method = "setLevel",
            at = @At("TAIL")
    )
    private void createmaintenancerequired$setLevel(
            Level level,
            CallbackInfo ci
    ) {
        BlockEntity self = (BlockEntity) (Object) this;

        if (!RustTargets.isRustable(
                self.getBlockState().getBlock()
        )) {
            return;
        }

        RustTracker.add(self);
    }

    @Inject(
            method = "setRemoved",
            at = @At("TAIL")
    )
    private void createmaintenancerequired$setRemoved(
            CallbackInfo ci
    ) {
        RustTracker.remove(
                (BlockEntity) (Object) this
        );
    }
}