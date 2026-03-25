package fr.estecka.packrulemenus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;
import fr.estecka.packrulemenus.DatapackHandler;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.WorldOptionsScreen;


@Unique
@Mixin(WorldOptionsScreen.class)
public class OptionScreenMixin
extends Screen
{
	private OptionScreenMixin(){ super(null); }

	@Inject(
		method = "init",
		at = @At(
			value = "INVOKE",
			ordinal = 2,
			shift = Shift.AFTER,
			target = "net/minecraft/client/gui/layouts/GridLayout$RowHelper.addChild (Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;"
		)
	)
	private void gameruleMenu$Init(CallbackInfo info, @Local GridLayout.RowHelper body){
		body.addChild(DatapackHandler.CreateButton(this));
	}

}
