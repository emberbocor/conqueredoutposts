# Conquered Outposts

Take a pillager outpost for yourself.

A pillager outpost keeps spawning pillagers forever, even after you have cleared it. Conquered Outposts lets you claim an outpost by planting your own banner in it. While your banner stands, the outpost stops spawning pillagers and the area behaves like the land around it. Take the banner down and the outpost goes back to normal.

The mod runs on the server only. Players join with a plain vanilla client.

Available for NeoForge, Forge and Fabric.

## Features

- **Claim with a banner.** Place any banner, standing or on a wall, in any colour or pattern, inside an outpost. The outpost is claimed at once; pillagers still around do not stop you.
- **No more outpost pillagers.** A claimed outpost no longer spawns pillagers. The normal mobs of the biome still spawn there, as anywhere else.
- **Easy to undo.** When the last claim banner of an outpost is gone, the outpost spawns pillagers again.
- **Saved with the world.** Claims survive server restarts.
- **Messages.** "Outpost claimed" and "Outpost lost" appear above the hotbar of the players inside the outpost. Can be turned off.
- **Configurable.** Choose which structures can be claimed, including outposts from other mods or datapacks.

## How it works

- **Claiming.** A banner placed by a player inside the area of an outpost claims that outpost. The area is the outpost's whole structure area: the watchtower and everything that was generated with it (cages, tents, logs, targets), from its lowest to its highest block.
- **Ominous Banners do not count.** Placing an Ominous Banner (the white banner with the illager pattern) never claims an outpost. Renaming it does not change that.
- **Only player banners count.** The banners that come with the outpost when it is generated never claim it, and neither do banners set with commands such as `/setblock`. Breaking a banner and placing it again yourself does count, unless it is an Ominous Banner.
- **More banners.** You can place more than one claim banner in an outpost. The outpost stays claimed as long as at least one of them is still in place; extra banners do not show the message again.
- **Losing the claim.** A claim banner is gone however it disappears: broken by a player, blown up, dropped because the block it stands or hangs on was removed or pushed away by a piston, or replaced with `/setblock` or `/fill` by anything that is not a banner. When the last one is gone, the outpost is no longer claimed and spawns pillagers again.
- **Outpost farms are safe.** Nothing changes for an outpost nobody has claimed, so pillager farms built at unclaimed outposts keep working.
- **What stays vanilla:** pillager patrols, raids, mob spawners, the pillagers that already live in the outpost, and the spawning rules of the biome.

## Requirements

| | NeoForge | Forge | Fabric |
|---|---|---|---|
| Minecraft | 26.1-26.1.2 | 26.1-26.1.2 | 26.1-26.1.2 |
| Loader | NeoForge 26.1.0.1-beta or newer | Forge 62.0.0 or newer | Fabric Loader 0.18.4 or newer |
| Other | | | Fabric API 0.144.1 or newer |
| Jar | `conqueredoutposts-neoforge-26.1-26.1.2-<version>.jar` | `conqueredoutposts-forge-26.1-26.1.2-<version>.jar` | `conqueredoutposts-fabric-26.1-26.1.2-<version>.jar` |

On Minecraft 26.1-26.1.2, NeoForge and Forge are separate loaders that do not run each other's mods, so each has its own jar. Pick the jar that matches your server's loader.

The Fabric jar bundles [NightConfig](https://github.com/TheElectronWill/night-config) (core and toml, LGPL-3.0) to read the TOML config. NeoForge and Forge already ship it.

### Client and server

Install the mod on the server only, on every loader. It adds no items, blocks, entities, commands, network messages or registries, so the server has nothing a client needs to know about:

- **NeoForge:** vanilla clients and NeoForge clients without the mod can join.
- **Forge:** vanilla clients and Forge clients without the mod can join. The mod is marked as server-only, so a Forge client without it still shows the server as compatible in the server list.
- **Fabric:** vanilla clients and Fabric clients without the mod can join. Fabric API is only needed on the server.

Installing it on a client as well does no harm. Claims and spawning are handled on the server, and the messages are plain text.

In singleplayer, install it in your own game; the built-in server runs it.

## Configuration

### Server: `config/conqueredoutposts.toml`

Created with comments on the first server or world start:

```toml
# Structures that players can claim by placing a banner inside them (any banner except an Ominous Banner).
# While at least one claim banner stays in place, the structure no longer spawns its own mobs
# (pillagers for outposts); the normal mobs of the biome still spawn there.
# Structure ids, for example "minecraft:pillager_outpost". Unknown ids are ignored with a warning.
structures = ["minecraft:pillager_outpost"]

# Show "Outpost claimed" and "Outpost lost" above the hotbar of the players inside the outpost.
showMessages = true
```

- `structures = []` turns claiming off. Existing claims are kept but have no effect until their structure is in the list again.
- Changes take effect after a server restart, or after closing and reopening a singleplayer world.
- Mistakes do not crash the server. An invalid value uses the default for that setting and is logged as a warning; unknown structure ids and unknown options are logged and ignored. If the file cannot be read at all (for example a TOML syntax error), all defaults are used, the file is left untouched and the log says why.

## Removing the mod

The world opens normally and every outpost spawns pillagers again, as in vanilla. The banners stay where they are as normal banners. The mod's claim data stays in the world folder (`dimensions/minecraft/overworld/data/conqueredoutposts/claims.dat`, and the same file for each other dimension); vanilla ignores it, and it does no harm. If you install the mod again, claims whose banners are still in place work again. The config file stays in `config/` until you delete it.

## Compatibility

- **Patrols and raids** are not changed. Pillager patrols still spawn around the world, and raids work as usual.
- **Outposts from other mods or datapacks** can be made claimable by adding their structure id to `structures`, as long as they spawn their mobs through a structure spawn override, like the vanilla outpost does. Structures that spawn their mobs in another way (mob spawners, their own spawning code) are not affected by a claim.
- Mods that replace the world generator with their own spawning code, or that change how structure spawns are chosen, may bypass the claim.
- Protection mods that undo a banner placement right after it happens: the outpost may show "Outpost claimed" and then "Outpost lost" at once; the end result is correct (not claimed).

## Building from source

```
./gradlew build
```

The jars are written to `neoforge/build/libs/`, `forge/build/libs/` and `fabric/build/libs/`.

## License

MIT. The Fabric jar includes NightConfig, which is licensed under the LGPL-3.0.
