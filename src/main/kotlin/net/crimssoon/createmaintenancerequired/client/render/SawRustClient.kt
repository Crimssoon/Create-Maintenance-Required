package net.crimssoon.createmaintenancerequired.client.render

import com.mojang.math.Axis
import com.simibubi.create.AllBlocks
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.saw.SawBlock
import dev.engine_room.flywheel.lib.model.baked.PartialModel
import net.crimssoon.createmaintenancerequired.CreateMaintenanceRequired
import net.crimssoon.createmaintenancerequired.client.helper.RustClientTargets
import net.crimssoon.createmaintenancerequired.client.helper.RustPart
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent

@EventBusSubscriber(
    modid = CreateMaintenanceRequired.MOD_ID,
    value = [Dist.CLIENT]
)
object SawRustClient {

    private enum class Mode {
        INACTIVE,
        ACTIVE,
        REVERSE
    }

    private fun modeOf(be: BlockEntity): Mode {
        val speed = (be as? KineticBlockEntity)?.speed ?: 0f

        return when {
            speed > 0f -> Mode.ACTIVE
            speed < 0f -> Mode.REVERSE
            else -> Mode.INACTIVE
        }
    }

    private fun horizontalAngle(facing: Direction): Float {
        if (facing.axis.isVertical) return 0f

        var angle = facing.toYRot()

        if (facing.axis == Direction.Axis.X) {
            angle = -angle
        }

        return angle
    }

    private fun verticalAngle(facing: Direction): Float {
        return when (facing) {
            Direction.UP -> -90f
            Direction.DOWN -> 90f
            else -> 0f
        }
    }

    private fun part(
        model: () -> PartialModel,
        horizontal: Boolean,
        mode: Mode
    ): RustPart {
        return RustPart(
            quads = {
                DamageOverlayRenderer.quadsForModel(model().get())
            },
            maxDistanceSq = 12.0 * 12.0,
            transform = { pose, _, state, _ ->
                val facing = state.getValue(BlockStateProperties.FACING)

                pose.translate(0.5, 0.5, 0.5)
                pose.mulPose(
                    Axis.YP.rotationDegrees(
                        horizontalAngle(facing)
                    )
                )
                pose.mulPose(
                    Axis.XP.rotationDegrees(
                        verticalAngle(facing)
                    )
                )

                if (
                    !horizontal &&
                    state.getValue(SawBlock.AXIS_ALONG_FIRST_COORDINATE)
                ) {
                    pose.mulPose(
                        Axis.ZP.rotationDegrees(90f)
                    )
                }

                pose.translate(-0.5, -0.5, -0.5)
            },
            enabled = { be, state ->
                state.getValue(BlockStateProperties.FACING).axis.isHorizontal == horizontal &&
                        modeOf(be) == mode
            }
        )
    }

    @JvmStatic
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        RustClientTargets.register(
            { AllBlocks.MECHANICAL_SAW.get() },
            listOf(
                part(
                    { AllPartialModels.SAW_BLADE_HORIZONTAL_INACTIVE },
                    true,
                    Mode.INACTIVE
                ),
                part(
                    { AllPartialModels.SAW_BLADE_HORIZONTAL_ACTIVE },
                    true,
                    Mode.ACTIVE
                ),
                part(
                    { AllPartialModels.SAW_BLADE_HORIZONTAL_REVERSED },
                    true,
                    Mode.REVERSE
                ),
                part(
                    { AllPartialModels.SAW_BLADE_VERTICAL_INACTIVE },
                    false,
                    Mode.INACTIVE
                ),
                part(
                    { AllPartialModels.SAW_BLADE_VERTICAL_ACTIVE },
                    false,
                    Mode.ACTIVE
                ),
                part(
                    { AllPartialModels.SAW_BLADE_VERTICAL_REVERSED },
                    false,
                    Mode.REVERSE
                )
            )
        )
    }
}