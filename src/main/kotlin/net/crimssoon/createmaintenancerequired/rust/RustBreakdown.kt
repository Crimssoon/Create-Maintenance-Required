package net.crimssoon.createmaintenancerequired.rust

import net.crimssoon.createmaintenancerequired.rust.helpers.SableWorld
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.FluidTags
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.fml.ModList

object RustBreakdown {

    private val sableLoaded: Boolean by lazy { ModList.get().isLoaded("sable") }

    private fun wetAt(level: ServerLevel, pos: BlockPos): Boolean {
        val state = level.getBlockState(pos)

        if (state.hasProperty(BlockStateProperties.WATERLOGGED) &&
            state.getValue(BlockStateProperties.WATERLOGGED)
        ) return true

        if (level.getFluidState(pos).`is`(FluidTags.WATER)) return true
        if (level.isRainingAt(pos.above())) return true

        for (direction in Direction.entries) {
            if (level.getFluidState(pos.relative(direction)).`is`(FluidTags.WATER)) return true
        }
        return false
    }

    private fun isWet(level: ServerLevel, pos: BlockPos): Boolean {
        if (wetAt(level, pos)) return true
        if (!sableLoaded) return false

        val projected = SableWorld.projectOut(level, pos)
        return projected != pos && wetAt(level, projected)
    }

    @JvmStatic
    fun onRandomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        val target = RustTargets.find(state.block) ?: return
        if (random.nextFloat() >= target.damageChance) return

        val be = level.getBlockEntity(pos) ?: return

        val amount = if (isWet(level, pos)) target.damageAmount * target.wetMultiplier else target.damageAmount
        RustDurability.damage(be, amount)
    }
}