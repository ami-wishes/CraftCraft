package com.ami.craftcraft.client.actions.move;

import com.ami.craftcraft.client.CraftCraftMod;
import com.ami.craftcraft.client.CraftCraftOutput;
import com.ami.craftcraft.client.actions.Action;
import com.ami.craftcraft.client.actions.pathing.Path;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

public class MoveAction extends Action {

	private Path movePath;

	public MoveAction(Vec3d startPos, Vec3d targetPos) {
		movePath = new Path();
		movePath.start = startPos;
		movePath.end = targetPos;
		movePath.cooldownTimer = 0;

		CraftCraftMod.PATH_MANAGER.addPath(movePath);
	}

	@Override
	public void tick(ClientPlayerEntity player) {
		//Move start of path...
		movePath.start = player.getPos();

		if (movePath.isValid) {
			Vec3d diff;

			for (int i = 0; i < movePath.positions.size() - 1; i++) {
				var current = movePath.positions.get(i);
				var next = movePath.positions.get(i + 1);

				for (int j = 0; j < 4; j++) {
					Vec3d pos = current.lerp(next, (float) j / 4.0f);

					player.world.addParticle(
							ParticleTypes.SOUL_FIRE_FLAME,
							pos.x, pos.y, pos.z,
							0, 0.01f, 0
					);
				}
			}

			//If there are path nodes, pathfind to the closest node.
			if (movePath.positions.size() > 0) {
				var pathTarget = movePath.positions.peek().add(0, 0, 0);

				//Calculate direction to pathfinding target
				diff = pathTarget.subtract(player.getPos());

				//Cut out Y pos
				double dY = diff.y;
				diff = new Vec3d(diff.x, 0, diff.z);

				//If the horizontal distance is less than 1 block, and the vertical distance is less than 0.01
				if (diff.length() < 1 && Math.abs(dY) < 0.01) {
					//Remove first entry of path, since we reached it...
					movePath.positions.removeFirst();
				}
			} else {
				diff = movePath.end.subtract(player.getPos());
				diff = new Vec3d(diff.x, 0, diff.z);
			}

			double dLength = movePath.end.subtract(player.getPos()).length();
			diff = diff.normalize();

			if (dLength > 1) {
				CraftCraftOutput.forwardMovement = 1;
				CraftCraftOutput.sideMovement = 0;
			} else {
				complete();
				CraftCraftOutput.forwardMovement = 0;
				CraftCraftOutput.sideMovement = 0;
			}
		}

		super.tick(player);
	}

	@Override
	public Vec3d lookTargetPos(ClientPlayerEntity entity) {
		if (movePath.isValid && movePath.positions.size() > 0) {
			var pos = movePath.positions.peek();
			return new Vec3d(pos.x, entity.getEyePos().y, pos.z);
		}
		return super.lookTargetPos(entity);
	}

	@Override
	public void complete() {
		movePath.complete();
		super.complete();
	}
}
