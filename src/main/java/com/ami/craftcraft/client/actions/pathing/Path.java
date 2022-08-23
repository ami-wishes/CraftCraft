package com.ami.craftcraft.client.actions.pathing;

import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Path {

	public int cooldownTimer = 0;

	public Vec3d start = Vec3d.ZERO, end = Vec3d.ZERO;

	public boolean isValid = false;
	public LinkedList<Vec3d> positions = new LinkedList<>();

	public boolean isReachPath = false;


	private boolean _isComplete;
	public void complete() {
		_isComplete = true;
	}
	public boolean isComplete() {
		return _isComplete;
	}
}
