package net.crimssoon.createmaintenancerequired.item

import net.crimssoon.createmaintenancerequired.rust.RustDurability
import net.crimssoon.createmaintenancerequired.rust.RustTargets
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BrushItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class WireBrushItem(
    properties: Properties
) : BrushItem(properties) {

    companion object {
        private const val REPAIR_INTERVAL = 5
        private const val REPAIR_AMOUNT = 2
        private const val BRUSH_DAMAGE = 1
        private const val MAX_DURABILITY = 100
    }

    override fun shouldCauseReequipAnimation(
        oldStack: ItemStack,
        newStack: ItemStack,
        slotChanged: Boolean
    ): Boolean {
        if (oldStack.item === this && newStack.item === this) {
            return false
        }

        return super.shouldCauseReequipAnimation(
            oldStack,
            newStack,
            slotChanged
        )
    }

    override fun useOn(
        context: UseOnContext
    ): InteractionResult {
        val level = context.level
        val pos = context.clickedPos
        val state = level.getBlockState(pos)

        val player = context.player
            ?: return InteractionResult.PASS

        if (!RustTargets.isRustable(state.block)) {
            return InteractionResult.PASS
        }

        val blockEntity = level.getBlockEntity(pos)
            ?: return InteractionResult.PASS

        val current = RustDurability.get(blockEntity)

        if (current >= MAX_DURABILITY) {
            return InteractionResult.PASS
        }

        if (!level.isClientSide) {
            player.startUsingItem(context.hand)
        }

        return InteractionResult.CONSUME
    }

    override fun onUseTick(
        level: Level,
        livingEntity: LivingEntity,
        stack: ItemStack,
        remainingUseDuration: Int
    ) {
        if (level.isClientSide) {
            super.onUseTick(
                level,
                livingEntity,
                stack,
                remainingUseDuration
            )
            return
        }

        if (livingEntity !is Player) {
            return
        }

        val elapsed =
            getUseDuration(stack, livingEntity) -
                    remainingUseDuration

        if (elapsed <= 0 || elapsed % REPAIR_INTERVAL != 0) {
            return
        }

        val hit =
            livingEntity.pick(
                4.5,
                0f,
                false
            )

        if (hit.type != HitResult.Type.BLOCK) {
            return
        }

        val blockHit = hit as BlockHitResult
        val pos = blockHit.blockPos
        val state = level.getBlockState(pos)

        // Ignore non-rustable blocks.
        if (!RustTargets.isRustable(state.block)) {
            return
        }

        val blockEntity =
            level.getBlockEntity(pos)
                ?: return

        val current =
            RustDurability.get(blockEntity)

        if (current >= MAX_DURABILITY) {
            return
        }

        val newDurability =
            (current + REPAIR_AMOUNT)
                .coerceAtMost(MAX_DURABILITY)

        RustDurability.set(
            blockEntity,
            newDurability
        )

        stack.hurtAndBreak(
            BRUSH_DAMAGE,
            livingEntity,
            LivingEntity.getSlotForHand(
                livingEntity.usedItemHand
            )
        )

        if (stack.isEmpty) {
            livingEntity.stopUsingItem()
        }
    }
}