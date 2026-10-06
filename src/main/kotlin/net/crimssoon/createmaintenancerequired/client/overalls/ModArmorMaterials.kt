package net.crimssoon.createmaintenancerequired.client.overalls

import net.crimssoon.createmaintenancerequired.CreateMaintenanceRequired
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.EnumMap
import java.util.function.Supplier

object ModArmorMaterials {

    private val MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, CreateMaintenanceRequired.MOD_ID)

    val OVERALLS = MATERIALS.register("overalls", Supplier {
        val defense = EnumMap<ArmorItem.Type, Int>(ArmorItem.Type::class.java)
        defense[ArmorItem.Type.BOOTS] = 0
        defense[ArmorItem.Type.LEGGINGS] = 2
        defense[ArmorItem.Type.CHESTPLATE] = 0
        defense[ArmorItem.Type.HELMET] = 0
        defense[ArmorItem.Type.BODY] = 0

        ArmorMaterial(
            defense,
            15,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            Supplier { Ingredient.of(Items.LEATHER) },
            listOf(
                ArmorMaterial.Layer(
                    ResourceLocation.fromNamespaceAndPath(CreateMaintenanceRequired.MOD_ID, "overalls")
                )
            ),
            0.0f,
            0.0f
        )
    })

    fun register(bus: IEventBus) {
        MATERIALS.register(bus)
    }
}