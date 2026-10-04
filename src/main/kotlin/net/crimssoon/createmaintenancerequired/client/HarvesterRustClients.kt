package net.crimssoon.createmaintenancerequired.client

import com.mojang.math.Axis
import com.simibubi.create.AllBlocks
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlock
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity
import net.crimssoon.createmaintenancerequired.CreateMaintenanceRequired
import net.minecraft.core.Direction
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModList
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent

@EventBusSubscriber(
    modid = CreateMaintenanceRequired.MOD_ID,
    value = [Dist.CLIENT]
)
object HarvesterRustClient {

    private const val PIVOT_Y = 6.0 / 16.0
    private const val PIVOT_Z = 9.0 / 16.0
    private const val SPIN_SIGN = -1f
    private const val SPIN_OFFSET = 0f
    private const val YAW_OFFSET = 0f

    private val sableLoaded: Boolean by lazy { ModList.get().isLoaded("sable") }

    private fun horizontalAngle(facing: Direction): Float {
        var angle = facing.toYRot()
        if (facing.axis == Direction.Axis.X) angle += 180f
        return angle
    }

    @JvmStatic
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        val blade = RustPart(
            quads = { DamageOverlayRenderer.quadsForModel(AllPartialModels.HARVESTER_BLADE.get()) },
            maxDistanceSq = 12.0 * 12.0,
            transform = { pose, be, state, partial ->
                if (state.hasProperty(HarvesterBlock.FACING)) {
                    val angle = horizontalAngle(state.getValue(HarvesterBlock.FACING)) + YAW_OFFSET

                    val lerped = if (sableLoaded) SableCompat.harvesterSpeed(be, partial) else null
                    val fallback = ((be.level?.gameTime ?: 0L) + partial) / 20f *
                            ((be as? HarvesterBlockEntity)?.animatedSpeed ?: 0f)
                    val raw = (lerped ?: fallback) % 360f
                    val spin = if (raw.isFinite()) SPIN_SIGN * raw + SPIN_OFFSET else 0f

                    pose.translate(0.5, 0.5, 0.5)
                    pose.mulPose(Axis.YP.rotationDegrees(angle))
                    pose.translate(-0.5, -0.5, -0.5)
                    pose.translate(0.0, PIVOT_Y, PIVOT_Z)
                    pose.mulPose(Axis.XP.rotationDegrees(-spin))
                    pose.translate(0.0, -PIVOT_Y, -PIVOT_Z)
                }
            }
        )

        RustClientTargets.register({ AllBlocks.MECHANICAL_HARVESTER.get() }, listOf(blade))
    }
}