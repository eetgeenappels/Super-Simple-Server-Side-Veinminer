# Super Simple Server Side Veinminer
This is a simple server-side veinminer mod for fabric. 
It allows players to mine entire veins of ores by breaking just one block.

## Features
- Server-side only, no client mod required
- Configurable block (white/black)list
- Configurable vein size
- Optionally also client sided for visual effects

## Configuring
To configure the mod, you have a few options:
You can use the /ssssv_config command this does not require you to have the mod installed on the client.

**Examples**
```
/ssssv_config blocks whitelist add minecraft:diamond_ore
/ssssv_config blocks blacklist remove minecraft:stone
/ssssv_config veinmine_max_blocks 100
/ssssv_config search mode BREADTH_FIRST_SEARCH
```

Secondly you can use /configure ssssv or to open the Fzzy Config GUI.
Alternatively you can use modmenu to open the Fzzy Config GUI.

Or you can direcly edit the config file located at `config/ssssv.toml`.

## Dependencies
- Fabric API (https://modrinth.com/mod/fabric-api)
- Fzzy Config (https://modrinth.com/mod/fzzy-config)
- Fabric Language Kotlin (https://modrinth.com/mod/fabric-language-kotlin)