package net.crimssoon.createmaintenancerequired

import com.simibubi.create.AllDamageTypes
import com.simibubi.create.AllSoundEvents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import kotlin.math.ceil
import kotlin.math.max

@EventBusSubscriber(modid = CreateMaintenanceRequired.MOD_ID)
object OverallsProtection {

    private const val TAG_LAST_HIT = "overalls_last_hit"
    private const val COOLDOWN_TICKS = 10

    @SubscribeEvent
    @JvmStatic
    fun onIncomingDamage(event: LivingIncomingDamageEvent) {
        val player = event.entity as? Player ?: return
        if (player.level().isClientSide) return

        val source = event.source
        if (!source.`is`(AllDamageTypes.DRILL) && !source.`is`(AllDamageTypes.SAW)) return

        val stack = player.getItemBySlot(EquipmentSlot.LEGS)
        if (stack.item !is OverallsItem) return

        event.isCanceled = true

        val data = player.persistentData
        val last = data.getInt(TAG_LAST_HIT)
        val elapsed = player.tickCount - last
        if (elapsed in 0 until COOLDOWN_TICKS) return

        data.putInt(TAG_LAST_HIT, player.tickCount)

        stack.hurtAndBreak(
            max(1, ceil(event.amount).toInt()),
            player,
            EquipmentSlot.LEGS
        )

        player.level().playSound(
            null,
            player.x,
            player.y,
            player.z,
            AllSoundEvents.SANDING_LONG.getMainEvent(),
            player.soundSource,
            1.0f,
            1.0f
        )
    }
}