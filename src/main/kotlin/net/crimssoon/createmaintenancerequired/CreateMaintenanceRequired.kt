package net.crimssoon.createmaintenancerequired

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod

@Mod(CreateMaintenanceRequired.MOD_ID)
class CreateMaintenanceRequired(modEventBus: IEventBus) {

    companion object {
        const val MOD_ID = "createmaintenancerequired"
    }

    init {
        ModBlocks.register(modEventBus)
        ModItems.register(modEventBus)
    }
}