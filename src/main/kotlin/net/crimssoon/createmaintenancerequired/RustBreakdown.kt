package net.crimssoon.createmaintenancerequired

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState

object RustBreakdown {

    @JvmStatic
    fun onRandomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        val target = RustTargets.find(state.block) ?: return
        if (random.nextFloat() >= target.damageChance) return

        val be = level.getBlockEntity(pos) ?: return
        RustDurability.damage(be, target.damageAmount)

        if (RustDurability.get(be) <= 0) {
            target.onBroken?.invoke(state, level, pos)
        }
    }
}