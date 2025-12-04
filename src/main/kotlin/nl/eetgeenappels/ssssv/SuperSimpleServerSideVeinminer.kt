package nl.eetgeenappels.ssssv

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import nl.eetgeenappels.ssssv.config.Configs
import nl.eetgeenappels.ssssv.config.SSSSVConfig
import nl.eetgeenappels.ssssv.veinminer.Veinminer
import org.slf4j.LoggerFactory

object SuperSimpleServerSideVeinminer : ModInitializer {
    private val logger = LoggerFactory.getLogger("super-simple-server-side-veinminer")
	val MOD_ID = "ssssv"

	override fun onInitialize() {
		logger.info("Hello Fabric world!")

		PlayerBlockBreakEvents.AFTER.register(PlayerBlockBreakEvents.After { level, player, blockPos, blockState, blockEntity ->
			Veinminer.onBlockBreak(level, player, blockPos, blockState, blockEntity)
		})



	}
}