package net.crimssoon.createmaintenancerequired.mixin;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity;
import net.crimssoon.createmaintenancerequired.rust.RustWork;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HarvesterBlockEntity.class, priority = 2000)
public abstract class HarvesterSubLevelTickMixin {

    @Inject(method = "sable$tick", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void createmaintenancerequired$brokenSubLevelTick(CallbackInfo ci) {
        if (RustWork.isBroken((BlockEntity) (Object) this)) {
            ci.cancel();
        }
    }
}