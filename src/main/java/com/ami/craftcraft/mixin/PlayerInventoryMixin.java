package com.ami.craftcraft.mixin;

import com.ami.craftcraft.client.CraftCraftMod;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {


	@Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
	public void scrollInHotbar(double scrollAmount, CallbackInfo ci){
		if(CraftCraftMod.SPECIAL_CAMERA.enabled){
			CraftCraftMod.SPECIAL_CAMERA.onScroll(scrollAmount);
			ci.cancel();
		}
	}
}
