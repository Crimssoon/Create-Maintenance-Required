package net.crimssoon.createmaintenancerequired.block

import com.simibubi.create.AllShapes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class BrokenHarvesterBlock(
    properties: Properties
) : Block(properties), ProperWaterloggedBlock {

    companion object {
        val FACING: DirectionProperty =
            BlockStateProperties.HORIZONTAL_FACING
    }

    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(
                    FACING,
                    Direction.NORTH
                )
                .setValue(
                    ProperWaterloggedBlock.WATERLOGGED,
                    false
                )
        )
    }

    override fun createBlockStateDefinition(
        builder: StateDefinition.Builder<Block, BlockState>
    ) {
        builder.add(
            FACING,
            ProperWaterloggedBlock.WATERLOGGED
        )
    }

    override fun getStateForPlacement(
        context: BlockPlaceContext
    ): BlockState {
        val facing: Direction

        if (context.clickedFace.axis.isVertical) {
            facing = context.horizontalDirection.opposite
        } else {
            val attachedPos = context.clickedPos.relative(
                context.clickedFace.opposite
            )

            val blockState = context.level.getBlockState(attachedPos)

            facing = if (blockState.block is BrokenHarvesterBlock) {
                blockState.getValue(FACING)
            } else {
                context.clickedFace
            }
        }

        return withWater(
            defaultBlockState()
                .setValue(FACING, facing),
            context
        )
    }

    override fun canSurvive(
        state: BlockState,
        level: LevelReader,
        pos: BlockPos
    ): Boolean {
        val facing = state.getValue(FACING)

        val supportPos = pos.relative(
            facing.opposite
        )

        val supportState = level.getBlockState(
            supportPos
        )

        return supportState.isFaceSturdy(
            level,
            supportPos,
            facing
        )
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return AllShapes.HARVESTER_BASE.get(
            state.getValue(FACING)
        )
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return AllShapes.HARVESTER_BASE.get(
            state.getValue(FACING)
        )
    }

    override fun getFluidState(
        state: BlockState
    ): FluidState {
        return fluidState(state)
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        updateWater(
            level,
            state,
            pos
        )

        return state
    }
}