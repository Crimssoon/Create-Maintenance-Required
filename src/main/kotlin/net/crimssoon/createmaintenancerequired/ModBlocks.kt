package net.crimssoon.createmaintenancerequired

import net.crimssoon.createmaintenancerequired.block.BrokenHarvesterBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object ModBlocks {

    val BLOCKS: DeferredRegister.Blocks =
        DeferredRegister.createBlocks(CreateMaintenanceRequired.MOD_ID)

    val BROKEN_HARVESTER: DeferredBlock<BrokenHarvesterBlock> =
        BLOCKS.register(
            "broken_harvester",
            Supplier {
                BrokenHarvesterBlock(
                    BlockBehaviour.Properties.of()
                        .strength(1.5f, 6.0f)
                        .noOcclusion()
                )
            }
        )

    fun register(eventBus: IEventBus) {
        BLOCKS.register(eventBus)
    }
}