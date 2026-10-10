package net.crimssoon.createmaintenancerequired.client.tools

import com.simibubi.create.foundation.item.TooltipHelper
import net.createmod.catnip.lang.FontHelper
import net.crimssoon.createmaintenancerequired.CreateMaintenanceRequired
import net.crimssoon.createmaintenancerequired.ModItems
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent

@EventBusSubscriber(
    modid = CreateMaintenanceRequired.MOD_ID,
    value = [Dist.CLIENT]
)
object OverallsTooltip {

    private const val KEY = "item.createmaintenancerequired.overalls.tooltip"
    private val PALETTE = FontHelper.Palette.STANDARD_CREATE

    @JvmStatic
    @SubscribeEvent
    fun onTooltip(event: ItemTooltipEvent) {
        if (event.itemStack.item != ModItems.OVERALLS.get()) return

        val tooltip = event.toolTip
        val index = minOf(1, tooltip.size)

        if (!Screen.hasShiftDown()) {
            tooltip.add(index, TooltipHelper.holdShift(PALETTE, false))
            return
        }

        val lines = ArrayList<Component>()
        lines.addAll(TooltipHelper.cutTextComponent(Component.translatable("$KEY.summary"), PALETTE))
        lines.add(Component.empty())
        lines.add(Component.translatable("$KEY.condition1").withStyle(ChatFormatting.GRAY))
        lines.addAll(
            TooltipHelper.cutTextComponent(
                Component.translatable("$KEY.behaviour1"),
                PALETTE.primary(),
                PALETTE.highlight(),
                1
            )
        )

        tooltip.addAll(index, lines)
    }
}