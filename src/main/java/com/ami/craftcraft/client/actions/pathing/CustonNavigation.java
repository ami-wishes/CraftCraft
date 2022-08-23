package com.ami.craftcraft.client.actions.pathing;

import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class CustonNavigation extends MobNavigation {



	public CustonNavigation(MobEntity mobEntity, World world) {
		super(mobEntity, world);
	}

	public World getWorld(){
		return world;
	}
}
