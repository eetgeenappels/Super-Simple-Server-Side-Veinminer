package nl.eetgeenappels.ssssv

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType
import net.fabricmc.api.ClientModInitializer
import nl.eetgeenappels.ssssv.config.SSSSVConfig

object SuperSimpleServerSideVeinminerClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ConfigApi.registerAndLoadConfig(::SSSSVConfig, RegisterType.BOTH)
	}
}