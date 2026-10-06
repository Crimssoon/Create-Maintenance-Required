package net.crimssoon.createmaintenancerequired.client.helper

import com.mojang.blaze3d.vertex.PoseStack
import net.crimssoon.createmaintenancerequired.client.render.Grouped
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

internal class RustPart(
    val quads: () -> Grouped,
    val maxDistanceSq: Double,
    val transform: (
        PoseStack,
        BlockEntity,
        BlockState,
        Float
    ) -> Unit,
    val enabled: (
        BlockEntity,
        BlockState
    ) -> Boolean = { _, _ -> true }
)

internal object RustClientTargets {

    private class Entry(
        val block: () -> Block,
        val parts: List<RustPart>
    )

    private val entries =
        ArrayList<Entry>()

    private val none =
        emptyList<RustPart>()

    fun register(
        block: () -> Block,
        parts: List<RustPart>
    ) {
        entries.add(
            Entry(
                block,
                parts
            )
        )
    }

    fun partsFor(
        block: Block
    ): List<RustPart> {
        for (entry in entries) {
            if (entry.block() === block) {
                return entry.parts
            }
        }

        return none
    }
}