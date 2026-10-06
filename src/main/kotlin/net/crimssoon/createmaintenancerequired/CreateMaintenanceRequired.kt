package net.crimssoon.createmaintenancerequired

import net.crimssoon.createmaintenancerequired.client.overalls.ModArmorMaterials
import net.crimssoon.createmaintenancerequired.rust.ModLootModifiers
import net.crimssoon.createmaintenancerequired.rust.ModRustTargets
import net.crimssoon.createmaintenancerequired.rust.helpers.ModAttachments
import net.crimssoon.createmaintenancerequired.rust.helpers.ModComponents
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod

@Mod(CreateMaintenanceRequired.MOD_ID)
class CreateMaintenanceRequired(modEventBus: IEventBus) {

    companion object {
        const val MOD_ID = "createmaintenancerequired"
    }

    init {
        ModArmorMaterials.register(modEventBus)
        ModItems.register(modEventBus)
        ModAttachments.register(modEventBus)
        ModComponents.register(modEventBus)
        ModRustTargets.register()
        ModLootModifiers.register(modEventBus)
        ModCreativeTabs.register(modEventBus)
    }
}