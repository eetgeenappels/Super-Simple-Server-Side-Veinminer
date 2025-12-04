package nl.eetgeenappels.ssssv.config

import me.fzzyhmstrs.fzzy_config.api.FileType
import me.fzzyhmstrs.fzzy_config.api.SaveType
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import nl.eetgeenappels.ssssv.SuperSimpleServerSideVeinminer

class SSSSVConfig: Config(ResourceLocation.parse("ssssv:ssssv_skibidi")) {


    var veinmineEnabled: Boolean = true
    var veinmineMaxBlocks: ValidatedInt = ValidatedInt(64, 256, 1)
    var allowedBlocks: List<Block> = listOf()
    var holdShiftToVeinmine: Boolean = false
    var teleportItemsToPlayer: Boolean = false


    override fun defaultPermLevel(): Int {
        return 1
    }

    override fun fileType(): FileType {
        return FileType.JSON5
    }

    override fun saveType(): SaveType {
        return SaveType.SEPARATE
    }
}