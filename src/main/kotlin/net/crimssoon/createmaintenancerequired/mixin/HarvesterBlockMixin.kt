package net.crimssoon.createmaintenancerequired.mixin

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlock
import net.crimssoon.createmaintenancerequired.ModComponents
import net.crimssoon.createmaintenancerequired.RustBreakdown
import net.crimssoon.createmaintenancerequired.RustDurability
import net.crimssoon.createmaintenancerequired.RustItems
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.HitResult
import org.spongepowered.asm.mixin.Mixin

@Mixin(HarvesterBlock::class)
abstract class HarvesterBlockMixin {

    protected fun isRandomlyTicking(state: BlockState?): Boolean {
        return true
    }

    protected fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        RustBreakdown.onRandomTick(state, level, pos, random)
    }

    fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        if (level.isClientSide) return
        val durability = stack.get(ModComponents.DURABILITY.get()) ?: return
        level.getBlockEntity(pos)?.let { RustDurability.set(it, durability) }
    }

    fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: LevelReader,
        pos: BlockPos,
        player: Player
    ): ItemStack {
        return RustItems.cloneStack(state, level, pos)
    }
}