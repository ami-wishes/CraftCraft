package com.ami.craftcraft.mixin;

import com.ami.craftcraft.client.CraftCraftInput;
import com.ami.craftcraft.client.CraftCraftMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {

	@Shadow
	public abstract boolean hasRightClicked();

	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
	public void lockCursor(CallbackInfo ci){
		if(CraftCraftMod.SPECIAL_CAMERA.enabled && !CraftCraftInput.pressedUse)
			ci.cancel();
	}

}
