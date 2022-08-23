package com.ami.craftcraft.mixin;

import com.ami.craftcraft.client.IKeybindPresser;
import net.minecraft.client.option.KeyBind;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyBind.class)
public class KeybindMixin implements IKeybindPresser {
	@Shadow
	private int timesPressed;

	@Shadow
	private boolean pressed;

	@Override
	public void press() {
		this.timesPressed++;
	}
}
