package net.crimssoon.createmaintenancerequired.client.render

import com.mojang.math.Axis
import com.simibubi.create.AllBlocks
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import net.createmod.catnip.math.AngleHelper
import net.crimssoon.createmaintenancerequired.CreateMaintenanceRequired
import net.crimssoon.createmaintenancerequired.client.helper.RustClientTargets
import net.crimssoon.createmaintenancerequired.client.helper.RustPart
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent

@EventBusSubscriber(
    modid = CreateMaintenanceRequired.MOD_ID,
    value = [Dist.CLIENT]
)
object DrillRustClient {

    @JvmStatic
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        val head = RustPart(
            quads = {
                DamageOverlayRenderer.quadsForModel(
                    AllPartialModels.DRILL_HEAD.get()
                )
            },
            maxDistanceSq = 12.0 * 12.0,
            transform = { pose, be, state, _ ->
                val facing =
                    state.getValue(
                        BlockStateProperties.FACING
                    )

                val kinetic =
                    be as? KineticBlockEntity
                        ?: return@RustPart

                val angle =
                    KineticBlockEntityRenderer.getAngleForBe(
                        kinetic,
                        be.blockPos,
                        facing.axis
                    )

                pose.translate(
                    0.5,
                    0.5,
                    0.5
                )

                pose.mulPose(
                    Axis.YP.rotationDegrees(
                        AngleHelper.horizontalAngle(facing)
                    )
                )

                pose.mulPose(
                    Axis.XP.rotationDegrees(
                        AngleHelper.verticalAngle(facing)
                    )
                )

                val rotation =
                    if (
                        facing.axisDirection ==
                        Direction.AxisDirection.NEGATIVE
                    ) {
                        -angle
                    } else {
                        angle
                    }

                pose.mulPose(
                    Axis.ZP.rotation(rotation)
                )

                pose.translate(
                    -0.5,
                    -0.5,
                    -0.5
                )
            }
        )

        RustClientTargets.register(
            { AllBlocks.MECHANICAL_DRILL.get() },
            listOf(head)
        )
    }
}