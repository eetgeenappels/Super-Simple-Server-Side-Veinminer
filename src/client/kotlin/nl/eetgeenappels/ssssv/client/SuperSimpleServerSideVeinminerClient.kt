package nl.eetgeenappels.ssssv.client

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import nl.eetgeenappels.ssssv.client.config.SSSSVRenderConfig
import nl.eetgeenappels.ssssv.client.network.ClientNetoworkHandler
import nl.eetgeenappels.ssssv.client.render.BlockOutlineRenderer
import nl.eetgeenappels.ssssv.config.SSSSVConfig
import nl.eetgeenappels.ssssv.network.PreviewPayload
import org.joml.Matrix4f


object SuperSimpleServerSideVeinminerClient : ClientModInitializer {

	var cubes: MutableList<BlockPos> = mutableListOf(
	)

	override fun onInitializeClient() {

		ConfigApi.network().registerS2C(PreviewPayload.TYPE, PreviewPayload.CODEC, ClientNetoworkHandler::handleMyCustomPayload)

		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ConfigApi.registerAndLoadConfig(::SSSSVConfig, RegisterType.BOTH)
		//ConfigApi.registerAndLoadConfig(::SSSSVRenderConfig, RegisterType.CLIENT)

	}
}