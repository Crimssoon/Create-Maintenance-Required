package net.crimssoon.createmaintenancerequired.mixin;

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import net.crimssoon.createmaintenancerequired.rust.RustWork;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBreakingKineticBlockEntity.class)
public abstract class BlockBreakingKineticBlockEntityMixin {

    @Inject(method = "shouldRun", at = @At("HEAD"), cancellable = true, require = 1)
    private void createmaintenancerequired$brokenMachine(CallbackInfoReturnable<Boolean> cir) {
        if (RustWork.isBroken((net.minecraft.world.level.block.entity.BlockEntity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}