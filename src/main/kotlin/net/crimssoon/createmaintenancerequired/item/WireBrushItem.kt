package net.crimssoon.createmaintenancerequired.item

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlock
import net.crimssoon.createmaintenancerequired.ModComponents
import net.crimssoon.createmaintenancerequired.RustDurability
import net.crimssoon.createmaintenancerequired.RustTargets
import net.crimssoon.createmaintenancerequired.block.BrokenHarvesterBlock
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
        private const val REPAIR_AMOUNT = 1
        private const val BRUSH_DAMAGE = 1
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

        /*
         * Broken harvester is also a valid repair target.
         */
        val isBrokenHarvester =
            state.block ===
                    net.crimssoon.createmaintenancerequired.ModBlocks
                        .BROKEN_HARVESTER.get()

        if (!isBrokenHarvester &&
            !RustTargets.isRustable(state.block)
        ) {
            return InteractionResult.PASS
        }

        val player = context.player
            ?: return InteractionResult.PASS

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
        /*
         * Keep vanilla brush particles and animation on the client.
         */
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
            livingEntity.stopUsingItem()
            return
        }

        val blockHit = hit as BlockHitResult
        val pos = blockHit.blockPos
        val state = level.getBlockState(pos)

        /*
         * Special case:
         *
         * Broken Harvester -> Mechanical Harvester
         * with 0 durability.
         */
        if (state.block ===
            net.crimssoon.createmaintenancerequired.ModBlocks
                .BROKEN_HARVESTER.get()
        ) {
            val facing =
                state.getValue(
                    BrokenHarvesterBlock.FACING
                )

            val repairedState =
                AllBlocks.MECHANICAL_HARVESTER
                    .get()
                    .defaultBlockState()
                    .setValue(
                        HarvesterBlock.FACING,
                        facing
                    )

            level.setBlockAndUpdate(
                pos,
                repairedState
            )

            val blockEntity =
                level.getBlockEntity(pos)

            if (blockEntity != null) {
                RustDurability.set(
                    blockEntity,
                    0
                )
            }

            stack.hurtAndBreak(
                BRUSH_DAMAGE,
                livingEntity,
                LivingEntity.getSlotForHand(
                    livingEntity.usedItemHand
                )
            )

            livingEntity.stopUsingItem()
            return
        }

        /*
         * Normal rustable block repair.
         */
        if (!RustTargets.isRustable(state.block)) {
            livingEntity.stopUsingItem()
            return
        }

        val blockEntity =
            level.getBlockEntity(pos)

        if (blockEntity == null) {
            livingEntity.stopUsingItem()
            return
        }

        val current =
            RustDurability.get(blockEntity)

        if (current >= RustDurability.MAX) {
            livingEntity.stopUsingItem()
            return
        }

        RustDurability.set(
            blockEntity,
            current + REPAIR_AMOUNT
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
