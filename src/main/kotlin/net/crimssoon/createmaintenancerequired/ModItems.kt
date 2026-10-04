package net.crimssoon.createmaintenancerequired

import net.crimssoon.createmaintenancerequired.item.WireBrushItem
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

    @JvmField
    val WIRE_BRUSH: DeferredItem<WireBrushItem> =
        ITEMS.register(
            "wire_brush",
            Supplier {
                WireBrushItem(
                    Item.Properties()
                        .durability(64)
                )
            }
        )

    fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
    }
}
