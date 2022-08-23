package com.ami.craftcraft.client.actions;

import net.minecraft.text.MutableText;
import net.minecraft.text.component.LiteralComponent;

import java.util.Queue;
import java.util.function.Consumer;

public class ActionCreator {

	public final MutableText nameText;
	private final Consumer<Action> _actionQueue;

	private ActionCreator(){
		nameText = null;
		_actionQueue = null;
	}

	public ActionCreator(Consumer<Action> actionQueue, String name) {
		_actionQueue = actionQueue;
		nameText = MutableText.create(new LiteralComponent(name));
	}

	public void tick() {

	}

	public void update(){

	}


	protected void queueAction(Action a) {
		_actionQueue.accept(a);
	}
}
