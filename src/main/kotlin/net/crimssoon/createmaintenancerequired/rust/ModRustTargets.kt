package net.crimssoon.createmaintenancerequired.rust

import com.simibubi.create.AllBlocks

object ModRustTargets {

    fun register() {
        RustTargets.register(
            RustTarget(
                block = { AllBlocks.MECHANICAL_HARVESTER.get() },
                damageChance = 0.5f,
                damageAmount = 1,
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