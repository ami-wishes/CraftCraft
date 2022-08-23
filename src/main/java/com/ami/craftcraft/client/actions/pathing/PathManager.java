package com.ami.craftcraft.client.actions.pathing;

import com.ami.craftcraft.client.CraftCraftMod;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.util.math.*;
import net.minecraft.world.chunk.ChunkCache;

import java.util.*;

public class PathManager {

	private ArrayList<Path> _activePaths = new ArrayList<>();
	private List<Vec3i> _neighborPositions = new ArrayList<>() {{
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && y == 0 && z == 0) continue;

					add(new Vec3i(x, y, z));
				}
			}
		}
	}};

	private MinecraftClient client;

	private int updateCooldown = 0;

	public void tick(MinecraftClient client) {
		this.client = client;

		if (navigation == null) {
			var ng = new LandPathNodeMaker();
			ng.setCanEnterOpenDoors(true);
			navigation = new PathNodeNavigator(ng, 256);
		}

		//Cooldown...
		if (updateCooldown > 0) {
			updateCooldown--;
			return;
		}

		for (int i = _activePaths.size() - 1; i >= 0; i--) {
			var path = _activePaths.get(i);

			if (path.isComplete())
				_activePaths.remove(i);
			else
				updatePath(path);
		}
	}

	public void addPath(Path p) {
		_activePaths.add(p);
	}

	private PathNodeNavigator navigation;

	private Stack<PathNode> _nodeCache = new Stack<>();
	private Stack<PathNode> _createdNodes = new Stack<>();
	private PathNode[] neighbourNodes = new PathNode[26];

	private List<PathNode> openSet = new ArrayList<>();
	private HashSet<PathNode> closedSet = new HashSet<>();

	private List<Vec3d> _positionReverseCache = new ArrayList<>();

	private HashMap<PathNode, PathNode> _nodeMap = new HashMap<>();


	private void updatePath(Path path) {
		if (path.cooldownTimer-- == 0) {
			path.cooldownTimer = 15;
			updateCooldown = 5;

			path.isValid = false;
			path.positions.clear();
			path.positions.add(path.end);

			PathNode startBP = getNode(path.end);
			PathNode endBP = getNode(path.start);


			//Clear sets...
			openSet.clear();
			closedSet.clear();
			_nodeMap.clear();

			openSet.add(startBP);

			//While there are pathfinding nodes to check...
			while (openSet.size() > 0 && openSet.size() < 1000) {
				var currentNode = openSet.get(0);
				var cFCost = currentNode.getFCost();
				//Find cheapest node...
				for (int i = 1; i < openSet.size(); i++) {
					var compVar = openSet.get(i);
					var compVarFCost = compVar.getFCost();

					if (compVarFCost < cFCost || compVarFCost == cFCost && compVar.hCost < currentNode.hCost) {
						currentNode = compVar;
						cFCost = compVarFCost;
					}
				}

				//Remove current node, as it's being checked right now...
				openSet.remove(currentNode);
				closedSet.add(currentNode);

				//If this node is the end, complete the path.
				if (currentNode.equals(endBP)) {
					//Clear positions...
					path.positions.clear();
					_positionReverseCache.clear();

					//Trace back our path...
					for (int i = 0; i < 256 && currentNode.parent != null; i++) {
						_positionReverseCache.add(new Vec3d(currentNode.x, currentNode.y, currentNode.z));
						currentNode = currentNode.parent;

						if (i == 255)
							break;
					}

					if (_positionReverseCache.size() == 0)
						break;

					//Reverse list.
					for (int i = 0; i < _positionReverseCache.size(); i++)
						path.positions.add(_positionReverseCache.get(i).add(0.5f, 0, 0.5f));

					path.positions.removeLast();
					path.positions.add(path.end);

					path.isValid = true;
					break;
				}

				updateNeighbors(currentNode);

				for (PathNode neighbourNode : neighbourNodes) {
					if (neighbourNode == null)
						continue;

					if (closedSet.contains(neighbourNode))
						continue;


					float newMoveCost = currentNode.gCost + getDistance(currentNode, neighbourNode);
					if (newMoveCost < neighbourNode.gCost || !openSet.contains(neighbourNode)) {
						if (!openSet.contains(neighbourNode)) {
							neighbourNode.gCost = newMoveCost;
							neighbourNode.hCost = getDistance(neighbourNode, endBP);
							neighbourNode.parent = currentNode;
							openSet.add(neighbourNode);
						}
					}
				}
			}

			while (_createdNodes.size() > 0)
				_nodeCache.push(_createdNodes.pop());

			openSet.clear();
			closedSet.clear();
			_nodeMap.clear();

			if (path.isValid)
				CraftCraftMod.LOGGER.info("Found path!");
			else
				CraftCraftMod.LOGGER.error("No path found!");
		}
	}

	public PathNode getNode(Vec3d pos) {
		int x = MathHelper.floor(pos.getX());
		int y = MathHelper.floor(pos.getY());
		int z = MathHelper.floor(pos.getZ());

		return getNode(x, y, z);
	}

	PathNode _indexNode = new PathNode(0, 0, 0);

	public PathNode getNode(int x, int y, int z) {

		_indexNode.set(x, y, z);
		PathNode pn = _nodeMap.get(_indexNode);

		if (pn == null) {
			if (_nodeCache.size() > 0) {
				pn = _nodeCache.pop();
				pn.x = x;
				pn.y = y;
				pn.z = z;
			} else {
				pn = new PathNode(x, y, z);

				_createdNodes.push(pn);
			}

			pn.hCost = 0;
			pn.gCost = 0;
			pn.parent = null;

			_nodeMap.put(pn, pn);
		} else {
			//System.out.println("test");
		}
		return pn;
	}

	public void updateNeighbors(PathNode current) {
		BlockPos currentPos = new BlockPos(current.x, current.y, current.z);

		for (int i = 0; i < _neighborPositions.size(); i++) {
			neighbourNodes[i] = null;
			var pos = _neighborPositions.get(i);

			BlockPos newPos = currentPos.add(pos);
			BlockPos below = newPos.add(0, -1, 0);
			BlockPos above = newPos.add(0, 1, 0);

			BlockState self = client.world.getBlockState(newPos);
			BlockState belowBlock = client.world.getBlockState(below);
			BlockState aboveBlock = client.world.getBlockState(above);


			if (self.getCollisionShape(client.world, newPos).isEmpty() && aboveBlock.getCollisionShape(client.world, above).isEmpty() && belowBlock.hasSolidTopSurface(client.world, below, client.player, Direction.UP))
				neighbourNodes[i] = getNode(newPos.getX(), newPos.getY(), newPos.getZ());
		}
	}

	public float getDistance(PathNode current, PathNode neighbor) {
		float dx = Math.abs(current.x - neighbor.x);
		float dy = Math.abs(current.y - neighbor.y);
		float dz = Math.abs(current.z - neighbor.z);
		dx = dx * dx;

		if (dy > 1.5)
			dy *= 100;

		dy = (dy * dy);
		dz = dz * dz;
		return (float) Math.sqrt(dx + dy + dz);
	}

	private static class PathNode {
		public float gCost;
		public float hCost;

		public int x, y, z;

		public PathNode parent;

		public PathNode(int x, int y, int z) {
			set(x, y, z);
		}

		public PathNode(Vec3d pos) {
			set(pos);
		}

		public float getFCost() {
			return gCost + hCost;
		}


		public void set(Vec3d pos) {
			x = MathHelper.floor(pos.getX());
			y = MathHelper.floor(pos.getY());
			z = MathHelper.floor(pos.getZ());
		}

		public void set(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}


		public boolean equals(PathNode other) {
			return x == other.x && y == other.y && z == other.z;
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (this.getClass() != obj.getClass())
				return false;

			PathNode other = (PathNode) obj;
			return equals(other);
		}

		@Override
		public int hashCode() {
			return (this.y + this.z * 31) * 31 + this.x;
		}
	}
}
