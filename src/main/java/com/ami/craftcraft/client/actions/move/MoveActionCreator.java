package com.ami.craftcraft.client.actions.move;

import com.ami.craftcraft.client.CraftCraftInput;
import com.ami.craftcraft.client.CraftCraftMod;
import com.ami.craftcraft.client.actions.Action;
import com.ami.craftcraft.client.actions.ActionCreator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Queue;
import java.util.function.Consumer;

public class MoveActionCreator extends ActionCreator {

	private boolean wasPressed = false;

	public MoveActionCreator(Consumer<Action> actionQueue) {
		super(actionQueue, "Move");
	}

	@Override
	public void tick() {
		var camera = CraftCraftMod.SPECIAL_CAMERA;
		var mouseTarget = camera.mouseTarget;

		if (!wasPressed && CraftCraftInput.pressedAttack && mouseTarget.getType() != HitResult.Type.MISS) {

			Vec3d targetPos = mouseTarget.getPos().add(0, 0, 0);
			MoveAction moveAction = new MoveAction(MinecraftClient.getInstance().player.getPos(), targetPos);

			queueAction(moveAction);
			CraftCraftMod.SPECIAL_CAMERA.focusOnPosition(targetPos.add(0, 0.5f, 0));
		}

		wasPressed = CraftCraftInput.pressedAttack;
	}
}
