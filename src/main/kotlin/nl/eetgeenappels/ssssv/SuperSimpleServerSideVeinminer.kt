package nl.eetgeenappels.ssssv

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import nl.eetgeenappels.ssssv.config.SSSSVConfigCommand
import nl.eetgeenappels.ssssv.veinminer.VeinminePreview
import nl.eetgeenappels.ssssv.veinminer.Veinminer
import org.slf4j.LoggerFactory

object SuperSimpleServerSideVeinminer : ModInitializer {
	val logger = LoggerFactory.getLogger("super-simple-server-side-veinminer")
	val MOD_ID = "ssssv"

	override fun onInitialize() {
		logger.info("Hello Fabric world!")

		PlayerBlockBreakEvents.AFTER.register(PlayerBlockBreakEvents.After { level, player, blockPos, blockState, blockEntity ->
			Veinminer.onBlockBreak(level, player, blockPos, blockState, blockEntity)
		})

		ServerTickEvents.END_SERVER_TICK.register { server ->
			for (player in server.playerList.players) {
				VeinminePreview.onPlayerTick(player)
			}
		}


        SSSSVConfigCommand.init()
	}
}