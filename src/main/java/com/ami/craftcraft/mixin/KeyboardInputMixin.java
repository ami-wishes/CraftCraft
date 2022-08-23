package com.ami.craftcraft.mixin;

import com.ami.craftcraft.client.CraftCraftMod;
import com.ami.craftcraft.client.CraftCraftOutput;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {

	@Inject(method = "tick", at = @At("RETURN"))
	public void tick(boolean slowDown, float f, CallbackInfo ci) {

		if (CraftCraftMod.SPECIAL_CAMERA.enabled) {
			this.pressingForward = false;
			this.pressingBack = false;
			this.pressingLeft = false;
			this.pressingRight = false;
			this.forwardMovement = CraftCraftOutput.forwardMovement;
			this.sidewaysMovement = CraftCraftOutput.sideMovement;
			this.jumping = false;
			this.sneaking = false;
		}
	}
}
