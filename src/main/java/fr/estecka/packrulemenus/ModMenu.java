package fr.estecka.packrulemenus;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.text.Text;
import fr.estecka.packrulemenus.config.EButtonLocation;
import fr.estecka.packrulemenus.gui.GenericOptionScreen;
import static fr.estecka.packrulemenus.PackRuleMod.CONFIG;


public class ModMenu
implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory(){
		return ModMenu::ModMenuScreen;
	}

	static public Screen ModMenuScreen(Screen parent){
		GenericOptionScreen screen = new GenericOptionScreen(Text.translatable("packrulemenus.gui.main.title"), parent);

		ButtonWidget packs = DatapackHandler.CreateButton(parent);
		ButtonWidget rules = GameruleHandler.CreateButton(parent);

		DirectionalLayoutWidget row = DirectionalLayoutWidget.horizontal().spacing(8);
		row.add(rules);
		row.add(packs);

		screen.AddWidget(row);
		screen.AddWidget(CreateCyclingButtonOption());
		screen.AddWidget(CreateConfirmationToggle());

		return screen;
	}

	static private CyclingButtonWidget<EButtonLocation> CreateCyclingButtonOption(){
		var button = CyclingButtonWidget.builder(EButtonLocation::TranslatableName)
			.values(EButtonLocation.values())
			.initially(CONFIG.buttonLocation)
			.tooltip(ModMenu::GetConfigTooltip)
			.build(Text.translatable("packrulemenus.config.buttonlocation"), (widget,value)->{CONFIG.buttonLocation=value;})
			;

		button.setWidth(8 + 2 * button.getWidth());
		return button;
	}

	static CheckboxWidget CreateConfirmationToggle(){
		final var client = MinecraftClient.getInstance();
		var checkbox = CheckboxWidget.builder(Text.translatable("packrulemenus.config.askPackConfirmation"), client.textRenderer)
			// .tooltip(Tooltip.of(Text.translatable("packrulemenus.config.askPackConfirmation.tooltip")))
			.checked(CONFIG.datapackConfirmation)
			.callback((widget,checked)->{CONFIG.datapackConfirmation=checked;})
			.build()
			;

		return checkbox;
	}

	static private Tooltip GetConfigTooltip(EButtonLocation e){
		return Tooltip.of(Text.translatable(e.TranslationKey() + ".tooltip"));
	}
}
