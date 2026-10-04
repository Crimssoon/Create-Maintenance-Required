package net.crimssoon.createmaintenancerequired

import net.minecraft.util.Mth
import net.minecraft.world.item.ItemStack

object RustBar {

    private fun durability(stack: ItemStack): Int? {
        if (RustTargets.findByItem(stack.item) == null) return null
        val value = stack.get(ModComponents.DURABILITY.get()) ?: return null
        if (value >= RustDurability.MAX) return null
        return value.coerceIn(0, RustDurability.MAX)
    }

    @JvmStatic
    fun visible(stack: ItemStack): Boolean = durability(stack) != null

    @JvmStatic
    fun width(stack: ItemStack): Int {
        val value = durability(stack) ?: return 0
        return Math.round(13f * value / RustDurability.MAX)
    }

    @JvmStatic
    fun color(stack: ItemStack): Int {
        val value = durability(stack) ?: return 0
        val fraction = Math.max(0f, value.toFloat() / RustDurability.MAX)
        return Mth.hsvToRgb(fraction / 3f, 1f, 1f)
    }

    @JvmStatic
    fun handles(stack: ItemStack): Boolean = durability(stack) != null
}