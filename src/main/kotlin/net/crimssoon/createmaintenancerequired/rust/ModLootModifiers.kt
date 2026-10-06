package net.crimssoon.createmaintenancerequired.rust

import com.mojang.serialization.MapCodec
import net.crimssoon.createmaintenancerequired.CreateMaintenanceRequired
import net.crimssoon.createmaintenancerequired.StampDurabilityModifier
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.loot.IGlobalLootModifier
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries

object ModLootModifiers {

    private val SERIALIZERS: DeferredRegister<MapCodec<out IGlobalLootModifier>> =
        DeferredRegister.create(
            NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS,
            CreateMaintenanceRequired.MOD_ID
        )

    init {
        SERIALIZERS.register("stamp_durability") { -> StampDurabilityModifier.CODEC }
    }

    fun register(bus: IEventBus) {
        SERIALIZERS.register(bus)
    }
}