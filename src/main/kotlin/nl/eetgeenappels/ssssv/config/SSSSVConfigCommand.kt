package nl.eetgeenappels.ssssv.config

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import nl.eetgeenappels.ssssv.veinminer.search.SearchStrategies

object SSSSVConfigCommand {

    fun init() {

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                literal("ssssv_config")
                    .requires {it.hasPermission(2) }

                    // Simple boolean toggles
                    .then(booleanCommand("veinmine_enabled") {
                        Configs.ssssvConfig.veinmineEnabled.validateAndSet(it)
                    })

                    .then(booleanCommand("hold_shift_to_veinmine") {
                        Configs.ssssvConfig.holdShiftToVeinmine.validateAndSet(it)
                    })

                    .then(booleanCommand("enable_in_creative_mode") {
                        Configs.ssssvConfig.enableInCreativeMode.validateAndSet(it)
                    })

                    .then(argument("collectionMode", StringArgumentType.string())
                        .executes { ctx ->
                            val name = StringArgumentType.getString(ctx, "collectionMode")
                            val new = SSSSVConfig.CollectionModes.fromString(name)
                            if (new != null) {
                                Configs.ssssvConfig.collectionMode = new
                                ctx.source.sendSystemMessage(Component.literal("Set collection mode to: $name"))
                            } else {
                                ctx.source.sendFailure(Component.literal("Collection mode '$name' not found"))
                            }
                            1
                        }
                    )

                    // Float configs
                    .then(floatCommand("durability_per_block", 0f, 2f) {
                        Configs.ssssvConfig.durabilityTakenPerBlock.validateAndSet(it).isValid()
                    })

                    .then(floatCommand("hunger_per_block", 0f, 1f) {
                        Configs.ssssvConfig.hungerTakenPerBlock.validateAndSet(it).isValid()
                    })

                    // Integer config
                    .then(
                        literal("veinmine_max_blocks")
                            .then(argument("amount", IntegerArgumentType.integer(1, 256))
                                .executes { ctx ->
                                    val v = IntegerArgumentType.getInteger(ctx, "amount")
                                    val ok = Configs.ssssvConfig.veinmineMaxBlocks.validateAndSet(v)
                                    if (ok.isValid()) {
                                        ctx.source.sendSystemMessage(Component.literal("Set veinmine max blocks to: $v"))
                                    } else {
                                        ctx.source.sendFailure(Component.literal("Invalid value: $v. Must be 1 to 256"))
                                    }
                                    1
                                }
                            )
                    )

                    // Search mode
                    .then(
                        literal("search")
                            .then(
                                literal("mode")
                                    .then(argument("mode", StringArgumentType.string())
                                        .executes { ctx ->
                                            val name = StringArgumentType.getString(ctx, "mode")
                                            val new = SearchStrategies.fromString(name)
                                            if (new != null) {
                                                Configs.ssssvConfig.blockSearchMode = new
                                                ctx.source.sendSystemMessage(Component.literal("Set search mode to: $name"))
                                            } else {
                                                ctx.source.sendFailure(Component.literal("Search mode '$name' not found"))
                                            }
                                            1
                                        }
                                    )
                            )
                            .then(
                                literal("list")
                                    .executes { ctx ->
                                        val names = SearchStrategies.entries.joinToString(", ") { it.name.lowercase() }
                                        ctx.source.sendSystemMessage(Component.literal("Available search modes: $names"))
                                        1
                                    }
                            )
                            .then(
                                booleanCommand("allow_diagonal") {
                                    Configs.ssssvConfig.allowDiagonalVeinmine.validateAndSet(it)
                                }
                            )
                    )

                    // Blocks (whitelist & blacklist)
                    .then(
                        literal("blocks")
                            .then(booleanCommand("enable_whitelist") {
                                Configs.ssssvConfig.blocksWhitelistEnbabled.validateAndSet(it)
                            })
                            .then(booleanCommand("enable_blacklist") {
                                Configs.ssssvConfig.blocksBlacklistEnabled = it
                            })
                            .then(blockListCommand("whitelist",
                                { Configs.ssssvConfig.blocksWhitelist.get() },
                                { Configs.ssssvConfig.blocksWhitelist.validateAndSet(it)}
                            ))
                            .then(blockListCommand("blacklist",
                                { Configs.ssssvConfig.blocksBlacklist.get() },
                                { Configs.ssssvConfig.blocksBlacklist.validateAndSet(it) }
                            ))
                    )

                    .then(
                        literal("tools")
                            .then(booleanCommand("allow_all_tools") {
                                Configs.ssssvConfig.allowAllTools.validateAndSet(it)
                            })
                            .then(itemListCommand("tool_list",
                                { Configs.ssssvConfig.allowedTools.get() },
                                { Configs.ssssvConfig.allowedTools.validateAndSet(it) }
                            ))
                    )

            )
        }
    }

    private fun booleanCommand(name: String, setter: (Boolean) -> Unit) =
        literal(name)
            .then(argument("value", StringArgumentType.string())
                .executes { ctx ->
                    val raw = StringArgumentType.getString(ctx, "value")
                    val enabled = raw.equals("true", ignoreCase = true)
                    setter(enabled)
                    ctx.source.sendSystemMessage(Component.literal("Set $name to: $enabled"))
                    1
                }
            )

    private fun floatCommand(name: String, min: Float, max: Float, setter: (Float) -> Boolean) =
        literal(name)
            .then(argument("amount", StringArgumentType.string())
                .executes { ctx ->
                    val raw = StringArgumentType.getString(ctx, "amount")
                    val value = raw.toFloatOrNull()

                    if (value == null) {
                        ctx.source.sendFailure(Component.literal("Invalid number: $raw"))
                        return@executes 1
                    }

                    if (value !in min..max) {
                        ctx.source.sendFailure(Component.literal("Invalid value: $value. Must be $min to $max"))
                        return@executes 1
                    }

                    if (setter(value)) {
                        ctx.source.sendSystemMessage(Component.literal("Set $name to: $value"))
                    } else {
                        ctx.source.sendFailure(Component.literal("Value rejected by config"))
                    }

                    1
                }
            )

    private fun blockListCommand(
        name: String,
        listGetter: () -> List<Block>,
        listSetter: (List<Block>) -> Unit
    ) = literal(name)
        .then(
            literal("add")
                .then(argument("block", StringArgumentType.string())
                    .executes { ctx ->
                        val block = resolveBlock(ctx) ?: return@executes 1
                        val list = listGetter().toMutableList()

                        if (block in list) {
                            ctx.source.sendFailure(Component.literal("Block already in $name"))
                        } else {
                            list.add(block)
                            listSetter(list)
                            ctx.source.sendSystemMessage(Component.literal("Added block: ${StringArgumentType.getString(ctx, "block")}"))
                        }
                        1
                    }
                )
        )
        .then(
            literal("remove")
                .then(argument("block", StringArgumentType.string())
                    .executes { ctx ->
                        val block = resolveBlock(ctx) ?: return@executes 1
                        val list = listGetter().toMutableList()

                        if (block in list) {
                            list.remove(block)
                            listSetter(list)
                            ctx.source.sendSystemMessage(Component.literal("Removed block"))
                        } else {
                            ctx.source.sendFailure(Component.literal("Block not found in $name"))
                        }
                        1
                    }
                )
        )
        .then(
            literal("list")
                .executes { ctx ->
                    val list = listGetter()
                    val msg = if (list.isEmpty()) {
                        "List is empty"
                    } else {
                        list.joinToString(", ") {
                            BuiltInRegistries.BLOCK.getKey(it).toString()
                        }
                    }
                    ctx.source.sendSystemMessage(Component.literal(msg))
                    1
                }
        )

    private fun itemListCommand(
        name: String,
        listGetter: () -> List<Item>,
        listSetter: (List<Item>) -> Unit
    ) = literal(name)
        .then(
            literal("add")
                .then(argument("item", StringArgumentType.string())
                    .executes { ctx ->
                        val item = resolveItem(ctx) ?: return@executes 1
                        val list = listGetter().toMutableList()

                        if (item in list) {
                            ctx.source.sendFailure(Component.literal("Item already in $name"))
                        } else {
                            list.add(item)
                            listSetter(list)
                            ctx.source.sendSystemMessage(Component.literal("Added item: ${StringArgumentType.getString(ctx, "item")}"))
                        }
                        1
                    }
                )
        )
        .then(
            literal("remove")
                .then(argument("block", StringArgumentType.string())
                    .executes { ctx ->
                        val block = resolveItem(ctx) ?: return@executes 1
                        val list = listGetter().toMutableList()

                        if (block in list) {
                            list.remove(block)
                            listSetter(list)
                            ctx.source.sendSystemMessage(Component.literal("Removed block"))
                        } else {
                            ctx.source.sendFailure(Component.literal("Block not found in $name"))
                        }
                        1
                    }
                )
        )
        .then(
            literal("list")
                .executes { ctx ->
                    val list = listGetter()
                    val msg = if (list.isEmpty()) {
                        "List is empty"
                    } else {
                        list.joinToString(", ") {
                            BuiltInRegistries.ITEM.getKey(it).toString()
                        }
                    }
                    ctx.source.sendSystemMessage(Component.literal(msg))
                    1
                }
        )

    private fun resolveBlock(
        ctx: CommandContext<CommandSourceStack>
    ): Block? {
        val name = StringArgumentType.getString(ctx, "block")
        val block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(name))

        if (block.isEmpty) {
            ctx.source.sendFailure(Component.literal("Block '$name' not found"))
            return null
        }

        return block.get().value()
    }
    private fun resolveItem(
        ctx: CommandContext<CommandSourceStack>
    ): Item? {
        val name = StringArgumentType.getString(ctx, "item")
        val item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(name))

        if (item.isEmpty) {
            ctx.source.sendFailure(Component.literal("Item '$name' not found"))
            return null
        }

        return item.get().value()
    }

}