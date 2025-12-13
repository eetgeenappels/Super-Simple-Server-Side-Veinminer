package nl.eetgeenappels.ssssv.client.network

import me.fzzyhmstrs.fzzy_config.networking.api.ClientPlayNetworkContext
import nl.eetgeenappels.ssssv.client.SuperSimpleServerSideVeinminerClient
import nl.eetgeenappels.ssssv.network.PreviewPayload


internal object ClientNetworkHandler {
    //insulating any client code that might be in ClientClassThatNeedsPayload
    fun handleMyCustomPayload(payload: PreviewPayload, context: ClientPlayNetworkContext?) {
        SuperSimpleServerSideVeinminerClient.cubes = payload.blocks.toMutableList()
    }
}