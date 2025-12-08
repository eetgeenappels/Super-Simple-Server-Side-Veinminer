package nl.eetgeenappels.ssssv.client.config

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType

object ClientConfigs {
    val ssssvRenderConfig = ConfigApi.registerAndLoadConfig(::SSSSVRenderConfig, RegisterType.CLIENT)
}