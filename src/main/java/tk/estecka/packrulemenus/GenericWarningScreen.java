package tk.estecka.packrulemenus;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class GenericWarningScreen
extends WarningScreen
{
	private ButtonWidget proceedButton, cancelButton;
	private final BooleanConsumer onConfirm;

	public GenericWarningScreen(Text header, Text message, Text checkMessage, BooleanConsumer onConfirm){
		super(header, message, checkMessage, message);
		this.onConfirm = onConfirm;
	}

	static private final Text FEATURE_TITLE   = Text.translatable("packrulemenus.featureflag.warning.title");
	static private final Text FEATURE_MESSAGE = Text.translatable("packrulemenus.featureflag.warning.message");
	static private final Text EXPERIMENTAL_MESSAGE = Text.translatable("selectWorld.experimental.message");
	static private final Text FEATURE_CHECK   = Text.translatable("packrulemenus.featureflag.warning.checkbox");
	static public GenericWarningScreen	FeatureWarning(boolean isExperimental, BooleanConsumer onConfirm){
		Text msg = (isExperimental) ? 
			Text.empty().append(FEATURE_MESSAGE).append("\n\n").append(EXPERIMENTAL_MESSAGE):
			FEATURE_MESSAGE;
		return new GenericWarningScreen(FEATURE_TITLE, msg, FEATURE_CHECK, onConfirm);
	}

	static private final Text VANILLA_TITLE   = Text.translatable("packrulemenus.vanillapack.warning.title");
	static private final Text VANILLA_MESSAGE = Text.translatable("packrulemenus.vanillapack.warning.message");
	static private final Text VANILLA_CHECK   = Text.translatable("packrulemenus.vanillapack.warning.checkbox");
	static public GenericWarningScreen	VanillaWarning(BooleanConsumer onConfirm){
		return new GenericWarningScreen(VANILLA_TITLE, VANILLA_MESSAGE, VANILLA_CHECK, onConfirm);
	}

	@Override
	protected void	initButtons(int yOffset){
		this.proceedButton = ButtonWidget.builder(ScreenTexts.PROCEED, this::OnAccept).dimensions(this.width / 2 - 155      , 100 + yOffset, 150, 20).build();
		this.cancelButton  = ButtonWidget.builder(ScreenTexts.CANCEL,  this::OnCancel).dimensions(this.width / 2 - 155 + 160, 100 + yOffset, 150, 20).build();
		this.addDrawableChild(proceedButton);
		this.addDrawableChild(cancelButton);
	}

	@Override
	public void	render(DrawContext context, int mouseX, int mouseY, float delta){
		this.proceedButton.active = this.checkbox.isChecked();
		super.render(context, mouseX, mouseY, delta);
	}

	private void	OnAccept(ButtonWidget __){
		if (checkbox.isChecked())
			this.onConfirm.accept(true);
	}

	private void	OnCancel(ButtonWidget __){
		this.onConfirm.accept(false);
	}

	@Override
	public void	close(){
		super.close();
		this.onConfirm.accept(false);
	}
}
