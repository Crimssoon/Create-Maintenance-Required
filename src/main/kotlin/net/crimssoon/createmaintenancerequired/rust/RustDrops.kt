package net.crimssoon.createmaintenancerequired.rust

import net.crimssoon.createmaintenancerequired.CreateMaintenanceRequired
import net.crimssoon.createmaintenancerequired.rust.helpers.ModComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.level.BlockDropsEvent

@EventBusSubscriber(modid = CreateMaintenanceRequired.MOD_ID)
object RustDrops {

    @JvmStatic
    fun stamp(state: BlockState, be: BlockEntity?, drops: List<ItemStack>) {
        if (be == null) return
        if (!RustTargets.isRustable(state.block)) return

        val durability = RustDurability.get(be)
        if (durability >= RustDurability.MAX) return

        val item = state.block.asItem()
        for (stack in drops) {
            if (stack.item == item) {
                stack.set(ModComponents.DURABILITY.get(), durability)
            }
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onBlockDrops(event: BlockDropsEvent) {
        if (event.level !is ServerLevel) return
        stamp(event.state, event.blockEntity, event.drops.map { it.item })
    }
}