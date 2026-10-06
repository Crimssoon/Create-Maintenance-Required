package net.crimssoon.createmaintenancerequired.client.overalls

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.model.geom.EntityModelSet
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EquipmentSlot
import net.crimssoon.createmaintenancerequired.CreateMaintenanceRequired
import net.crimssoon.createmaintenancerequired.OverallsItem

class OverallsLayer(
    parent: RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>,
    modelSet: EntityModelSet
) : RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(parent) {

    private val model = HumanoidModel<AbstractClientPlayer>(
        modelSet.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)
    )

    private val texture = ResourceLocation.fromNamespaceAndPath(
        CreateMaintenanceRequired.MOD_ID,
        "textures/models/armor/overalls_bib.png"
    )

    override fun render(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        player: AbstractClientPlayer,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTick: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val stack = player.getItemBySlot(EquipmentSlot.LEGS)
        if (stack.item !is OverallsItem) return

        parentModel.copyPropertiesTo(model)
        model.setAllVisible(false)
        model.body.visible = true

        val vc = buffer.getBuffer(RenderType.armorCutoutNoCull(texture))
        model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, -1)
    }
}