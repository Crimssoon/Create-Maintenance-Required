package net.crimssoon.createmaintenancerequired.client.overalls

import net.crimssoon.createmaintenancerequired.CreateMaintenanceRequired
import net.minecraft.client.renderer.entity.player.PlayerRenderer
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.EntityRenderersEvent

@EventBusSubscriber(modid = CreateMaintenanceRequired.MOD_ID, value = [Dist.CLIENT])
object ClientEvents {

    @SubscribeEvent
    @JvmStatic
    fun addLayers(event: EntityRenderersEvent.AddLayers) {
        for (skin in event.skins) {
            val renderer: PlayerRenderer = event.getSkin(skin) ?: continue
            renderer.addLayer(OverallsLayer(renderer, event.entityModels))
        }
    }
}