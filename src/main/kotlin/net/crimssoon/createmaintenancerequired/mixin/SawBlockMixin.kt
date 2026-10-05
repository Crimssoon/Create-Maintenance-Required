package net.crimssoon.createmaintenancerequired.mixin

import com.simibubi.create.content.kinetics.saw.SawBlock
import com.simibubi.create.content.kinetics.saw.SawBlockEntity
import net.crimssoon.createmaintenancerequired.ModComponents
import net.crimssoon.createmaintenancerequired.RustBreakdown
import net.crimssoon.createmaintenancerequired.RustDurability
import net.crimssoon.createmaintenancerequired.RustItems
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.HitResult
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import net.minecraft.world.phys.AABB

@Mixin(SawBlock::class)
abstract class SawBlockMixin {

    protected fun isRandomlyTicking(state: BlockState?): Boolean {
        return true
    }

    protected fun randomTick(
        state: BlockState,
        level: ServerLevel,
        pos: BlockPos,
        random: RandomSource
    ) {
        RustBreakdown.onRandomTick(
            state,
            level,
            pos,
            random
        )
    }

    fun setPlacedBy(
        level: Level,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        stack: ItemStack
    ) {
        if (level.isClientSide)
            return

        val durability =
            stack.get(ModComponents.DURABILITY.get())
                ?: RustDurability.MAX

        level.getBlockEntity(pos)?.let {
            RustDurability.set(it, durability)
        }
    }

    fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: LevelReader,
        pos: BlockPos,
        player: Player
    ): ItemStack {
        return RustItems.cloneStack(
            state,
            level,
            pos
        )
    }

    private companion object {

        @JvmStatic
        @Inject(
            method = ["entityInside"],
            at = [At("HEAD")]
        )
        private fun poisonEntity(
            state: BlockState,
            level: Level,
            pos: BlockPos,
            entity: Entity,
            ci: CallbackInfo
        ) {
            if (level.isClientSide)
                return

            if (!AABB(pos).deflate(0.1).intersects(entity.boundingBox))
                return

            if (entity !is LivingEntity)
                return

            level.getBlockEntity(pos)?.let { be ->
                if (be !is SawBlockEntity)
                    return@let

                if (be.getSpeed() == 0f)
                    return@let

                if (RustDurability.get(be) >= 20)
                    return@let

                entity.addEffect(
                    MobEffectInstance(
                        MobEffects.POISON,
                        100,
                        0
                    )
                )
            }
        }
    }
}