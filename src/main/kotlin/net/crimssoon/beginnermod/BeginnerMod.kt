package net.crimssoon.beginnermod

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod

@Mod(BeginnerMod.MOD_ID)
class BeginnerMod(modEventBus: IEventBus) {

    companion object {
        const val MOD_ID = "beginnermod"
    }

    init {
        ModBlocks.register(modEventBus)
        ModItems.register(modEventBus)
    }
}