package net.crimssoon.createmaintenancerequired

import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object ModComponents {

    private val COMPONENTS: DeferredRegister.DataComponents =
        DeferredRegister.createDataComponents(
            Registries.DATA_COMPONENT_TYPE,
            CreateMaintenanceRequired.MOD_ID
        )

    val DURABILITY: DeferredHolder<DataComponentType<*>, DataComponentType<Int>> =
        COMPONENTS.registerComponentType("durability") { builder ->
            builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
        }

    fun register(bus: IEventBus) {
        COMPONENTS.register(bus)
    }
}