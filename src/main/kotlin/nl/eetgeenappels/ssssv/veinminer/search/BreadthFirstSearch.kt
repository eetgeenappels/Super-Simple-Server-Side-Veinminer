package nl.eetgeenappels.ssssv.veinminer.search

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import nl.eetgeenappels.ssssv.config.Configs
import java.util.LinkedList
import java.util.Queue

class BreadthFirstSearch: SearchStrategy {
    override fun search(
        startPos: BlockPos,
        targetBlock: Block,
        level: Level,
        maxBlocks: Int
    ): List<BlockPos> {
        val found = mutableListOf<BlockPos>()
        val queue: Queue<BlockPos> = LinkedList()
        val visited = HashSet<BlockPos>()

        queue.add(startPos)
        visited.add(startPos)

        while (queue.isNotEmpty() && found.size < maxBlocks) {
            val current = queue.poll()
            val state = level.getBlockState(current)

            if (state.block == targetBlock) {
                found.add(current)

                // use your diagonal toggle if needed
                val neighbors = if (Configs.ssssvConfig.searchSection.allowDiagonalVeinmine) {
                    getDiagonalNeighbours(current)
                } else {
                    getDirectNeighbours(current)
                }

                for (n in neighbors) {
                    if (!visited.contains(n)) {
                        val neighborState = level.getBlockState(n)
                        if (neighborState.block == targetBlock) {
                            queue.add(n)
                        }
                        visited.add(n)
                    }
                }
            }
        }

        return found
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

}