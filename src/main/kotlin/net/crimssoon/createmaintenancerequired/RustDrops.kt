package net.crimssoon.createmaintenancerequired

import net.minecraft.server.level.ServerLevel
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.level.BlockDropsEvent

@EventBusSubscriber(modid = CreateMaintenanceRequired.MOD_ID)
object RustDrops {

    @JvmStatic
    @SubscribeEvent
    fun onBlockDrops(event: BlockDropsEvent) {
        if (event.level !is ServerLevel) return
        if (!RustTargets.isRustable(event.state.block)) return

        val be = event.blockEntity ?: return
        val durability = RustDurability.get(be)
        if (durability >= RustDurability.MAX) return

        val item = event.state.block.asItem()
        for (drop in event.drops) {
            if (drop.item.item == item) {
                drop.item.set(ModComponents.DURABILITY.get(), durability)
            }
        }
    }
}