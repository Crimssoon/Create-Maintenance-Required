package net.crimssoon.createmaintenancerequired

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import net.minecraft.world.level.block.entity.BlockEntity

object RustWork {

    private const val ATTACHMENTS_KEY = "neoforge:attachments"
    private const val DURABILITY_KEY = CreateMaintenanceRequired.MOD_ID + ":durability"

    @JvmStatic
    fun isBroken(context: MovementContext): Boolean {
        val level = context.world
        val localPos = context.localPos
        if (level != null && localPos != null) {
            val be = level.getBlockEntity(localPos)
            if (be != null && RustTargets.isRustable(be.blockState.block)) {
                return RustDurability.get(be) <= 0
            }
        }

        val data = context.blockEntityData ?: return false
        val attachments = data.getCompound(ATTACHMENTS_KEY)
        if (!attachments.contains(DURABILITY_KEY)) return false
        return attachments.getInt(DURABILITY_KEY) <= 0
    }

    @JvmStatic
    fun isBroken(be: BlockEntity): Boolean =
        RustTargets.isRustable(be.blockState.block) && RustDurability.get(be) <= 0
}