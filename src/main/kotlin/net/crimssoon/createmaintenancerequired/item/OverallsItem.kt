package net.crimssoon.createmaintenancerequired

import net.minecraft.core.Holder
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial

class OverallsItem(
    material: Holder<ArmorMaterial>,
    properties: Properties
) : ArmorItem(material, Type.LEGGINGS, properties)