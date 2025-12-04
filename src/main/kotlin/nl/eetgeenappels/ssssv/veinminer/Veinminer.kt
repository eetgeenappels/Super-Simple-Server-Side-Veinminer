package nl.eetgeenappels.ssssv.veinminer

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import nl.eetgeenappels.ssssv.config.Configs
import java.util.*

object Veinminer {

    fun onBlockBreak(level: Level, player: Player, blockPos: BlockPos, blockState: BlockState, blockEntity: BlockEntity?) {
        val config = Configs.ssssvConfig
        if (!Configs.ssssvConfig.veinmineEnabled)
            return
        if (level.isClientSide)
            return
        val serverLevel = level as ServerLevel
        if (!canVeinmineBlock(blockState.block))
            return
        if (Configs.ssssvConfig.holdShiftToVeinmine && !player.isShiftKeyDown)
            return

        val queue: Queue<BlockPos> = LinkedList()
        val visited: MutableSet<BlockPos> = HashSet<BlockPos>()
        var blocksMined = 0

        queue.add(blockPos)

        // Breadth-first loop to remove connected tree blocks
        while (!queue.isEmpty() && blocksMined < Configs.ssssvConfig.veinmineMaxBlocks.get()) {
            val current: BlockPos = queue.poll()

            for (direction in Direction.entries) {
                val neighbor = current.relative(direction)

                if (!visited.contains(neighbor)) {
                    val neighborState = level.getBlockState(neighbor)
                    val neighborBlock = neighborState.block

                    if (neighborBlock == blockState.block) {
                        if (Configs.ssssvConfig.teleportItemsToPlayer) {
                            val drops = neighborState.getDrops(LootParams.Builder(serverLevel).apply {
                                withParameter(LootContextParams.THIS_ENTITY, player)
                                withParameter(LootContextParams.TOOL, player.mainHandItem)
                                withParameter(LootContextParams.BLOCK_STATE, blockState)
                                if (blockEntity != null)
                                    withParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                            })
                            level.destroyBlock(neighbor, false)
                            for (item in drops) {
                                player.inventory.add(item)
                            }
                        } else {
                            level.destroyBlock(neighbor, true)
                        }
                        blocksMined++
                        queue.add(neighbor)
                        visited.add(neighbor)
                    }
                }
            }
        }

    }

    fun canVeinmineBlock(block: Block): Boolean {
        return Configs.ssssvConfig.allowedBlocks.contains(block)
    }
}