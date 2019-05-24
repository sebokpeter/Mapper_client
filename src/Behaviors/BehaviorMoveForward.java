package Behaviors;

import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;

/**
 * This behavior will move the robot forward.
 * @author sebok
 *
 */
public class BehaviorMoveForward implements Behavior{

	private boolean suppressed = false;
	private MovePilot pilot;
	
	/**
	 *  This behavior will move the robot forward.
	 */
	public BehaviorMoveForward(MovePilot pilot) {
		this.pilot = pilot;
	}
	
	/**
	 * This behavior will always try to take control, and move the robot forward.
	 */
	@Override
	public boolean takeControl() {
		return true;
	}

	@Override
	public void action() {
		suppressed = false;
		System.out.println("Moving forward");
		pilot.forward();
		while (!suppressed) {
			Thread.yield();
		}
		pilot.stop();
	}

	@Override
	public void suppress() {
		suppressed = true;
	}

}
