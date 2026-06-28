package fr.estecka.packrulemenus.gui;

import java.io.IOException;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import fr.estecka.packrulemenus.PackRuleMod;

public class GenericOptionScreen
extends Screen
{
	private final Screen parent;

	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
	private final LinearLayout body = LinearLayout.vertical().spacing(8);
	private final Button footer = Button.builder( CommonComponents.GUI_DONE, b->this.onClose() ).width(200).build();

	public GenericOptionScreen(Component title, Screen parent) {
		super(title);
		this.parent = parent;
		layout.addToFooter(footer);
	}

	public void AddWidget(LayoutElement widget){
		body.addChild(widget);
	}

	@Override
	public void init(){
		layout.addTitleHeader(this.title, this.font);
		layout.addToContents(body);

		layout.visitWidgets(e -> this.addRenderableWidget(e));
		layout.arrangeElements();
	}

	@Override
	public void onClose(){
		this.minecraft.setScreenAndShow(parent);
		try {
			PackRuleMod.CONFIG_IO.Write(PackRuleMod.CONFIG);
		}
		catch (IOException e){
			PackRuleMod.LOGGER.error(e.getMessage());
		}
	}
}
