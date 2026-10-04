package net.crimssoon.createmaintenancerequired

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod

@Mod(CreateMaintenanceRequired.MOD_ID)
class CreateMaintenanceRequired(modEventBus: IEventBus) {

    companion object {
        const val MOD_ID = "createmaintenancerequired"
    }

    init {
        ModItems.register(modEventBus)
        ModAttachments.register(modEventBus)
        ModComponents.register(modEventBus)
        ModRustTargets.register()
        ModLootModifiers.register(modEventBus)
        ModCreativeTabs.register(modEventBus)
    }
}