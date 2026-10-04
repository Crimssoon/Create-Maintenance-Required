package net.crimssoon.createmaintenancerequired.client

import com.mojang.blaze3d.vertex.PoseStack
import net.crimssoon.createmaintenancerequired.ModComponents
import net.crimssoon.createmaintenancerequired.RustDurability
import net.crimssoon.createmaintenancerequired.RustTargets
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.item.ItemStack

object ItemRustRenderer {

    @JvmStatic
    fun render(
        stack: ItemStack,
        poseStack: PoseStack,
        buffers: MultiBufferSource,
        light: Int,
        overlay: Int,
        model: BakedModel
    ) {
        if (RustTargets.findByItem(stack.item) == null) return
        if (model.isCustomRenderer) return

        val durability = stack.get(ModComponents.DURABILITY.get()) ?: return
        val alpha = (RustDurability.MAX - durability) / RustDurability.MAX.toFloat()
        if (alpha < 0.02f) return

        DamageOverlayRenderer.drawGroupedTo(
            DamageOverlayRenderer.quadsForModel(model),
            poseStack, buffers, alpha, light, overlay
        )
    }
}