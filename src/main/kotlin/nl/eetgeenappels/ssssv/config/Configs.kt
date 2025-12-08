package nl.eetgeenappels.ssssv.config

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType

object Configs {

    val ssssvConfig = ConfigApi.registerAndLoadConfig(::SSSSVConfig, RegisterType.BOTH)
}