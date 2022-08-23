package com.ami.craftcraft.client.actions;

import com.ami.craftcraft.client.CraftCraftMod;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public abstract class Action {

	private boolean isCompleted = false;

	public void tick(ClientPlayerEntity player){

	}

	public void complete(){
		isCompleted = true;
	}

	public boolean isCompleted(){
		return isCompleted;
	}

	public void cancel(){
		complete();
	}


	public Vec3d lookTargetPos(ClientPlayerEntity entity){
		return CraftCraftMod.SPECIAL_CAMERA.mouseTarget.getPos();
	}
}
