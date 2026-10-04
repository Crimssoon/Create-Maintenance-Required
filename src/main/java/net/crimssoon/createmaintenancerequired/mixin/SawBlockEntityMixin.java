package net.crimssoon.createmaintenancerequired.mixin;

import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import net.crimssoon.createmaintenancerequired.RustWork;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SawBlockEntity.class)
public abstract class SawBlockEntityMixin {

    @Inject(method = "shouldRun", at = @At("HEAD"), cancellable = true, require = 1)
    private void createmaintenancerequired$brokenRun(CallbackInfoReturnable<Boolean> cir) {
        if (RustWork.isBroken((net.minecraft.world.level.block.entity.BlockEntity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canProcess", at = @At("HEAD"), cancellable = true, require = 1)
    private void createmaintenancerequired$brokenProcess(CallbackInfoReturnable<Boolean> cir) {
        if (RustWork.isBroken((net.minecraft.world.level.block.entity.BlockEntity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}