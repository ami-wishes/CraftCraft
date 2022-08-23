package com.ami.craftcraft.mixin;

import com.ami.craftcraft.client.CraftCraftMod;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

	@Shadow
	@Final
	protected MinecraftClient client;

	@Shadow
	public abstract float getYaw(float tickDelta);

	public ClientPlayerEntityMixin(ClientWorld clientWorld, GameProfile gameProfile, @Nullable PlayerPublicKey playerPublicKey) {
		super(clientWorld, gameProfile, playerPublicKey);
	}

	@Override
	public void changeLookDirection(double cursorDeltaX, double cursorDeltaY) {
		if (!CraftCraftMod.SPECIAL_CAMERA.enabled) {
			super.changeLookDirection(cursorDeltaX, cursorDeltaY);
		} else {
			CraftCraftMod.SPECIAL_CAMERA.rotate(cursorDeltaX, cursorDeltaY);
		}
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {

		var camera = CraftCraftMod.SPECIAL_CAMERA;
		if (!camera.enabled)
			return;

		Vec3d diff = null;

		var action = CraftCraftMod.PLAYER_CONTROLLER.getCurrentAction();

		if (action != null)
			diff = action.lookTargetPos((ClientPlayerEntity) (Object) this).subtract(getEyePos()).normalize();

		if (action == null && camera.mouseTarget != null && camera.mouseTarget.getType() != HitResult.Type.MISS)
			diff = camera.mouseTarget.getPos().subtract(getEyePos()).normalize();

		if(diff == null)
			diff = Vec3d.ZERO;

		//Horizontal rotation
		setYaw(MathHelper.lerpAngleDegrees(0.5f, getYaw(), (float) Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90));

		//Vertical rotation
		setPitch(MathHelper.lerpAngleDegrees(0.5f, getPitch(), (float) Math.toDegrees(Math.asin(-diff.y))));
	}


	private static Vec3d project(Vec3d vector, Vec3d planeNormal) {
		double sqrMag = planeNormal.dotProduct(planeNormal);
		if (sqrMag < 0.0001f)
			return vector;
		else {
			var dot = vector.dotProduct(planeNormal);
			return new Vec3d(vector.x - planeNormal.x * dot / sqrMag,
					vector.y - planeNormal.y * dot / sqrMag,
					vector.z - planeNormal.z * dot / sqrMag);
		}
	}
}
