package net.crimssoon.beginnermod

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister

object ModBlocks {

    val BLOCKS: DeferredRegister.Blocks =
        DeferredRegister.createBlocks(BeginnerMod.MOD_ID)

    val TEST_BLOCK: DeferredBlock<Block> =
        BLOCKS.registerSimpleBlock(
            "test_block",
            BlockBehaviour.Properties.of()
                .strength(5.0f, 6.0f)
        )

    fun register(eventBus: IEventBus) {
        BLOCKS.register(eventBus)
    }
}