package fr.estecka.packrulemenus.gui;

import java.io.IOException;
import fr.estecka.packrulemenus.PackRuleMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class GenericOptionScreen
extends Screen
{
	private final Screen parent;

	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
	private final DirectionalLayoutWidget body = DirectionalLayoutWidget.vertical().spacing(8);
	private final ButtonWidget footer = ButtonWidget.builder( ScreenTexts.DONE, b->this.close() ).width(200).build();

	public GenericOptionScreen(Text title, Screen parent) {
		super(title);
		this.parent = parent;
		layout.addFooter(footer);
	}

	public void AddWidget(Widget widget){
		body.add(widget);
	}

	@Override
	public void init(){
		layout.addHeader(this.title, this.textRenderer);
		layout.addBody(body);

		layout.forEachChild(e -> this.addDrawableChild(e));
		layout.refreshPositions();
	}

	@Override
	public void close(){
		this.client.setScreen(parent);
		try {
			PackRuleMod.CONFIG_IO.Write(PackRuleMod.CONFIG);
		}
		catch (IOException e){
			PackRuleMod.LOGGER.error(e.getMessage());
		}
	}
}
