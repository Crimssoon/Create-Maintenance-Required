package net.crimssoon.createmaintenancerequired.rust

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

class RustTarget(
    val block: () -> Block,
    val damageChance: Float = 1f,
    val damageAmount: Int = 1,
    val wetMultiplier: Int = 2,
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