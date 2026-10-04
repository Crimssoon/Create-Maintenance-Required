package net.crimssoon.createmaintenancerequired

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlock
import net.crimssoon.createmaintenancerequired.block.BrokenHarvesterBlock

object ModRustTargets {

    fun register() {
        RustTargets.register(
            RustTarget(
                block = { AllBlocks.MECHANICAL_HARVESTER.get() },
                damageChance = 0.5f,
                damageAmount = 1,
                onBroken = { state, level, pos ->
                    val broken = ModBlocks.BROKEN_HARVESTER.get().defaultBlockState()
                        .setValue(BrokenHarvesterBlock.FACING, state.getValue(HarvesterBlock.FACING))
                    level.setBlockAndUpdate(pos, broken)
                }
            )
        )
        RustTargets.register(
            RustTarget(
                block = { AllBlocks.MECHANICAL_SAW.get() },
                damageChance = 0.5f,
                damageAmount = 1
            )
        )

        RustTargets.register(
            RustTarget(
                block = { AllBlocks.MECHANICAL_DRILL.get() },
                damageChance = 0.5f,
                damageAmount = 1
            )
        )
    }
}