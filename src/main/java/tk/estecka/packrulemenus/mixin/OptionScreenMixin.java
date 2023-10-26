package tk.estecka.packrulemenus.mixin;

import java.util.Collection;
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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;
import tk.estecka.packrulemenus.GenericWarningScreen;
import tk.estecka.packrulemenus.PackRuleMenus;

@Mixin(OptionsScreen.class)
abstract public class OptionScreenMixin 
extends Screen
{
	private OptionScreenMixin(){ super(null); throw new AssertionError(); }

	@Shadow abstract ButtonWidget	createButton(Text message, Supplier<Screen> screenSupplier);

	@Inject( method="init", at=@At(value="INVOKE", ordinal=1, shift=Shift.AFTER, target="net/minecraft/client/gui/widget/GridWidget$Adder.add (Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;") )
	private void gameruleMenu$Init(CallbackInfo info, @Local GridWidget.Adder adder){
		final MinecraftServer server = this.client.getServer();
		final World world;

		if (client.isIntegratedServerRunning() && null != (world=client.getServer().getOverworld()))
		{
			adder.add(createButton(
				Text.translatable("selectWorld.gameRules"),
				() -> new EditGameRulesScreen(
					world.getGameRules(),
					optRules -> RevertScreen()
				)
			));

			var rollback = server.getDataPackManager().getEnabledNames();
			adder.add(createButton(
				Text.translatable("selectWorld.dataPacks"),
				() -> new PackScreen(
					server.getDataPackManager(),
					manager -> { RevertScreen(); HandleDatapackRefresh(manager, rollback); },
					server.getSavePath(WorldSavePath.DATAPACKS),
					Text.translatable("dataPack.title")
				)
			));
		}
	}

	@Unique
	private void	RevertScreen(){
		client.setScreen((OptionsScreen)(Object)this);
	};

	@Unique
	private void	HandleDatapackRefresh(final ResourcePackManager manager, Collection<String> rollback){
		final MinecraftServer server = this.client.getServer();
		FeatureSet neoFeatures = manager.getRequestedFeatures();
		FeatureSet oldFeatures = server.getSaveProperties().getEnabledFeatures();

		if (neoFeatures.equals(oldFeatures))
			ReloadPacks(manager, server);
		else {
			boolean isExperimental = FeatureFlags.isNotVanilla(neoFeatures);
			boolean wasVanillaRemoved = oldFeatures.contains(FeatureFlags.VANILLA) && !neoFeatures.contains(FeatureFlags.VANILLA);
			BooleanConsumer onConfirm = confirmed -> {
				if (confirmed){
					this.ApplyFlags(manager, server);
					this.ReloadPacks(manager, server);
				} else {
					manager.setEnabledProfiles(rollback);
				}
				RevertScreen();
			};

			client.setScreen(GenericWarningScreen.FeatureWarning(isExperimental, confirmed -> {
				if (!wasVanillaRemoved || !confirmed)
					onConfirm.accept(confirmed);
				else
					client.setScreen(GenericWarningScreen.VanillaWarning(onConfirm));
			}));
		}
	}

	@Unique
	private void	ApplyFlags(final ResourcePackManager manager, final MinecraftServer server){
		FeatureSet features = manager.getRequestedFeatures();

		String featureNames = "";
		for (Identifier id : FeatureFlags.FEATURE_MANAGER.toId(features))
			featureNames += id.toString()+", ";
		PackRuleMenus.LOGGER.info("Reloading packs with features: {}", featureNames);

		server.getSaveProperties().updateLevelInfo(new DataConfiguration(IMinecraftServerMixin.callCreateDataPackSettings(manager), features));		
	}


	@Unique
	private void	ReloadPacks(final ResourcePackManager manager, final MinecraftServer server){
		if (client.player != null)
			client.inGameHud.getChatHud().addMessage(Text.translatable("commands.reload.success"));

		server.reloadResources(manager.getEnabledNames()).exceptionally(e -> {
			PackRuleMenus.LOGGER.error("{}", e);
			if (client.player != null)
				client.player.getCommandSource().sendError(Text.translatable("commands.reload.failure"));
			return null;
		});
	}

}
