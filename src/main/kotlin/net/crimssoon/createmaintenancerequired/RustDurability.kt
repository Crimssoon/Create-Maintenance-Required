package net.crimssoon.createmaintenancerequired

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity

object RustDurability {

    const val MAX = 100

    @JvmStatic
    fun get(be: BlockEntity): Int = be.getData(ModAttachments.DURABILITY)

    @JvmStatic
    fun setRaw(be: BlockEntity, value: Int) {
        be.setData(ModAttachments.DURABILITY, value.coerceIn(0, MAX))
        be.setChanged()
    }

    fun set(be: BlockEntity, value: Int) {
        val old = get(be)
        val new = value.coerceIn(0, MAX)
        if (new == old) return

        be.setData(ModAttachments.DURABILITY, new)
        be.setChanged()

        val level = be.level
        if (level != null && !level.isClientSide && old / 5 != new / 5) {
            level.sendBlockUpdated(be.blockPos, be.blockState, be.blockState, Block.UPDATE_CLIENTS)
        }
    }

    fun damage(be: BlockEntity, amount: Int = 1) = set(be, get(be) - amount)

    fun repairFully(be: BlockEntity) = set(be, MAX)
}