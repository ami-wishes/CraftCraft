package com.ami.craftcraft.client;

import com.ami.craftcraft.client.actions.pathing.PathManager;
import com.ami.craftcraft.client.rendering.HUDRenderer;
import com.mojang.blaze3d.platform.InputUtil;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.option.Perspective;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CraftCraftMod implements ClientModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("CraftCraft");

	public static KeyBind specialCameraButton;
	public static KeyBind doActionButton;
	public static KeyBind previousModeButton;
	public static KeyBind nextModeButton;


	public static final SpecialCamera SPECIAL_CAMERA = new SpecialCamera();
	public static final PlayerController PLAYER_CONTROLLER = new PlayerController();
	public static final HUDRenderer HUD_RENDERER = new HUDRenderer();
	public static final PathManager PATH_MANAGER = new PathManager();

	@Override
	public void onInitializeClient(ModContainer mod) {
		specialCameraButton = KeyBindingHelper.registerKeyBinding(new KeyBind(
				"com.ami.craftcraft.cambutton",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_MINUS,
				"com.ami.craftcraft.bindings"
		));

		doActionButton = KeyBindingHelper.registerKeyBinding(new KeyBind(
				"com.ami.craftcraft.doAction",
				InputUtil.Type.MOUSE,
				0,
				"com.ami.craftcraft.bindings"
		));

		previousModeButton = KeyBindingHelper.registerKeyBinding(new KeyBind(
				"com.ami.craftcraft.prevmode",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_Z,
				"com.ami.craftcraft.bindings"
		));

		nextModeButton = KeyBindingHelper.registerKeyBinding(new KeyBind(
				"com.ami.craftcraft.nextmode",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_X,
				"com.ami.craftcraft.bindings"
		));

		ClientTickEvents.START.register(client -> {
			SPECIAL_CAMERA.tick(client);
			PATH_MANAGER.tick(client);

			if (SPECIAL_CAMERA.enabled)
				PLAYER_CONTROLLER.tick(client);
		});

		HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
			if (SPECIAL_CAMERA.enabled)
				HUD_RENDERER.render(matrixStack, tickDelta);
		});
	}

	public static void toggleSpecialCamera() {
		var client = MinecraftClient.getInstance();

		//If turning off special camera...
		if (SPECIAL_CAMERA.enabled) {
			SPECIAL_CAMERA.disableCamera();

			client.options.setPerspective(Perspective.FIRST_PERSON);
			client.mouse.lockCursor();

		} else {
			SPECIAL_CAMERA.enableCamera();

			var ent = client.cameraEntity;
			client.mouse.unlockCursor();

			if (ent == null)
				return;

			SPECIAL_CAMERA.position = ent.getPos().add(0, 1, 0);
		}
	}
}
