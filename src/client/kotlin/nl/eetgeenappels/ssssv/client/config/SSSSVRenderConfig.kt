package nl.eetgeenappels.ssssv.client.config

import me.fzzyhmstrs.fzzy_config.api.FileType
import me.fzzyhmstrs.fzzy_config.api.SaveType
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedColor
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import net.minecraft.resources.Identifier
import java.awt.Color

class SSSSVRenderConfig: Config(Identifier.parse("ssssv:ssssv_render_config")) {

    var renderPreviewColor: ValidatedColor = ValidatedColor(0, 255, 0, 100)
    var renderPreviewThroughBlocks: Boolean = true
    var renderPreviewLineWidth: ValidatedFloat = ValidatedFloat(0F, 20F, 0F)


    override fun fileType(): FileType {
        return FileType.TOML
    }

    override fun saveType(): SaveType {
        return SaveType.SEPARATE
    }
}