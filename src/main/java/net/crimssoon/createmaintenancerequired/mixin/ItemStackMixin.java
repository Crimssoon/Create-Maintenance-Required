package net.crimssoon.createmaintenancerequired.mixin;

import net.crimssoon.createmaintenancerequired.rust.RustBar;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "isBarVisible", at = @At("HEAD"), cancellable = true, require = 1)
    private void createmaintenancerequired$barVisible(CallbackInfoReturnable<Boolean> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (RustBar.handles(self)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true, require = 1)
    private void createmaintenancerequired$barWidth(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (RustBar.handles(self)) {
            cir.setReturnValue(RustBar.width(self));
        }
    }

    @Inject(method = "getBarColor", at = @At("HEAD"), cancellable = true, require = 1)
    private void createmaintenancerequired$barColor(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (RustBar.handles(self)) {
            cir.setReturnValue(RustBar.color(self));
        }
    }
}