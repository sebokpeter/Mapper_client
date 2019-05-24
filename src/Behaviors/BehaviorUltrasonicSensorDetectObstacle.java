package Behaviors;

import Communication.ObstacleReporter;
import Movement.RobotPosition;
import lejos.hardware.Sound;
import lejos.robotics.RangeFinderAdapter;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;

/**
 * This behavior will turn the robot if the ultrasonic sensor detects an obstacle closer than the determined distance.
 * @author sebok
 *
 */
public class BehaviorUltrasonicSensorDetectObstacle implements Behavior {

	private boolean supressed = false;
	private MovePilot pilot = null;
	private RangeFinderAdapter rangeFinder = null;
	private int detectionRange = 20;
	
	private ObstacleReporter or;
	private OdometryPoseProvider opp;
	/**
	 * Creates a BehaviorUltrasonicSensorDetectObstacle instance.
	 * @param pilot The pilot that will be used to perform the turn.
	 * @param rfa The RangeFinderAdapter that will be used to detect obstacles.
	 * @param range The distance that the behavior will use to determine if it should take control.
	 */
	public BehaviorUltrasonicSensorDetectObstacle(MovePilot pilot, RangeFinderAdapter rfa, int range, ObstacleReporter or, OdometryPoseProvider opp) {
		this.pilot = pilot;
		this.rangeFinder = rfa;
		this.detectionRange = range;
		this.or = or;
		this.opp = opp;
	}
	
	/**
	 * This behavior will take control if the ultrasonic sensor detects an obstacle closer than the determined distance.
	 */
	@Override
	public boolean takeControl() {
		return (rangeFinder.getRange() < detectionRange);
	}
	
	@Override
	public void action() {
		supressed = false;
		System.out.println("Turning the robot -- Ultrasonic sensor detected an obstacle");
		
		// Report obstacle
		float x = opp.getPose().getX();
		float y = opp.getPose().getY();
		float theta = (float) Math.toRadians(opp.getPose().getHeading());
		float c = rangeFinder.getRange();
		
		// Calculate the position of the obstacle based on heading and measured distance
		float dx = (float) (Math.cos(theta) * c);
		float dy = (float) (Math.sin(theta) * c);
		
		x += dx;
		y += dy;
		
		RobotPosition obstacle = new RobotPosition(x, y, true);
		obstacle.setHeading(theta);
		or.reportObstacle(obstacle);
		
		Sound.buzz();
		
		// Generate a random turn angle 
		int randomAngle = BehaviorHelper.randomWithRange(-200, 200);
		
		// Rotate the robot using the randomly selected number.
		pilot.rotate(randomAngle, true);
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
