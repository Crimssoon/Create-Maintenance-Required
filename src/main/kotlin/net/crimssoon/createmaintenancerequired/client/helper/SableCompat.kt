package net.crimssoon.createmaintenancerequired.client.helper

import dev.ryanhcode.sable.companion.ClientSubLevelAccess
import dev.ryanhcode.sable.companion.SableCompanion
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.harvester.HarvesterLerpedSpeed
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import org.joml.Quaternionf
import org.joml.Vector3d

class SubLevelTransform(
    val gx: Double,
    val gy: Double,
    val gz: Double,
    val rotation: Quaternionf
)

object SableCompat {

    fun transformFor(level: Level, pos: BlockPos): SubLevelTransform? {
        val sub = SableCompanion.INSTANCE.getContaining(level, pos) ?: return null
        val pose = (sub as? ClientSubLevelAccess)?.renderPose() ?: sub.logicalPose()
        val g = pose.transformPosition(
            Vector3d(
                pos.x + 0.5,
                pos.y + 0.5,
                pos.z + 0.5
            )
        )

        return SubLevelTransform(
            g.x,
            g.y,
            g.z,
            Quaternionf().set(pose.orientation())
        )
    }

    fun harvesterSpeed(be: BlockEntity, partialTick: Float): Float? {
        val lerped = (be as Any) as? HarvesterLerpedSpeed ?: return null
        return lerped.`sable$getLerpedFloat`().getValue(partialTick)
    }
}