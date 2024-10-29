package fr.estecka.packrulemenus;

import java.util.Collection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import fr.estecka.packrulemenus.gui.GenericWarningScreen;
import fr.estecka.packrulemenus.mixin.IMinecraftServerMixin;

public class DatapackHandler
{
	private final Screen parent;
	private final IntegratedServer server;
	private final ResourcePackManager manager;
	private final Collection<String> rollback;
	static private final MinecraftClient client = MinecraftClient.getInstance();

	private DatapackHandler(Screen parent, IntegratedServer server){
		this.parent = parent;
		this.server = server;
		this.manager = server.getDataPackManager();
		this.rollback = manager.getEnabledIds();
	}

	static public ButtonWidget CreateButton(Screen parent, IntegratedServer server){
		return ButtonWidget.builder(
				Text.translatable("selectWorld.dataPacks"),
				__->client.setScreen( new DatapackHandler(parent, server).CreateScreen() )
			).build();
	}

	public PackScreen CreateScreen(){
		return new PackScreen(
			server.getDataPackManager(),
			manager -> { HandleDatapackRefresh(manager, rollback); },
			server.getSavePath(WorldSavePath.DATAPACKS),
			Text.translatable("dataPack.title")
		);
	}

	private void	HandleDatapackRefresh(final ResourcePackManager manager, Collection<String> rollback){
		FeatureSet neoFeatures = manager.getRequestedFeatures();
		FeatureSet oldFeatures = server.getSaveProperties().getEnabledFeatures();
		boolean doSoftConfirm = true;

		if (!neoFeatures.equals(oldFeatures)){
			boolean isExperimental = FeatureFlags.isNotVanilla(neoFeatures);
			boolean wasVanillaRemoved = oldFeatures.contains(FeatureFlags.VANILLA) && !neoFeatures.contains(FeatureFlags.VANILLA);
			ShowFeatureWarning(isExperimental, wasVanillaRemoved);
		}
		else if (doSoftConfirm)
			ShowConfirmationScreen();
		else
			ReloadPacks();
	}


/******************************************************************************/
/* ## Utility                                                                 */
/******************************************************************************/

	private void	ApplyFlags(){
		FeatureSet features = manager.getRequestedFeatures();

		String featureNames = "";
		for (Identifier id : FeatureFlags.FEATURE_MANAGER.toId(features))
			featureNames += id.toString()+", ";
		PackRuleMod.LOGGER.info("Reloading packs with features: {}", featureNames);

		server.getSaveProperties().updateLevelInfo(new DataConfiguration(IMinecraftServerMixin.callCreateDataPackSettings(manager, true), features));		
	}

	private void SaveAndQuit(){
		this.ApplyFlags();
		this.server.stop(false);
		if (client.world != null)
			client.world.disconnect();
		client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
		client.setScreen(new TitleScreen());
	}

	private void	ReloadPacks(){
		client.inGameHud.getChatHud().addMessage(Text.translatable("commands.reload.success"));

		server.reloadResources(manager.getEnabledIds()).exceptionally(e -> {
			PackRuleMod.LOGGER.error("{}", e);
			client.inGameHud.getChatHud().addMessage(Text.translatable("commands.reload.failure").formatted(Formatting.RED));
			return null;
		});
		client.setScreen(parent);
	}

	private void Rollback(){
		this.manager.setEnabledProfiles(rollback);
		client.setScreen(parent);
	}


/******************************************************************************/
/* ## Warning Screens                                                         */
/******************************************************************************/

	public void	ShowConfirmationScreen(){
		client.setScreen(new GenericWarningScreen(
			Text.translatable("packrulemenus.warning.packConfirmation.title"),
			Text.translatable("packrulemenus.warning.packConfirmation.message"),
			Text.translatable("packrulemenus.warning.packConfirmation.checkbox"),
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
		MutableText msg = Text.translatable("packrulemenus.warning.featureflag.message");
		if (isExperimental)
			msg.append("\n\n").append(Text.translatable("selectWorld.experimental.message"));

		client.setScreen(new GenericWarningScreen(
			Text.translatable("packrulemenus.warning.featureflag.title"),
			msg,
			Text.translatable("packrulemenus.warning.featureflag.checkbox"),
			true,
			checked -> { if(wasVanillaRemoved) ShowVanillaWarning(); else if (checked) SaveAndQuit(); },
			this::Rollback
		));
	}

	public void	ShowVanillaWarning(){
		client.setScreen(new GenericWarningScreen(
			Text.translatable("packrulemenus.warning.vanillapack.title"),
			Text.translatable("packrulemenus.warning.vanillapack.message"),
			Text.translatable("packrulemenus.warning.vanillapack.checkbox"),
			true,
			checked -> { if(checked) SaveAndQuit(); },
			this::Rollback
		));
	}
}
