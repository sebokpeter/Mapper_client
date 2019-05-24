package Behaviors;

import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.Pose;
import lejos.robotics.subsumption.Behavior;

/**
 * Limits the robot's movement to n*n cm square, where n is provided in the constructor.
 * @author sebok
 *
 */
public class BehaviorLimitMovement implements Behavior {

	private boolean supressed = false;
	private OdometryPoseProvider opp;
	private MovePilot pilot;
	
	private int movementLimit;
	
	/**
	 * Create a behavior that limits the robot's movement.
	 * @param opp The pose provider that will be used to determine the robot's position.
	 * @param limit The robot's movement will be limited to a square based on this number. It is in centimeters.
	 * @param pilot The pilot that is used to move the robot.
	 */
	public BehaviorLimitMovement(OdometryPoseProvider opp, int limit, MovePilot pilot) {
		this.opp = opp;
		this.movementLimit = limit / 2; // Since the robot starts at 0,0 which is at the center of the n*n square, we need to divide the number by 2
		this.pilot = pilot;
	}
	
	/**
	 * Take control if the robot is at or over the distance specified by the limit.
	 */
	@Override
	public boolean takeControl() {
		float x, y;
		Pose p = opp.getPose();
		
		x = p.getX();
		
		if(x >= movementLimit || x <= -movementLimit) {
			return true;
		}
		
		y = p.getY();
		
		if(y >= movementLimit || y <= -movementLimit) {
			return true;
		}
		
		return false;
	}

	/**
	 * The robot should turn back.
	 */
	@Override
	public void action() {
		supressed = false;
		System.out.println("Limit reached -- turning back");

		// Turn around
		pilot.rotate(-180, true);
		while (!supressed && pilot.isMoving()) {
			Thread.yield();
		}
		// Travel 20 cm backwards
		pilot.travel(20, true);
		while (!supressed && pilot.isMoving()) {
			Thread.yield();
		}
		
		pilot.stop();
	}

	@Override
	public void suppress() {
		this.supressed = true;
	}
}
