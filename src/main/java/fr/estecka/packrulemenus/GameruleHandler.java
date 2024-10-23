package fr.estecka.packrulemenus;

import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import tk.estecka.clothgamerules.api.ClothGamerulesScreenFactory;

public class GameruleHandler
{
	private final Screen parent;
	private final IntegratedServer server;
	private final MinecraftClient client = MinecraftClient.getInstance();

	public GameruleHandler(Screen parent, IntegratedServer server){
		this.parent = parent;
		this.server = server;
	}

	public ButtonWidget CreateButton() {
		final GameRules worldRules = server.getOverworld().getGameRules();
		final FeatureSet features = server.getOverworld().getEnabledFeatures();

		return ButtonWidget.builder(
				Text.translatable("selectWorld.gameRules"),
				__ -> client.setScreen( CreateGameruleScreen(parent, worldRules.copy(features), optRules -> optRules.ifPresent(r -> worldRules.setAllValues(r, server))) )
			)
			.build()
			;
	}


	static public Screen CreateGameruleScreen(Screen parent, GameRules rules, Consumer<Optional<GameRules>> saveConsumer){
		if (FabricLoader.getInstance().isModLoaded("cloth-gamerules"))
			return ClothGamerulesScreenFactory.CreateScreen(parent, rules, saveConsumer);
		else
			return new EditGameRulesScreen(rules, saveConsumer.andThen( __->MinecraftClient.getInstance().setScreen(parent) ));
	}
}
