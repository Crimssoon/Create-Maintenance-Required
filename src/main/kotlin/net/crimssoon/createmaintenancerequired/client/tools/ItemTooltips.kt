package net.crimssoon.createmaintenancerequired.client.tools

import net.crimssoon.createmaintenancerequired.CreateMaintenanceRequired
import net.crimssoon.createmaintenancerequired.rust.helpers.ModComponents
import net.crimssoon.createmaintenancerequired.rust.RustDurability
import net.crimssoon.createmaintenancerequired.rust.RustTargets
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent

@EventBusSubscriber(
    modid = CreateMaintenanceRequired.MOD_ID,
    value = [Dist.CLIENT]
)
object ItemTooltips {

    @JvmStatic
    @SubscribeEvent
    fun onTooltip(event: ItemTooltipEvent) {
        val stack = event.itemStack
        if (RustTargets.findByItem(stack.item) == null) return

        val durability = stack.get(ModComponents.DURABILITY.get()) ?: RustDurability.MAX

        val color = when {
            durability > 66 -> ChatFormatting.GREEN
            durability > 33 -> ChatFormatting.YELLOW
            else -> ChatFormatting.RED
        }

        val line = Component.literal("Durability: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(durability.toString()).withStyle(color))

        val tooltip = event.toolTip
        var index = tooltip.size

        if (event.flags.isAdvanced) {
            val itemId = BuiltInRegistries.ITEM.getKey(stack.item).toString()
            val idIndex = tooltip.indexOfLast { it.string == itemId }
            if (idIndex >= 0) index = idIndex
        }

        tooltip.add(index, line)
    }
}