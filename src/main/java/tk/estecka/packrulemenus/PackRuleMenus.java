package tk.estecka.packrulemenus;

import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.world.GameRules;
import tk.estecka.clothgamerules.api.ClothGamerulesScreenFactory;

public class PackRuleMenus
{
	static public final Logger LOGGER = LoggerFactory.getLogger("Pack-Rule Menus");

	static public Screen CreateGameruleScreen(Screen parent, GameRules rules, Consumer<Optional<GameRules>> saveConsumer){
		if (FabricLoader.getInstance().isModLoaded("cloth-gamerules"))
			return ClothGamerulesScreenFactory.CreateScreen(parent, rules, saveConsumer);
		else
			return new EditGameRulesScreen(rules, saveConsumer.andThen( __->MinecraftClient.getInstance().setScreen(parent) ));
	}
}
