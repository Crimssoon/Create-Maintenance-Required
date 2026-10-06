package net.crimssoon.createmaintenancerequired.rust

import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

object RustTracker {

    private val tracked = LinkedHashSet<BlockEntity>()

    @JvmStatic
    fun add(be: BlockEntity) {
        val level = be.level ?: return

        if (!level.isClientSide) return
        if (!RustTargets.isRustable(be.blockState.block)) return

        synchronized(tracked) {
            tracked.add(be)
        }
    }

    @JvmStatic
    fun remove(be: BlockEntity) {
        synchronized(tracked) {
            tracked.remove(be)
        }
    }

    fun collect(
        level: Level,
        out: MutableList<BlockEntity>
    ) {
        synchronized(tracked) {
            tracked.removeIf { be ->
                be.isRemoved ||
                        be.level !== level ||
                        !RustTargets.isRustable(
                            be.blockState.block
                        )
            }

            out.addAll(tracked)
        }
    }

    fun clear() {
        synchronized(tracked) {
            tracked.clear()
        }
    }
}