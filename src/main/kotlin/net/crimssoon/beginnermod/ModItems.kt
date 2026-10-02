package net.crimssoon.beginnermod

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object ModItems {

    val ITEMS: DeferredRegister.Items =
        DeferredRegister.createItems(BeginnerMod.MOD_ID)

    val TEST_BLOCK: DeferredItem<BlockItem> =
        ITEMS.register(
            "test_block",
            Supplier {
                BlockItem(
                    ModBlocks.TEST_BLOCK.get(),
                    Item.Properties()
                )
            }
        )

    fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
    }
}