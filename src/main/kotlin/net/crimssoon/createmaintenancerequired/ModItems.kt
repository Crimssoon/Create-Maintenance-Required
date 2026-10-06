package net.crimssoon.createmaintenancerequired

import net.crimssoon.createmaintenancerequired.client.overalls.ModArmorMaterials
import net.crimssoon.createmaintenancerequired.item.WireBrushItem
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object ModItems {

    val ITEMS: DeferredRegister.Items =
        DeferredRegister.createItems(CreateMaintenanceRequired.MOD_ID)

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

    @JvmField
    val OVERALLS: DeferredItem<OverallsItem> =
        ITEMS.register(
            "overalls",
            Supplier {
                OverallsItem(
                    ModArmorMaterials.OVERALLS,
                    Item.Properties()
                        .durability(ArmorItem.Type.LEGGINGS.getDurability(15))
                )
            }
        )

    fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
    }
}