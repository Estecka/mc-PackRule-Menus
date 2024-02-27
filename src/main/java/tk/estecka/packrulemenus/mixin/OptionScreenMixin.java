package tk.estecka.packrulemenus.mixin;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.GameRules;
import tk.estecka.clothgamerules.api.ClothGamerulesScreenFactory;
import tk.estecka.packrulemenus.GenericWarningScreen;
import tk.estecka.packrulemenus.PackRuleMenus;

@Unique
@Mixin(OptionsScreen.class)
abstract public class OptionScreenMixin 
extends Screen
{
	private OptionScreenMixin(){ super(null); }

	@Shadow abstract ButtonWidget	createButton(Text message, Supplier<Screen> screenSupplier);

	private IntegratedServer server;

	@Inject( method="init", at=@At(value="INVOKE", ordinal=1, shift=Shift.AFTER, target="net/minecraft/client/gui/widget/GridWidget$Adder.add (Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;") )
	private void gameruleMenu$Init(CallbackInfo info, @Local GridWidget.Adder adder){
		this.server = this.client.getServer();

		if (client.isIntegratedServerRunning() 
		&& server.getSaveProperties().areCommandsAllowed()
		&& server.getOverworld() != null)
		{
			final GameRules worldRules = server.getOverworld().getGameRules();
			adder.add(createButton(
				Text.translatable("selectWorld.gameRules"),
				() -> CreateGameruleScreen(
					worldRules.copy(),
					optRules -> optRules.ifPresent(r -> worldRules.setAllValues(r, server))
				)
			));

			var rollback = server.getDataPackManager().getEnabledNames();
			adder.add(createButton(
				Text.translatable("selectWorld.dataPacks"),
				() -> new PackScreen(
					server.getDataPackManager(),
					manager -> { HandleDatapackRefresh(manager, rollback); },
					server.getSavePath(WorldSavePath.DATAPACKS),
					Text.translatable("dataPack.title")
				)
			));
		}
	}
	
	private Screen CreateGameruleScreen(GameRules rules, Consumer<Optional<GameRules>> saveConsumer){
		if (FabricLoader.getInstance().isModLoaded("cloth-gamerules"))
			return ClothGamerulesScreenFactory.CreateScreen(this, rules, saveConsumer);
		else
			return new EditGameRulesScreen(rules, saveConsumer.andThen( __->RevertScreen() ));
	}

	private void	RevertScreen(){
		client.setScreen((OptionsScreen)(Object)this);
	};

	private void	HandleDatapackRefresh(final ResourcePackManager manager, Collection<String> rollback){
		FeatureSet neoFeatures = manager.getRequestedFeatures();
		FeatureSet oldFeatures = server.getSaveProperties().getEnabledFeatures();

		if (neoFeatures.equals(oldFeatures)) {
			ReloadPacks(manager);
			RevertScreen();
		}
		else {
			boolean isExperimental = FeatureFlags.isNotVanilla(neoFeatures);
			boolean wasVanillaRemoved = oldFeatures.contains(FeatureFlags.VANILLA) && !neoFeatures.contains(FeatureFlags.VANILLA);
			BooleanConsumer onConfirm = confirmed -> {
				if (confirmed){
					this.ApplyFlags(manager);
					this.server.stop(false);
					if (this.client.world != null)
						this.client.world.disconnect();
					this.client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
					this.client.setScreen(new TitleScreen());
				} else {
					manager.setEnabledProfiles(rollback);
					RevertScreen();
				}
			};

			client.setScreen(GenericWarningScreen.FeatureWarning(isExperimental, confirmed -> {
				if (!wasVanillaRemoved || !confirmed)
					onConfirm.accept(confirmed);
				else
					client.setScreen(GenericWarningScreen.VanillaWarning(onConfirm));
			}));
		}
	}

	private void	ApplyFlags(final ResourcePackManager manager){
		FeatureSet features = manager.getRequestedFeatures();

		String featureNames = "";
		for (Identifier id : FeatureFlags.FEATURE_MANAGER.toId(features))
			featureNames += id.toString()+", ";
		PackRuleMenus.LOGGER.info("Reloading packs with features: {}", featureNames);

		server.getSaveProperties().updateLevelInfo(new DataConfiguration(IMinecraftServerMixin.callCreateDataPackSettings(manager), features));		
	}


	private void	ReloadPacks(final ResourcePackManager manager){
		client.inGameHud.getChatHud().addMessage(Text.translatable("commands.reload.success"));

		server.reloadResources(manager.getEnabledNames()).exceptionally(e -> {
			PackRuleMenus.LOGGER.error("{}", e);
			client.inGameHud.getChatHud().addMessage(Text.translatable("commands.reload.failure").formatted(Formatting.RED));
			return null;
		});
	}

}
