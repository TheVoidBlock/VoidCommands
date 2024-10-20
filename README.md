# Void Commands <a href="https://github.com/TheVoidBlock/VoidCommands/releases/latest"><img alt="GitHub Release" src="https://img.shields.io/github/v/release/TheVoidBlock/voidcommands?include_prereleases&sort=semver&display_name=tag&style=for-the-badge&logo=github"></a> <a href="https://modrinth.com/mod/voidcommands"><img alt="Modrinth Downloads" src="https://img.shields.io/modrinth/dt/voidcommands?style=for-the-badge&logo=modrinth&label=MODRINTH&color=%231BD96A"></a> <a href="https://www.gnu.org/licenses/gpl-3.0.html"><img alt="GNU GPL3" src="https://www.gnu.org/graphics/gplv3-127x51.png"></a>
All commands are prefixed with ```v```

<details>
<summary>Commands</summary>

| Command                                                                 | Description                                                                                                                |
|-------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------|
| ```vget <itemStack> <count>```                                          | Gives yourself a ghost item. In Creative it will be ghost, until you use it.                                               |
| ```vsetblock <blockPos> <blockState>```                                 | Sets a the block at <blockPos> to the block <blockState>.                                                                  |
| ```vghostplacement <booleanToggle>```                                   | Toggles if interacting with blocks sends a packet. This includes placing blocks, using buttons, etc.                       |
| ```vgetrender```                                                        | Prints simulation/view distance in chat.                                                                                   |
| ```vgetlocation <entity>```                                             | Prints current coordinates, and dimension in chat.                                                                         |
| ```vsummon <entity> <location> <nbt>```                                 | Summons an entity client-side (you can be nudged by client side entities)                                                  |
| ```vkill <entities>```                                                  | Removes entities from the client world                                                                                     |
| ```vquery [blocks\|entities] <blockState\|entitySelector> <distance>``` | Prints the amount of entities/blocks of a type loaded within <distance> chunks. Default distance is your render distance.  |

</details> 

Note: This mod is very new, and I'm adding most features suggested. You can [suggest a feature](https://github.com/TheVoidBlock/VoidCommands/issues/new) through the [GitHub](https://github.com/TheVoidBlock/VoidCommands)!

if you like this mod, I also recommend getting [ClientCommands](https://modrinth.com/mod/client-commands)
