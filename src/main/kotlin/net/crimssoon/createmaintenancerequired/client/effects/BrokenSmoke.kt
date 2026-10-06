package net.crimssoon.createmaintenancerequired.client.effects

import net.crimssoon.createmaintenancerequired.CreateMaintenanceRequired
import net.crimssoon.createmaintenancerequired.client.helper.SableCompat
import net.crimssoon.createmaintenancerequired.rust.RustDurability
import net.crimssoon.createmaintenancerequired.rust.RustTracker
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModList
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent

@EventBusSubscriber(
    modid = CreateMaintenanceRequired.MOD_ID,
    value = [Dist.CLIENT]
)
object BrokenSmoke {

    private const val MAX_DISTANCE_SQ = 32.0 * 32.0
    private const val INTERVAL_TICKS = 4L
    private const val CHANCE = 0.6f

    private val machines = ArrayList<BlockEntity>()
    private val sableLoaded: Boolean by lazy { ModList.get().isLoaded("sable") }

    @JvmStatic
    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent.Post) {
        val mc = Minecraft.getInstance()
        val level = mc.level ?: return
        val player = mc.player ?: return
        if (mc.isPaused) return
        if (level.gameTime % INTERVAL_TICKS != 0L) return

        machines.clear()
        RustTracker.collect(level, machines)

        for (be in machines) {
            if (RustDurability.get(be) > 0) continue

            val pos = be.blockPos
            val t = if (sableLoaded) SableCompat.transformFor(level, pos) else null

            val x = t?.gx ?: (pos.x + 0.5)
            val y = t?.gy ?: (pos.y + 0.5)
            val z = t?.gz ?: (pos.z + 0.5)

            val dx = x - player.x
            val dy = y - player.y
            val dz = z - player.z
            if (dx * dx + dy * dy + dz * dz > MAX_DISTANCE_SQ) continue

            val random = level.random
            if (random.nextFloat() > CHANCE) continue

            level.addParticle(
                ParticleTypes.SMOKE,
                x + (random.nextDouble() - 0.5) * 0.6,
                y + 0.4,
                z + (random.nextDouble() - 0.5) * 0.6,
                0.0, 0.04, 0.0
            )
        }
    }
}