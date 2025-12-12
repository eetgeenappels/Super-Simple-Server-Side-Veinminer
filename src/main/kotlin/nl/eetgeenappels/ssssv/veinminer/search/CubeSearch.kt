package nl.eetgeenappels.ssssv.veinminer.search

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import kotlin.math.cbrt
import kotlin.math.ceil

class CubeSearch: SearchStrategy {
    override fun search(startPos: BlockPos, targetBlock: Block, level: Level, maxBlocks: Int): List<BlockPos> {
        val found = mutableListOf<BlockPos>()
        val range = ceil(cbrt(maxBlocks.toDouble()) / 2).toInt()
        for (x in -range..range) {
            for (y in -range..range) {
                for (z in -range..range) {
                    val currentPos = startPos.offset(x, y, z)
                    val state = level.getBlockState(currentPos)
                    if (state.block == targetBlock) {
                        found.add(currentPos)
                        if (found.size >= maxBlocks) {
                            return found
                        }
                    }
                }
            }
        }
        return found
    }
}