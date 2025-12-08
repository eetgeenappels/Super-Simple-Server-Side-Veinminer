package nl.eetgeenappels.ssssv.veinminer

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import nl.eetgeenappels.ssssv.SuperSimpleServerSideVeinminer
import nl.eetgeenappels.ssssv.config.Configs

object Veinminer {

    fun onBlockBreak(level: Level, player: Player, blockPos: BlockPos, blockState: BlockState, blockEntity: BlockEntity?) {
        val config = Configs.ssssvConfig
        if (!Configs.ssssvConfig.veinmineEnabled)
            return
        if (level.isClientSide)
            return
        if (level !is ServerLevel) {
            return
        }
        if (!canVeinmineBlock(blockState.block))
            return
        if (Configs.ssssvConfig.holdShiftToVeinmine && !player.isShiftKeyDown)
            return

        val searchStrategy = Configs.ssssvConfig.searchSection.blockSearchMode.strategy
        val blocksToMine = searchStrategy.search(
            blockPos,
            blockState.block,
            player.level(),
            Configs.ssssvConfig.veinmineMaxBlocks.get()
        )

        SuperSimpleServerSideVeinminer.logger.info("Veinmine found ${blocksToMine.size} blocks to mine.")

        for (oreBlock in blocksToMine) {

            if (oreBlock == blockPos)
                continue // skip the original block, it's already being mined

            val state = level.getBlockState(oreBlock)

            val drops = Block.getDrops(state, level, oreBlock, blockEntity)
            for (itemStack in drops) {
                if (Configs.ssssvConfig.teleportItemsToPlayer) {
                    val droppedItem = player.drop(itemStack, false)
                    droppedItem?.setPos(player.x, player.y + 0.5, player.z)
                } else {
                    Block.popResource(level, oreBlock, itemStack)
                }
            }

            // set block to air
            level.setBlock(oreBlock, Blocks.AIR.defaultBlockState(), 3)
        }


        // apply durability and hunger cost
        val durabilityCost = (blocksToMine.size - 1) * Configs.ssssvConfig.durabilityTakenPerBlock.get()
        if (player.mainHandItem.isDamageableItem) {
            player.mainHandItem.damageValue += durabilityCost.toInt()
        }
        val hungerCost = (blocksToMine.size - 1) * Configs.ssssvConfig.hungerTakenPerBlock.get()
        player.foodData.foodLevel -= hungerCost.toInt()
    }
    fun canVeinmineBlock(block: Block): Boolean {
        return isInWhitelist(block) && !isInBlacklist(block);
    }

    fun isInWhitelist(block: Block): Boolean {
        val config = Configs.ssssvConfig
        if (!config.blocksSection.blocksWhitelistEnbabled)
            return true
        return config.blocksSection.blocksWhitelist.contains(block)
    }
    fun isInBlacklist(block: Block): Boolean {
        val config = Configs.ssssvConfig
        if (!config.blocksSection.blocksBlacklistEnabled)
            return false
        return config.blocksSection.blocsBlacklist.contains(block)
    }
}