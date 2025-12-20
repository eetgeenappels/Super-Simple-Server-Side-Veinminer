package nl.eetgeenappels.ssssv.config

import me.fzzyhmstrs.fzzy_config.api.FileType
import me.fzzyhmstrs.fzzy_config.api.SaveType
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedList
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedRegistryType
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import nl.eetgeenappels.ssssv.veinminer.search.SearchStrategies


class SSSSVConfig: Config(Identifier.parse("ssssv:server_side_config")) {


    var enabled: ValidatedBoolean = ValidatedBoolean(true)
    var maxBlocks: ValidatedInt = ValidatedInt(32, 256, 1)
    var holdShiftToVeinmine: ValidatedBoolean = ValidatedBoolean(true)
    var collectionMode: CollectionModes = CollectionModes.DROP_NORMALLY
    var enableInCreativeMode: ValidatedBoolean = ValidatedBoolean(false)

    enum class CollectionModes {
        DROP_NORMALLY,
        TELEPORT_TO_PLAYER,
        ADD_TO_INVENTORY_OR_DROP;

        companion object {
            fun fromString(name: String): CollectionModes? {
                return entries.find { it.name.equals(name, ignoreCase = true) }
            }
        }
    }

    var durabilityTakenPerBlock: ValidatedFloat = ValidatedFloat(1.0f, 2.0f, 0.0f)
    var hungerTakenPerBlock: ValidatedFloat = ValidatedFloat(0.1f, 1.0f, 0.0f)

    var search: ConfigGroup = ConfigGroup("search", true)

    var blockSearchMode: SearchStrategies = SearchStrategies.BREADTH_FIRST_SEARCH
    @ConfigGroup.Pop
    var allowDiagonalVeinmine: ValidatedBoolean = ValidatedBoolean(true)

    var blocks: ConfigGroup = ConfigGroup("blocks", true)

    var blocksWhitelistEnbabled: ValidatedBoolean = ValidatedBoolean(true)
    var blocksWhitelist: ValidatedList<Block> = ValidatedRegistryType.of(BuiltInRegistries.BLOCK).toList(listOf(
        Blocks.COAL_ORE,
        Blocks.DEEPSLATE_COAL_ORE,
        Blocks.IRON_ORE,
        Blocks.DEEPSLATE_IRON_ORE,
        Blocks.GOLD_ORE,
        Blocks.DEEPSLATE_GOLD_ORE,
        Blocks.DIAMOND_ORE,
        Blocks.DEEPSLATE_DIAMOND_ORE,
        Blocks.EMERALD_ORE,
        Blocks.DEEPSLATE_EMERALD_ORE,
        Blocks.REDSTONE_ORE,
        Blocks.DEEPSLATE_REDSTONE_ORE,
        Blocks.LAPIS_ORE,
        Blocks.DEEPSLATE_LAPIS_ORE,
        Blocks.NETHER_QUARTZ_ORE,
        Blocks.NETHER_GOLD_ORE,
        Blocks.ANCIENT_DEBRIS,
        Blocks.COPPER_ORE,
        Blocks.DEEPSLATE_COPPER_ORE,
        Blocks.GRAVEL,
        Blocks.DIORITE,
        Blocks.ANDESITE,
        Blocks.GRANITE,
        Blocks.TUFF,
        Blocks.OAK_LOG,
        Blocks.SPRUCE_LOG,
        Blocks.BIRCH_LOG,
        Blocks.JUNGLE_LOG,
        Blocks.ACACIA_LOG,
        Blocks.DARK_OAK_LOG,
        Blocks.MANGROVE_LOG,
        Blocks.CRIMSON_STEM,
        Blocks.WARPED_STEM,
        Blocks.CHERRY_LOG
    ))
    var blocksBlacklistEnabled: Boolean = false
    @ConfigGroup.Pop
    var blocksBlacklist: ValidatedList<Block> = ValidatedRegistryType.of(BuiltInRegistries.BLOCK).toList(listOf(
        Blocks.STONE,
        Blocks.DEEPSLATE
    ))

    var tools: ConfigGroup = ConfigGroup("tools", true)

    var allowAllTools: ValidatedBoolean = ValidatedBoolean(false)
    @ConfigGroup.Pop
    var allowedTools: ValidatedList<Item> = ValidatedRegistryType.of(BuiltInRegistries.ITEM).toList(listOf(
        Items.WOODEN_PICKAXE,
        Items.STONE_PICKAXE,
        Items.IRON_PICKAXE,
        Items.GOLDEN_PICKAXE,
        Items.DIAMOND_PICKAXE,
        Items.NETHERITE_PICKAXE,
        Items.WOODEN_AXE,
        Items.STONE_AXE,
        Items.IRON_AXE,
        Items.GOLDEN_AXE,
        Items.DIAMOND_AXE,
        Items.NETHERITE_AXE,
        Items.WOODEN_SHOVEL,
        Items.STONE_SHOVEL,
        Items.IRON_SHOVEL,
        Items.GOLDEN_SHOVEL,
        Items.DIAMOND_SHOVEL,
        Items.NETHERITE_SHOVEL
    ))

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