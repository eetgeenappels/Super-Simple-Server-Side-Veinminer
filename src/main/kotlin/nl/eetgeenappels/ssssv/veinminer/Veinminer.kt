package nl.eetgeenappels.ssssv.veinminer

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import nl.eetgeenappels.ssssv.SuperSimpleServerSideVeinminer
import nl.eetgeenappels.ssssv.config.Configs
import nl.eetgeenappels.ssssv.config.SSSSVConfig

object Veinminer {

    fun onBlockBreak(level: Level, player: Player, blockPos: BlockPos, blockState: BlockState, blockEntity: BlockEntity?) {
        if (!Configs.ssssvConfig.veinmineEnabled.get())
            return
        if (level.isClientSide)
            return
        if (level !is ServerLevel)
            return
        if (!canVeinmineBlock(blockState.block))
            return
        if (!isInToolList(player.mainHandItem.item))
            return
        if (Configs.ssssvConfig.enableInCreativeMode.get() && player.isCreative)
            return
        if (Configs.ssssvConfig.holdShiftToVeinmine.get() && !player.isShiftKeyDown)
            return

        val searchStrategy = Configs.ssssvConfig.blockSearchMode.strategy
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

            if (!player.isCreative) {

                val state = level.getBlockState(oreBlock)

                val drops = Block.getDrops(state, level, oreBlock, blockEntity)
                for (itemStack in drops) {

                    when (Configs.ssssvConfig.collectionMode) {
                        SSSSVConfig.CollectionModes.DROP_NORMALLY -> {
                            Block.popResource(level, oreBlock, itemStack)
                        }

                        SSSSVConfig.CollectionModes.ADD_TO_INVENTORY_OR_DROP -> {
                            val inventory = player.inventory

                            if (inventory.freeSlot == -1) {
                                player.drop(itemStack, false)
                            } else {
                                inventory.add(itemStack)
                            }
                        }

                        SSSSVConfig.CollectionModes.TELEPORT_TO_PLAYER -> {
                            val droppedItem = player.drop(itemStack, false)
                            droppedItem?.setPos(player.x, player.y + 0.5, player.z)
                            droppedItem?.deltaMovement?.multiply(Vec3.ZERO)
                        }
                    }
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
    fun isInToolList(item: Item): Boolean {
        if (Configs.ssssvConfig.allowAllTools.get()) {
            return true
        }
        return Configs.ssssvConfig.allowedTools.contains(item)
    }
    fun isInWhitelist(block: Block): Boolean {
        val config = Configs.ssssvConfig
        if (!config.blocksWhitelistEnbabled.get())
            return true
        return config.blocksWhitelist.contains(block)
    }
    fun isInBlacklist(block: Block): Boolean {
        val config = Configs.ssssvConfig
        if (!config.blocksBlacklistEnabled)
            return false
        return config.blocksBlacklist.contains(block)
    }
}