package com.ami.craftcraft.mixin;

import com.ami.craftcraft.client.CraftCraftInput;
import com.ami.craftcraft.client.CraftCraftMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

	protected CameraMixin() {
	}

	@Inject(method = "update", at = @At("RETURN"))
	public void craftcraft_update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {

		//If we're using the special camera, we should overwrite vanilla behaviour, basically.
		if (CraftCraftMod.SPECIAL_CAMERA.enabled) {
			var client = MinecraftClient.getInstance();
			var camera = CraftCraftMod.SPECIAL_CAMERA;

			//Set this so we can see ourselves and don't see our hand.
			client.options.setPerspective(Perspective.THIRD_PERSON_BACK);

			camera.update(focusedEntity, tickDelta);

			if (CraftCraftInput.pressedUse) {
				client.mouse.lockCursor();
			} else {
				client.mouse.unlockCursor();

				var proj = new Matrix3f(RenderSystem.getProjectionMatrix());
				var view = RenderSystem.getInverseViewRotationMatrix();
				view.invert();
				proj.multiply(view);
				proj.invert();

				float mouseXClip = (float) MathHelper.lerp(client.mouse.getX() / (float) client.getWindow().getFramebufferWidth(), -1, 1);
				float mouseYClip = (float) MathHelper.lerp(((float) client.getWindow().getFramebufferHeight() - client.mouse.getY()) / (float) client.getWindow().getFramebufferHeight(), -1, 1);

				//Transform forward vector by camera rotation
				Vec3f forward = new Vec3f(mouseXClip, mouseYClip, 1);
				forward.transform(proj);
				forward.normalize();

				//Calculate start/end positions for raycast
				Vec3d source = camera.realPosition;
				Vec3d dest = source.add(new Vec3d(forward).multiply(100));

				var result = client.world.raycast(new RaycastContext(
						source, dest,
						RaycastContext.ShapeType.COLLIDER,
						RaycastContext.FluidHandling.NONE,
						focusedEntity
				));

				camera.mouseTarget = result;
			}

			//Update camera...
			this.setRotation(camera.yawSmoothed, camera.pitch);
			this.setPos(camera.realPosition.x, camera.realPosition.y, camera.realPosition.z);
		}

	}

	@Shadow
	protected void setPos(double lerp, double v, double lerp1) {
	}

	@Shadow
	protected void setRotation(float yaw, float pitch) {
	}

}
