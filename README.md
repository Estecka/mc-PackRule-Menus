# Data-Pack / Game-Rule Menus

Takes the titular menus from the World-Creation menu, and makes them available from already created singleplayer worlds.
This provides a more user-friendly alternative to the vanilla `/gamerule` and `/datapack` commands.

Like those commands, the menus will only be accessible in worlds with cheats/commands enabled.

Unlike the vanilla `/datapack` command, the Datapack menu from this mod can be used to toggle experimental features!


## About Datapacks

**Experimental features** require a world restart in order to fully take effect. The mod will detect whenever you try to toggle one, and display a warning before exiting the world gracefully.

Since MC 1.21, some regular datapack can add data to registries (painting variants, etc), which also requires a world restart. However, **those packs are not detected by the mod**, so a restart cannot be enforced for those.
A confirmation screen will still be displayed for any and all pack change, giving you the option to exit the world instead of hot-reloading.

Toggling a registry pack without restarting may print some errors in the log, but those are usually benign. This behaviour is no different from using the `/datapack` command.

### Vanilla Datapack
The Vanilla datapack can technically be disabled, but doing so will break worlds unless you know exactly what you are doing.
An additional warning screen will appear when trying to disable this pack.
If you can't load a world after having disabled the Vanilla datapack, loading it in Safe Mode should be able to restore it.
