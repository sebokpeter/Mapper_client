package Behaviors;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.robotics.subsumption.Behavior;

/**
 * This behavior will listen to a key press on the Escape button, and exits the program if it detects one.
 * @author sebok
 *
 */
public class BehaviorEscape implements Behavior, KeyListener {

	@SuppressWarnings("unused")
	private boolean supress = false;
	private boolean wantsToExit = false;
	
	public BehaviorEscape() {
		Button.ESCAPE.addKeyListener(this);
	}
	
	@Override
	public void keyPressed(Key k) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(Key k) {
		this.wantsToExit = true;
	}

	@Override
	public boolean takeControl() {
		return wantsToExit;
	}

	@Override
	public void action() {
		supress = false;
		System.exit(0);
	}

	@Override
	public void suppress() {
		this.supress = true;
	}

}
