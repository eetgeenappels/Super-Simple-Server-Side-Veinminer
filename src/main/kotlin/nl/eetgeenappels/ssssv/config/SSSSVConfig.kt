package nl.eetgeenappels.ssssv.config

import me.fzzyhmstrs.fzzy_config.api.FileType
import me.fzzyhmstrs.fzzy_config.api.SaveType
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block
import nl.eetgeenappels.ssssv.veinminer.search.SearchStrategies

class SSSSVConfig: Config(Identifier.parse("ssssv:ssssv_skibidi")) {


    var veinmineEnabled: Boolean = true
    var veinmineMaxBlocks: ValidatedInt = ValidatedInt(32, 256, 1)
    var holdShiftToVeinmine: Boolean = true
    var teleportItemsToPlayer: Boolean = true
    var durabilityTakenPerBlock: ValidatedFloat = ValidatedFloat(1.0f, 2.0f, 0.0f)
    var hungerTakenPerBlock: ValidatedFloat = ValidatedFloat(0.1f, 1.0f, 0.0f)

    var searchSection: SearchSection = SearchSection()

    class SearchSection : ConfigSection(){
        var blockSearchMode: SearchStrategies = SearchStrategies.BREADTH_FIRST_SEARCH
        var allowDiagonalVeinmine: Boolean = true
    }

    var blocksSection = BlocksSection()

    class BlocksSection: ConfigSection() {

        var blocksWhitelistEnbabled: Boolean = false
        var blocksWhitelist: List<Block> = listOf()
        var blocksBlacklistEnabled: Boolean = true
        var blocsBlacklist: List<Block> = listOf()
    }


    override fun defaultPermLevel(): Int {
        return 1
    }

    override fun fileType(): FileType {
        return FileType.TOML
    }

    override fun saveType(): SaveType {
        return SaveType.SEPARATE
    }
}