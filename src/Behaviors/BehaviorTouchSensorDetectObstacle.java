package Behaviors;

import Communication.ObstacleReporter;
import Movement.RobotPosition;
import lejos.hardware.Sound;
import lejos.robotics.TouchAdapter;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;

/**
 * This behavior will turn the robot if either of the touch sensors are pressed.
 * @author sebok
 *
 */
public class BehaviorTouchSensorDetectObstacle implements Behavior {
	
	private boolean supressed = false;
	private MovePilot pilot;
	private TouchAdapter rightTouch;
	private TouchAdapter leftTouch;
	private ObstacleReporter or;
	private OdometryPoseProvider opp;
	
	public BehaviorTouchSensorDetectObstacle(MovePilot pilot, TouchAdapter rt, TouchAdapter lt, ObstacleReporter or, OdometryPoseProvider opp) {
		this.pilot = pilot;
		this.rightTouch = rt;
		this.leftTouch = lt;
		this.or = or;
		this.opp = opp;
	}
	
	/**
	 * This behavior will take control if one of the touch sensors is pressed.
	 */
	@Override
	public boolean takeControl() {
		return (rightTouch.isPressed() || leftTouch.isPressed());
	}

	@Override
	public void action() {
		supressed = false;
		System.out.println("Turning the robot -- Touch sensor detected an obstacle");
		
		// Report obstacle
		float x = opp.getPose().getX();
		float y = opp.getPose().getY();
		
		RobotPosition obstacle = new RobotPosition(x, y, true);
		obstacle.setHeading(opp.getPose().getHeading());
		or.reportObstacle(obstacle);
		
		Sound.beep();
		
		// Generate a random turn angle 
		int randomAngle = BehaviorHelper.randomWithRange(-200, 200);
		
		// Move 20 cm backwards
		pilot.travel(-20, true);
		while (!supressed && pilot.isMoving()) {
			Thread.yield();
		}
		// Rotate the robot using the randomly selected number.
		pilot.rotate(randomAngle, true);
		while (!supressed && pilot.isMoving()) {
			Thread.yield();
		}
		pilot.stop();
	}

	
	@Override
	public void suppress() {
		supressed = true;
	}

}
