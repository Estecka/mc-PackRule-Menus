package fr.estecka.packrulemenus.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class GenericWarningScreen
extends WarningScreen
{
	private Button proceedButton, cancelButton;
	private final boolean isCheckRequired;
	private final BooleanConsumer onConfirm;
	private final Runnable onCancel;

	public GenericWarningScreen(Component header, Component message, Component checkMessage, boolean isCheckRequired, BooleanConsumer onConfirm, Runnable onCancel){
		super(header, message, checkMessage, message);
		this.isCheckRequired = isCheckRequired;
		this.onConfirm = onConfirm;
		this.onCancel = onCancel;
	}

	@Override
	protected Layout addFooterButtons(){
		LinearLayout layout = LinearLayout.horizontal().spacing(8);
		this.proceedButton = Button.builder(CommonComponents.GUI_PROCEED, this::OnAccept).build();
		this.cancelButton  = Button.builder(CommonComponents.GUI_CANCEL,  this::OnCancel).build();
		layout.addChild(proceedButton);
		layout.addChild(cancelButton);
		return layout;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
		this.proceedButton.active = this.stopShowing.selected() || !isCheckRequired;
		super.extractRenderState(context, mouseX, mouseY, delta);
	}

	private void	OnAccept(Button __){
		if (stopShowing.selected())
			this.onConfirm.accept(true);
		else if (!isCheckRequired)
			this.onConfirm.accept(false);
	}

	private void	OnCancel(Button __){
		this.onCancel.run();
	}

	@Override
	public void	onClose(){
		this.onCancel.run();
	}
}
