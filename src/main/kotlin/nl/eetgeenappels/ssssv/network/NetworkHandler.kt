package nl.eetgeenappels.ssssv.network

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.networking.api.NetworkApi
import net.minecraft.server.level.ServerPlayer

object NetworkHandler {

    // example method for sending a payload to be received by the registered handler.
    fun sendPreviewBlocks(player: ServerPlayer, previewPayload: PreviewPayload) {
        if (ConfigApi.network().canSend(PreviewPayload.TYPE.id, player)) {
            ConfigApi.network().send(previewPayload, player)
        }
    }

}