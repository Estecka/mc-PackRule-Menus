package fr.estecka.packrulemenus;

import java.io.IOException;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import fr.estecka.packrulemenus.config.ConfigLoader;
import fr.estecka.packrulemenus.config.EButtonLocation;
import fr.estecka.packrulemenus.gui.GenericOptionScreen;


public class ModMenu
implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory(){
		return ModMenu::ModMenuScreen;
	}

	static public Screen ModMenuScreen(Screen parent){
		final MinecraftClient client = MinecraftClient.getInstance();
		final IntegratedServer server = client.getServer();

		GenericOptionScreen screen = new GenericOptionScreen(Text.translatable("packrulemenus.gui.main.title"), parent);

		boolean showWorldOptions = PackRuleMod.CanModifyWorld();
		ButtonWidget packs, rules;
		if (showWorldOptions){
			packs = DatapackHandler.CreateButton(screen, server);
			rules = GameruleHandler.CreateButton(screen, server);
		}
		else {
			packs = ButtonWidget.builder( Text.translatable("selectWorld.dataPacks"), __->{} ).build();
			rules = ButtonWidget.builder( Text.translatable("selectWorld.gameRules"), __->{} ).build();
			packs.active = false;
			rules.active = false;
		}

		DirectionalLayoutWidget row = DirectionalLayoutWidget.horizontal().spacing(8);
		row.add(rules);
		row.add(packs);

		screen.AddWidget(CreateConfigButton());
		screen.AddWidget(row);

		return screen;
	}

	static private CyclingButtonWidget<EButtonLocation> CreateConfigButton(){
		var button = CyclingButtonWidget.builder(EButtonLocation::TranslatableName)
			.values(EButtonLocation.values())
			.initially(PackRuleMod.BUTTON_LOCATION)
			.tooltip(ModMenu::GetConfigTooltip)
			.build(Text.translatable("packrulemenus.config.buttonlocation"), ModMenu::OnButtonChanged)
			;

		button.setWidth(8 + 2 * button.getWidth());
		return button;
	}

	static private Tooltip GetConfigTooltip(EButtonLocation e){
		return Tooltip.of(Text.translatable(e.TranslationKey() + ".tooltip"));
	}

	static private void OnButtonChanged(CyclingButtonWidget<?> button, EButtonLocation value){
		PackRuleMod.BUTTON_LOCATION = value;
		try {
			PackRuleMod.CONFIG_IO.Write(new ConfigLoader());
		}
		catch (IOException e){
			PackRuleMod.LOGGER.error(e.getMessage());
		}
	}
}
