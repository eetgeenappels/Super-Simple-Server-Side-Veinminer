package nl.eetgeenappels.ssssv.client.config

import me.fzzyhmstrs.fzzy_config.api.FileType
import me.fzzyhmstrs.fzzy_config.api.SaveType
import me.fzzyhmstrs.fzzy_config.config.Config
import net.minecraft.resources.ResourceLocation
import java.awt.Color

class SSSSVRenderConfig: Config(ResourceLocation.parse("ssssv:ssssv_render_config")) {

    var renderPreviewColor: Color = Color(0, 255, 0, 100)
    var renderPreviewThroughBlocks: Boolean = true


    override fun fileType(): FileType {
        return FileType.TOML
    }

    override fun saveType(): SaveType {
        return SaveType.SEPARATE
    }
}