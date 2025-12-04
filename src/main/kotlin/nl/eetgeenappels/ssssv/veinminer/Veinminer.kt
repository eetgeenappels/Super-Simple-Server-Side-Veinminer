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

            val neighbors = if (Configs.ssssvConfig.allowDiagonalVeinmine) {
                getDiagonalNeighbours(current)
            } else {
                getDirectNeighbours(current)
            }

            for (neighbor in neighbors) {

                if (!visited.contains(neighbor)) {
                    val neighborState = level.getBlockState(neighbor)
                    val neighborBlock = neighborState.block

                    if (neighborBlock == blockState.block) {
                        if (Configs.ssssvConfig.teleportItemsToPlayer) {
                            val drops = neighborState.getDrops(LootParams.Builder(serverLevel).apply {
                                withParameter(LootContextParams.THIS_ENTITY, player)
                                withParameter(LootContextParams.TOOL, player.mainHandItem)
                                withParameter(LootContextParams.BLOCK_STATE, blockState)
                                withParameter(LootContextParams.ORIGIN, neighbor.center)
                                if (blockEntity != null)
                                    withParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                            })
                            level.destroyBlock(neighbor, false)
                            for (item in drops) {
                                val success = player.inventory.add(item)
                                if (!success) {
                                    player.drop(item, false)
                                }
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

    fun getDirectNeighbours(blockPos: BlockPos): List<BlockPos> {
        val neighbors = mutableListOf<BlockPos>()
        for (direction in Direction.entries) {
            val neighbor = blockPos.relative(direction)
            neighbors.add(neighbor)
        }
        return neighbors
    }

    fun getDiagonalNeighbours(blockPos: BlockPos): List<BlockPos> {
        val neighbors = mutableListOf<BlockPos>()
        for (dx in -1..1) {
            for (dy in -1..1) {
                for (dz in -1..1) {
                    if (dx == 0 && dy == 0 && dz == 0) continue
                    val neighbor = blockPos.offset(dx, dy, dz)
                    neighbors.add(neighbor)
                }
            }
        }
        return neighbors
    }

    fun canVeinmineBlock(block: Block): Boolean {
        return Configs.ssssvConfig.allowedBlocks.contains(block)
    }
}