package fr.estecka.packrulemenus;

import java.io.IOException;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		final Minecraft client = Minecraft.getInstance();
		final IntegratedServer server = client.getSingleplayerServer();

		return client.hasSingleplayerServer()
		    && server.getWorldData().isAllowCommands()
		    && server.overworld() != null
		    ;
	}

	static public Optional<Button> DisabledButton(String buttonText){
		final Minecraft client = Minecraft.getInstance();
		final IntegratedServer server = client.getSingleplayerServer();
		String tooltip = null;

		if (!client.hasSingleplayerServer() || server.overworld() == null)
			tooltip = "packrulemenus.gui.disabled.noworld";
		else if (!server.getWorldData().isAllowCommands())
			tooltip = "packrulemenus.gui.disabled.cheatsdisabled";

		if (tooltip == null)
			return Optional.empty();
		else {
			var button = Button.builder( Component.translatable(buttonText), __->{} )
				.tooltip(Tooltip.create(Component.translatable(tooltip)))
				.build();
			button.active = false;
			return Optional.of(button);
		}
	}
}
