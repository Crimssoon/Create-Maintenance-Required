package net.crimssoon.createmaintenancerequired.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.crimssoon.createmaintenancerequired.client.ItemRustRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",
                    shift = At.Shift.BEFORE
            ),
            require = 1
    )
    private void createmaintenancerequired$rust(
            ItemStack stack,
            ItemDisplayContext context,
            boolean leftHand,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int light,
            int overlay,
            BakedModel model,
            CallbackInfo ci
    ) {
        ItemRustRenderer.render(stack, poseStack, buffers, light, overlay, model);
    }
}