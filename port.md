# Minecraft Code Breaking Changes
## 1.19.4
Current master

## 1.20.0
- `MatrixStack` parameters are replaced with `DrawContext` in most GUI.

## 1.20.5
- The option screen's layout has changed, causing custom buttons to appear in a different place.
- `WarningScreen::initButtons` was replaced with `getLayout`.
### Possible backward compatible workarounds:
- `ResourcePackManager::getEnabledNames` was renamed to `getEnabledIds` (Yarn Mappings changes)
- `MinecraftServer::createDataPackSettings` now takes an extra parameter: The function is simple enough to be reimplemented locally.

# 1.21.2
## No workaround
- Gamerules constructors no require a FeatureSet as parameter.

## 1.21.6
- `World::disconnect` now takes a parameter.
- `MinecraftClient::disconnect` takes new parameters.

## 1.21.9
### Backward compatible workarounds:
- `Checkbox.onPress` now takes an argument. Use `checked` on the builder instead.

## 1.21.11
- `CyclingButtonWidget::builder` now takes the initial value immediately instead of as a sub-call/

## 26.1
- Minecraft now has its own in-world gamerule menu.
- Minecraft now has a dedicated menu for in-world options.

## 26.2
- `MinecraftClient::setScreen` was renamed to `setScreenAndShow`
- `MinecraftClient.gui.getChat()` is now nested in `.gui.hud.getChat()`
