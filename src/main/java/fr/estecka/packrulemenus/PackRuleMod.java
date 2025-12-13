package fr.estecka.packrulemenus;

import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import fr.estecka.packrulemenus.config.ConfigIO;
import fr.estecka.packrulemenus.config.Config;


public class PackRuleMod
{
	static public final String MODID = "packrule-menus";
	static public final Logger LOGGER = LoggerFactory.getLogger(MODID);

	static public final ConfigIO CONFIG_IO = new ConfigIO(MODID+".properties");
	static public final Config CONFIG = new Config();

	static
	{
		try {
			CONFIG_IO.GetIfExists(CONFIG);
		}
		catch (IOException e){
			LOGGER.error(e.getMessage());
		}
	}

	static public boolean CanModifyWorld(){
		final MinecraftClient client = MinecraftClient.getInstance();
		final IntegratedServer server = client.getServer();

		return client.isIntegratedServerRunning()
		    && server.getSaveProperties().areCommandsAllowed()
		    && server.getOverworld() != null
		    ;
	}

	static public Optional<ButtonWidget> DisabledButton(String buttonText){
		final MinecraftClient client = MinecraftClient.getInstance();
		final IntegratedServer server = client.getServer();
		String tooltip = null;

		if (!client.isIntegratedServerRunning() || server.getOverworld() == null)
			tooltip = "packrulemenus.gui.disabled.noworld";
		else if (!server.getSaveProperties().areCommandsAllowed())
			tooltip = "packrulemenus.gui.disabled.cheatsdisabled";

		if (tooltip == null)
			return Optional.empty();
		else {
			var button = ButtonWidget.builder( Text.translatable(buttonText), __->{} )
				.tooltip(Tooltip.of(Text.translatable(tooltip)))
				.build();
			button.active = false;
			return Optional.of(button);
		}
	}
}
