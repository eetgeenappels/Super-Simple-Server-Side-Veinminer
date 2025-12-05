package nl.eetgeenappels.ssssv.veinminer.search

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

interface SearchStrategy {

    fun search(startPos: BlockPos, targetBlock: Block, level: Level, maxBlocks: Int): List<BlockPos>
}