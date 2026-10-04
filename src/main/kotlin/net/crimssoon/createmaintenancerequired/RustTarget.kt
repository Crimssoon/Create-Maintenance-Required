package net.crimssoon.createmaintenancerequired

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class RustTarget(
    val block: () -> Block,
    val damageChance: Float = 0.5f,
    val damageAmount: Int = 1,
    val onBroken: ((BlockState, ServerLevel, BlockPos) -> Unit)? = null
)

object RustTargets {

    @JvmStatic
    fun count(): Int = targets.size

    private val targets = ArrayList<RustTarget>()

    fun register(target: RustTarget) {
        targets.add(target)
    }

    @JvmStatic
    fun find(block: Block): RustTarget? {
        for (target in targets) {
            if (target.block() === block) return target
        }
        return null
    }

    fun findByItem(item: Item): RustTarget? {
        for (target in targets) {
            if (target.block().asItem() === item) return target
        }
        return null
    }

    @JvmStatic
    fun isRustable(block: Block): Boolean = find(block) != null
}