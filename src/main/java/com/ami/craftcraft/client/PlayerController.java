package com.ami.craftcraft.client;

import com.ami.craftcraft.client.actions.Action;
import com.ami.craftcraft.client.actions.ActionCreator;
import com.ami.craftcraft.client.actions.move.MoveActionCreator;
import net.minecraft.client.MinecraftClient;

import java.util.LinkedList;
import java.util.Queue;

public class PlayerController {

	public final Queue<Action> actionQueue = new LinkedList<Action>();

	public final ActionCreator[] _actionCreators = new ActionCreator[]{
			new MoveActionCreator(this::addAction)
	};
	private int _creatorIndex = 0;

	public void tick(MinecraftClient client) {
		getCurrentCreator().tick();

		if (actionQueue.size() > 0) {
			var first = actionQueue.peek();

			//Get and tick first task in list.
			first.tick(client.player);

			//Remove, task was completed.
			if (first.isCompleted())
				actionQueue.poll();
		}
	}

	public void update() {
		getCurrentCreator().update();
	}

	public void addAction(Action a) {
		if (!CraftCraftInput.pressedSneak)
			while(actionQueue.size()>0)
				actionQueue.poll().cancel();

		actionQueue.add(a);
	}

	public ActionCreator getCurrentCreator() {
		return _actionCreators[_creatorIndex];
	}

	public void scrollCreator(int scrollAmount) {
		_creatorIndex += scrollAmount;
		if (_creatorIndex < 0)
			_creatorIndex = _actionCreators.length - 1;
		if (_creatorIndex >= _actionCreators.length)
			_creatorIndex = 0;
	}

	public Action getCurrentAction() {
		return actionQueue.peek();
	}
}
