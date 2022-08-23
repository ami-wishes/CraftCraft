package com.ami.craftcraft.mixin;

import com.ami.craftcraft.client.CraftCraftInput;
import com.ami.craftcraft.client.CraftCraftMod;
import com.ami.craftcraft.client.IKeybindPresser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBind;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

	@Shadow
	protected abstract void handleBlockBreaking(boolean bl);

	@Shadow
	@Nullable
	public Screen currentScreen;

	@Shadow
	@Nullable
	public ClientPlayerEntity player;

	@Shadow
	@Nullable
	public ClientPlayerInteractionManager interactionManager;

	@Shadow
	protected abstract void doItemUse();

	@Shadow
	protected abstract void doItemPick();

	@Shadow
	protected abstract boolean doAttack();

	@Shadow
	private int itemUseCooldown;

	@Shadow
	@Final
	public GameOptions options;

	@Shadow
	@Final
	public Mouse mouse;

	@Inject(method = "handleInputEvents", at = @At("HEAD"))
	public void handleInputEvents_HEAD(CallbackInfo ci) {
		var client = MinecraftClient.getInstance();

		while (CraftCraftMod.specialCameraButton.wasPressed()) {
			CraftCraftMod.toggleSpecialCamera();
		}

		if (CraftCraftMod.SPECIAL_CAMERA.enabled) {
			KeyBind.updatePressedStates();

			CraftCraftInput.pressedAttack = client.options.attackKey.isPressed();
			CraftCraftInput.pressedUse = client.options.useKey.isPressed();
			CraftCraftInput.pressedPick = client.options.pickItemKey.isPressed();

			CraftCraftInput.pressedForward = client.options.forwardKey.isPressed();
			CraftCraftInput.pressedBack = client.options.backKey.isPressed();
			CraftCraftInput.pressedLeft = client.options.leftKey.isPressed();
			CraftCraftInput.pressedRight = client.options.rightKey.isPressed();

			CraftCraftInput.pressedSneak = client.options.sneakKey.isPressed();
			CraftCraftInput.pressedJump = client.options.jumpKey.isPressed();

			CraftCraftInput.pressedDrop = client.options.dropKey.isPressed();

			CraftCraftInput.pressedAttack = client.options.attackKey.isPressed();
			CraftCraftInput.pressedUse = client.options.useKey.isPressed();
			CraftCraftInput.pressedPick = client.options.pickItemKey.isPressed();

			CraftCraftInput.pressedForward = client.options.forwardKey.isPressed();
			CraftCraftInput.pressedBack = client.options.backKey.isPressed();
			CraftCraftInput.pressedLeft = client.options.leftKey.isPressed();
			CraftCraftInput.pressedRight = client.options.rightKey.isPressed();

			CraftCraftInput.pressedSneak = client.options.sneakKey.isPressed();
			CraftCraftInput.pressedJump = client.options.jumpKey.isPressed();

			CraftCraftInput.pressedDrop = client.options.dropKey.isPressed();

			//Consume input events so vanilla doesn't take them....
			while (client.options.attackKey.wasPressed()) {
			}
			while (client.options.useKey.wasPressed()) {
			}
			while (client.options.pickItemKey.wasPressed()) {
			}
			while (client.options.dropKey.wasPressed()) {
			}

			client.options.useKey.setPressed(false);
			client.options.attackKey.setPressed(false);
			client.options.pickItemKey.setPressed(false);
			client.options.dropKey.setPressed(false);

			while (CraftCraftMod.nextModeButton.wasPressed())
				CraftCraftMod.PLAYER_CONTROLLER.scrollCreator(1);
			while (CraftCraftMod.previousModeButton.wasPressed())
				CraftCraftMod.PLAYER_CONTROLLER.scrollCreator(-1);

			CraftCraftMod.SPECIAL_CAMERA.updateInput();
		} else {
			CraftCraftInput.pressedAttack = false;
			CraftCraftInput.pressedUse = false;
			CraftCraftInput.pressedPick = false;

			CraftCraftInput.pressedForward = false;
			CraftCraftInput.pressedBack = false;
			CraftCraftInput.pressedLeft = false;
			CraftCraftInput.pressedRight = false;

			CraftCraftInput.pressedSneak = false;
			CraftCraftInput.pressedJump = false;
		}
	}

	@Inject(method = "handleInputEvents", at = @At("RETURN"))
	public void handleInputEvents_RETURN(CallbackInfo ci) {
		if (CraftCraftMod.SPECIAL_CAMERA.enabled) {
			var client = MinecraftClient.getInstance();

			client.options.useKey.setPressed(CraftCraftInput.pressedUse);
			client.options.attackKey.setPressed(CraftCraftInput.pressedAttack);
			client.options.pickItemKey.setPressed(CraftCraftInput.pressedPick);
			client.options.dropKey.setPressed(CraftCraftInput.pressedDrop);
		}
	}

}
