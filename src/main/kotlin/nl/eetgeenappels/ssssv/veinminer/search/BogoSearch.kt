package nl.eetgeenappels.ssssv.veinminer.search

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import kotlin.math.cbrt
import kotlin.math.ceil
// yeah idk man i am tired
class BogoSearch: SearchStrategy {
    override fun search(
        startPos: BlockPos,
        targetBlock: Block,
        level: Level,
        maxBlocks: Int
    ): List<BlockPos> {

        val range = ceil(cbrt(maxBlocks.toDouble())).toInt()

        val maxTries = maxBlocks * 5
        val found = mutableSetOf<BlockPos>()

        var tries = 0
        while (found.size < maxBlocks && tries < maxTries) {

            val offsetPos = getOffset(range, startPos)
            val state = level.getBlockState(offsetPos)
            if (state.block == targetBlock) {
                found.add(offsetPos)
            }
            tries++
        }
        return found.toList()
    }

    private fun getOffset(range: Int, blockPos: BlockPos): BlockPos {
        val rand = java.util.Random()
        val x = rand.nextInt(-range, range + 1)
        val y = rand.nextInt(-range, range + 1)
        val z = rand.nextInt(-range, range + 1)
        return blockPos.offset(x, y, z)
    }

}