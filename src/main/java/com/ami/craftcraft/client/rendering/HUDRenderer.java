package com.ami.craftcraft.client.rendering;

import com.ami.craftcraft.client.CraftCraftMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.component.LiteralComponent;

public class HUDRenderer extends DrawableHelper {

	private final MutableText _modeText = MutableText.create(new LiteralComponent("MODE : "));

	public void render(MatrixStack matrixStack, float tickDelta) {
		var currCreator = CraftCraftMod.PLAYER_CONTROLLER.getCurrentCreator();
		var textRenderer = MinecraftClient.getInstance().textRenderer;

		drawTextWithShadow(matrixStack, textRenderer, _modeText.append(currCreator.nameText), 1, 1, 0xFFFFFFFF);
	}
}
