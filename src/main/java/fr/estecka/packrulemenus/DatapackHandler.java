package fr.estecka.packrulemenus;

import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.storage.LevelResource;
import fr.estecka.packrulemenus.gui.GenericWarningScreen;
import fr.estecka.packrulemenus.mixin.IMinecraftServerMixin;

public class DatapackHandler
{
	static public final String BUTTON_TEXT = "selectWorld.dataPacks";

	private final Screen parent;
	private final IntegratedServer server;
	private final PackRepository manager;
	private final Collection<String> rollback;
	static private final Minecraft client = Minecraft.getInstance();

	private DatapackHandler(Screen parent, IntegratedServer server){
		this.parent = parent;
		this.server = server;
		this.manager = server.getPackRepository();
		this.rollback = manager.getSelectedIds();
	}

	static public Button CreateButton(Screen parent){
		return PackRuleMod.DisabledButton(BUTTON_TEXT)
			.orElseGet(()->new DatapackHandler(parent, client.getSingleplayerServer()).CreateButton())
			;
	}

	public Button CreateButton(){
		return Button.builder(
			Component.translatable(BUTTON_TEXT),
			__->client.setScreen( this.CreateScreen() )
		).build();
	}

	public PackSelectionScreen CreateScreen(){
		return new PackSelectionScreen(
			server.getPackRepository(),
			manager -> { HandleDatapackRefresh(manager, rollback); },
			server.getWorldPath(LevelResource.DATAPACK_DIR),
			Component.translatable("dataPack.title")
		);
	}

	private void	HandleDatapackRefresh(final PackRepository manager, Collection<String> rollback){
		FeatureFlagSet neoFeatures = manager.getRequestedFeatureFlags();
		FeatureFlagSet oldFeatures = server.getWorldData().enabledFeatures();

		if (!neoFeatures.equals(oldFeatures)){
			boolean isExperimental = FeatureFlags.isExperimental(neoFeatures);
			boolean wasVanillaRemoved = oldFeatures.contains(FeatureFlags.VANILLA) && !neoFeatures.contains(FeatureFlags.VANILLA);
			ShowFeatureWarning(isExperimental, wasVanillaRemoved);
		}
		else if (PackRuleMod.CONFIG.datapackConfirmation)
			ShowConfirmationScreen();
		else
			ReloadPacks();
	}


/******************************************************************************/
/* ## Utility                                                                 */
/******************************************************************************/

	private void	ApplyFlags(){
		FeatureFlagSet features = manager.getRequestedFeatureFlags();

		String featureNames = "";
		for (Identifier id : FeatureFlags.REGISTRY.toNames(features))
			featureNames += id.toString()+", ";
		PackRuleMod.LOGGER.info("Reloading packs with features: {}", featureNames);

		server.getWorldData().setDataConfiguration(new WorldDataConfiguration(IMinecraftServerMixin.callGetSelectedPacks(manager, true), features));		
	}

	private void SaveAndQuit(){
		this.ApplyFlags();
		this.server.halt(false);
		if (client.level != null)
			client.level.disconnect(ClientLevel.DEFAULT_QUIT_MESSAGE);
		client.disconnectWithSavingScreen();
		client.setScreen(new TitleScreen());
	}

	private void	ReloadPacks(){
		client.gui.getChat().addServerSystemMessage(Component.translatable("commands.reload.success"));

		server.reloadResources(manager.getSelectedIds()).exceptionally(e -> {
			PackRuleMod.LOGGER.error("{}", e);
			client.gui.getChat().addServerSystemMessage(Component.translatable("commands.reload.failure").withStyle(ChatFormatting.RED));
			return null;
		});
		client.setScreen(parent);
	}

	private void Rollback(){
		this.manager.setSelected(rollback);
		client.setScreen(parent);
	}


/******************************************************************************/
/* ## Warning Screens                                                         */
/******************************************************************************/

	public void	ShowConfirmationScreen(){
		client.setScreen(new GenericWarningScreen(
			Component.translatable("packrulemenus.warning.packConfirmation.title"),
			Component.translatable("packrulemenus.warning.packConfirmation.message"),
			Component.translatable("packrulemenus.warning.packConfirmation.checkbox"),
			false,
			checked -> { if(checked) SaveAndQuit(); else ReloadPacks(); },
			this::Rollback
		));
	}

	/**
	 * @param isExperimental Whether the currently selected packs include
	 * experimental features. This will be false upon removing all features.
	 */
	public void	ShowFeatureWarning(boolean isExperimental, boolean wasVanillaRemoved){
		MutableComponent msg = Component.translatable("packrulemenus.warning.featureflag.message");
		if (isExperimental)
			msg.append("\n\n").append(Component.translatable("selectWorld.experimental.message"));

		client.setScreen(new GenericWarningScreen(
			Component.translatable("packrulemenus.warning.featureflag.title"),
			msg,
			Component.translatable("packrulemenus.warning.featureflag.checkbox"),
			true,
			checked -> { if(wasVanillaRemoved) ShowVanillaWarning(); else if (checked) SaveAndQuit(); },
			this::Rollback
		));
	}

	public void	ShowVanillaWarning(){
		client.setScreen(new GenericWarningScreen(
			Component.translatable("packrulemenus.warning.vanillapack.title"),
			Component.translatable("packrulemenus.warning.vanillapack.message"),
			Component.translatable("packrulemenus.warning.vanillapack.checkbox"),
			true,
			checked -> { if(checked) SaveAndQuit(); },
			this::Rollback
		));
	}
}
