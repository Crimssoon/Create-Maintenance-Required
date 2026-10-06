package net.crimssoon.createmaintenancerequired.rust.helpers

import dev.ryanhcode.sable.Sable
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

object SableWorld {

    fun projectOut(level: Level, pos: BlockPos): BlockPos {
        val projected = Sable.HELPER.projectOutOfSubLevel(level, pos.center)
        return BlockPos.containing(projected)
    }
}