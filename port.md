# Minecraft Code Breaking Changes
### 1.19.4
Current master

### 1.20.0
#### No Workarounds:
- `MatrixStack` parameters are replaced with `DrawContext` in most GUI.

### 1.20.5
#### No Workaround:
- The option screen's layout has changed, causing custom buttons to appear in a different place.
- `WarningScreen::initButtons` was replaced with `getLayout`.
#### Possible Workaround:
- `ResourcePackManager::getEnabledNames` was renamed to `getEnabledIds` (Yarn Mappings changes)
- `MinecraftServer::createDataPackSettings` now takes an extra parameter: The function is simple enough to be reimplemented locally.

### 1.21.6
- `World::disconnect` now takes a parameter.
- `MinecraftClient::disconnect` takes new parameters.
