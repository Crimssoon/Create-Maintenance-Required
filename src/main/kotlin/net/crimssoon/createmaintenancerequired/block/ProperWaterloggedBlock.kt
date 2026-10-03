package net.crimssoon.createmaintenancerequired.block

import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids

interface ProperWaterloggedBlock : SimpleWaterloggedBlock {

    companion object {
        val WATERLOGGED: BooleanProperty =
            BlockStateProperties.WATERLOGGED

        fun withWater(
            level: LevelAccessor,
            placementState: BlockState,
            pos: BlockPos
        ): BlockState {
            if (placementState.isAir) {
                val fluidState = level.getFluidState(pos)

                return if (fluidState.type == Fluids.WATER) {
                    fluidState.createLegacyBlock()
                } else {
                    placementState
                }
            }

            if (placementState.block !is SimpleWaterloggedBlock) {
                return placementState
            }

            return placementState.setValue(
                BlockStateProperties.WATERLOGGED,
                level.getFluidState(pos).type == Fluids.WATER
            )
        }
    }

    fun fluidState(state: BlockState): FluidState {
        return if (state.getValue(WATERLOGGED)) {
            Fluids.WATER.getSource(false)
        } else {
            Fluids.EMPTY.defaultFluidState()
        }
    }

    fun updateWater(
        level: LevelAccessor,
        state: BlockState,
        pos: BlockPos
    ) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(
                pos,
                Fluids.WATER,
                Fluids.WATER.getTickDelay(level)
            )
        }
    }

    fun withWater(
        placementState: BlockState,
        ctx: BlockPlaceContext
    ): BlockState {
        return withWater(
            ctx.level,
            placementState,
            ctx.clickedPos
        )
    }
}