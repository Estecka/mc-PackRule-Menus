# Data-Pack / Game-Rule Menus

Takes the titular menus from the World-Creation menu, and makes them available from already created singleplayer worlds.
This provides a more user-friendly alternative to the vanilla `/gamerule` and `/datapack` commands.

Like those commands, the menus will only be accessible in worlds with cheats/commands enabled.

Unlike the vanilla `/datapack` command, the Datapack menu from this mod can be used to toggle experimental features!


## Word of caution: Datapacks

Some types of datapacks require a world restart to fully take effect.
Whenever possible, the mod will show a warning after toggling one of them, and offer you the option to either back-out, or exit the world gracefully.
However, some type of packs cannot be detected, and no warning will be displayed for those.

### Registry Packs
The new type of packs introduced in MC 1.21, those packs which add data to registries (painting variants, etc), **are not detected by the mod**, but still require a world restart to fully take effects.

Toggling these packs may cause some errors in the log, but those are benign so long as you restart the world immediately afterward. This behaviour is no different from using the `/datapack` command.

### Experimental Features
Packs that include experimental features (such as bundles) are properly detected by the mod. Toggling them will also toggle the corresponding feature-flag on the world, and exit the world gracefully.

### Vanilla Datapack
The Vanilla datapack can technically be disabled, but you probably don't want to do it.
Doing so will usually break worlds unless you know exactly what you are doing.
An additional warning screen will appear when trying to disable this pack.

If you can't load a world after having disabled the Vanilla datapack, loading it in Safe Mode should be able to restore it.
