package net.crimssoon.createmaintenancerequired

import com.simibubi.create.AllBlocks
import net.crimssoon.createmaintenancerequired.rust.helpers.ModComponents
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object ModCreativeTabs {

    private val TABS: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateMaintenanceRequired.MOD_ID)

    val MAIN: DeferredHolder<CreativeModeTab, CreativeModeTab> =
        TABS.register("main") { ->
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + CreateMaintenanceRequired.MOD_ID))
                .icon { ItemStack(ModItems.WIRE_BRUSH.get()) }
                .displayItems { _, output ->
                    output.accept(ModItems.WIRE_BRUSH.get())

                    val machines = listOf(
                        AllBlocks.MECHANICAL_HARVESTER.asItem(),
                        AllBlocks.MECHANICAL_SAW.asItem(),
                        AllBlocks.MECHANICAL_DRILL.asItem()
                    )

                    for (item in machines) {
                        val worn = ItemStack(item)
                        worn.set(ModComponents.DURABILITY.get(), 50)
                        output.accept(worn)
                    }

                    for (item in machines) {
                        val broken = ItemStack(item)
                        broken.set(ModComponents.DURABILITY.get(), 0)
                        output.accept(broken)
                    }
                }
                .build()
        }

    fun register(bus: IEventBus) {
        TABS.register(bus)
    }
}