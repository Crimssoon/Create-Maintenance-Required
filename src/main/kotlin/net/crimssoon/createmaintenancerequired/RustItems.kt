package net.crimssoon.createmaintenancerequired

import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.state.BlockState

object RustItems {

    @JvmStatic
    fun cloneStack(state: BlockState, level: LevelReader, pos: BlockPos): ItemStack {
        val stack = ItemStack(state.block.asItem())
        val be = level.getBlockEntity(pos)
        if (be != null) {
            val durability = RustDurability.get(be)
            if (durability < RustDurability.MAX) {
                stack.set(ModComponents.DURABILITY.get(), durability)
            }
        }
        return stack
    }
}