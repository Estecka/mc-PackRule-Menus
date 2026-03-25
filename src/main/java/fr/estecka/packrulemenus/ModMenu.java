package fr.estecka.packrulemenus;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import fr.estecka.packrulemenus.config.EButtonLocation;
import fr.estecka.packrulemenus.gui.GenericOptionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import static fr.estecka.packrulemenus.PackRuleMod.CONFIG;


public class ModMenu
implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory(){
		return ModMenu::ModMenuScreen;
	}

	static public Screen ModMenuScreen(Screen parent){
		GenericOptionScreen screen = new GenericOptionScreen(Component.translatable("packrulemenus.gui.main.title"), parent);

		Button packs = DatapackHandler.CreateButton(parent);
		packs.setWidth(8 + 2 * packs.getWidth());

		LinearLayout row = LinearLayout.horizontal().spacing(8);
		row.addChild(packs);

		screen.AddWidget(row);
		// screen.AddWidget(CreateCyclingButtonOption());
		screen.AddWidget(CreateConfirmationToggle());

		return screen;
	}

	static private CycleButton<EButtonLocation> CreateCyclingButtonOption(){
		var button = CycleButton.builder(EButtonLocation::TranslatableName, CONFIG.buttonLocation)
			.withValues(EButtonLocation.values())
			.withTooltip(ModMenu::GetConfigTooltip)
			.create(Component.translatable("packrulemenus.config.buttonlocation"), (widget,value)->{CONFIG.buttonLocation=value;})
			;

		button.setWidth(8 + 2 * button.getWidth());
		return button;
	}

	static Checkbox CreateConfirmationToggle(){
		final var client = Minecraft.getInstance();
		var checkbox = Checkbox.builder(Component.translatable("packrulemenus.config.askPackConfirmation"), client.font)
			// .tooltip(Tooltip.of(Text.translatable("packrulemenus.config.askPackConfirmation.tooltip")))
			.selected(CONFIG.datapackConfirmation)
			.onValueChange((widget,checked)->{CONFIG.datapackConfirmation=checked;})
			.build()
			;

		return checkbox;
	}

	static private Tooltip GetConfigTooltip(EButtonLocation e){
		return Tooltip.create(Component.translatable(e.TranslationKey() + ".tooltip"));
	}
}
