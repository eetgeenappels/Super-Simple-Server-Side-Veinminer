package nl.eetgeenappels.ssssv.client

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType
import net.fabricmc.api.ClientModInitializer
import net.minecraft.core.BlockPos
import nl.eetgeenappels.ssssv.client.network.ClientNetworkHandler
import nl.eetgeenappels.ssssv.config.SSSSVConfig
import nl.eetgeenappels.ssssv.network.PreviewPayload


object SuperSimpleServerSideVeinminerClient : ClientModInitializer {

	var cubes: MutableList<BlockPos> = mutableListOf(
	)

	override fun onInitializeClient() {

		ConfigApi.network().registerS2C(PreviewPayload.TYPE, PreviewPayload.CODEC, ClientNetworkHandler::handleMyCustomPayload)

		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ConfigApi.registerAndLoadConfig(::SSSSVConfig, RegisterType.BOTH)
		//ConfigApi.registerAndLoadConfig(::SSSSVRenderConfig, RegisterType.CLIENT)

	}
}