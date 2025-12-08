package nl.eetgeenappels.ssssv.veinminer

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import nl.eetgeenappels.ssssv.SuperSimpleServerSideVeinminer
import nl.eetgeenappels.ssssv.config.Configs
import nl.eetgeenappels.ssssv.config.SSSSVConfig
import nl.eetgeenappels.ssssv.network.NetworkHandler
import nl.eetgeenappels.ssssv.network.PreviewPayload
import org.apache.logging.log4j.core.jmx.Server

object VeinminePreview {

    fun onPlayerTick(player: Player) {

        // check if is not client side
        if (player.level().isClientSide) {
            return
        }

        var blocks = emptyList<BlockPos>()

        val targetedBlock = getTargetedBlock(player, 5.0)
        if (targetedBlock != null) {
            if (Veinminer.canVeinmineBlock(player.level().getBlockState(targetedBlock.blockPos).block ?: Blocks.AIR)) {
                val blockPos = targetedBlock.blockPos
                // Here you can add code to highlight the block at blockPos
                // For example, send a packet to the client to render a highlight
                val searchStrategy = Configs.ssssvConfig.searchSection.blockSearchMode.strategy
                blocks = searchStrategy.search(
                    blockPos,
                    player.level().getBlockState(blockPos).block,
                    player.level(),
                    Configs.ssssvConfig.veinmineMaxBlocks.get()
                )

            }
        }
        // send the blocks to the client
        NetworkHandler.sendPreviewBlocks(player as ServerPlayer, PreviewPayload(blocks))
    }

    fun getTargetedBlock(player: Player, maxDistance: Double = 5.0): BlockHitResult? {
        val level = player.level()

        // Get the player's eye position and look vector
        val eyePos = player.getEyePosition(1.0f)
        val lookVec = player.lookAngle

        // Calculate the end position based on max distance
        val endPos = eyePos.add(
            lookVec.x * maxDistance,
            lookVec.y * maxDistance,
            lookVec.z * maxDistance
        )

        // Perform the raycast
        val clipContext = ClipContext(
            eyePos,
            endPos,
            ClipContext.Block.OUTLINE,
            ClipContext.Fluid.NONE,
            player
        )

        val hitResult = level.clip(clipContext)

        // Check if we hit a block
        return if (hitResult.type == HitResult.Type.BLOCK) {
            hitResult as BlockHitResult
        } else {
            null
        }
    }



}