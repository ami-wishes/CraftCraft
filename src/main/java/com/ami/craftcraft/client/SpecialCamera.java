package com.ami.craftcraft.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

public class SpecialCamera {

	public boolean enabled;

	//Camera input from player...
	public Vec3d input = Vec3d.ZERO;

	//Pivot position of the camera in world-space.
	public Vec3d position = Vec3d.ZERO;
	public Vec3d realPosition = Vec3d.ZERO;

	public Vec3d focusPoint = null;

	public float targetZoom = 5;
	public float zoom = 0;

	//Position that the mouse is targeting. Player entity will face this position.
	public BlockHitResult mouseTarget;

	public float yaw = 0;
	public float yawSmoothed = 0;

	public float pitch = 45;

	public SpecialCamera() {
		mouseTarget = BlockHitResult.createMissed(Vec3d.ZERO, Direction.UP, BlockPos.ORIGIN);
	}

	public void tick(MinecraftClient client) {
	}

	public void update(Entity focusedEntity, float tickDelta) {
		float deltaTime = MinecraftClient.getInstance().getLastFrameDuration();

		zoom = MathHelper.lerp(1 - (float) Math.pow(0.5f, deltaTime), zoom, targetZoom);

		//Update position...
		{
			if (focusPoint != null) {
				var diff = focusPoint.subtract(position);

				position = position.lerp(focusPoint, 1 - (float) Math.pow(0.7f, deltaTime));

				if (diff.length() < 0.1f)
					focusPoint = null;
			} else {
				var inputRotated = input.rotateY((float) Math.toRadians(-yawSmoothed)).normalize();

				position = position.add(inputRotated.multiply(deltaTime * 0.4f));
			}
		}

		//Update rotation...
		{
			//Update rotation...
			yawSmoothed = MathHelper.lerpAngleDegrees(1 - (float) Math.pow(0.5f, deltaTime), yawSmoothed, yaw * 0.2f);
		}

		//Update in-world rotation and position.
		{
			Vec3d forward = new Vec3d(0, 0, -zoom);
			forward = forward.rotateX((float) Math.toRadians(45));
			forward = forward.rotateY(-(float) Math.toRadians(yawSmoothed - 180));

			realPosition = position.subtract(forward);
		}
	}

	public void updateInput() {
		float xInput = 0, yInput = 0;

		if (CraftCraftInput.pressedForward)
			yInput++;

		if (CraftCraftInput.pressedBack)
			yInput--;

		if (CraftCraftInput.pressedRight)
			xInput--;

		if (CraftCraftInput.pressedLeft)
			xInput++;

		input = new Vec3d(xInput, 0, yInput);
	}

	public void focusOnPosition(Vec3d focus) {
		focusPoint = focus;
	}

	public void enableCamera() {
		enabled = true;
		zoom = 0;
	}

	public void disableCamera() {
		enabled = false;
	}

	public void onScroll(double scrollAmount) {
		targetZoom = MathHelper.clamp(targetZoom - (float) scrollAmount, 1, 7);
	}

	public void rotate(double x, double y) {
		if(focusPoint != null)
			return;
		yaw += x;
		pitch = (float) MathHelper.clamp(pitch + y * 0.15f, -70, 70);
	}
}
