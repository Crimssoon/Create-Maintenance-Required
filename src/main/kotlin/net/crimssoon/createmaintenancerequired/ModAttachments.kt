package net.crimssoon.createmaintenancerequired

import com.mojang.serialization.Codec
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.function.Supplier

object ModAttachments {

    private val ATTACHMENTS: DeferredRegister<AttachmentType<*>> =
        DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES,
            CreateMaintenanceRequired.MOD_ID
        )

    val DURABILITY: DeferredHolder<AttachmentType<*>, AttachmentType<Int>> =
        ATTACHMENTS.register(
            "durability",
            Supplier<AttachmentType<Int>> {
                AttachmentType.builder(Supplier<Int> { 100 })
                    .serialize(Codec.INT)
                    .build()
            }
        )

    fun register(bus: IEventBus) {
        ATTACHMENTS.register(bus)
    }
}