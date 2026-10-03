package net.crimssoon.createmaintenancerequired

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object ModItems {

    val ITEMS: DeferredRegister.Items =
        DeferredRegister.createItems(CreateMaintenanceRequired.MOD_ID)

    val BROKEN_HARVESTER: DeferredItem<BlockItem> =
        ITEMS.register(
            "broken_harvester",
            Supplier {
                BlockItem(
                    ModBlocks.BROKEN_HARVESTER.get(),
                    Item.Properties()
                )
            }
        )

    fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
    }
}