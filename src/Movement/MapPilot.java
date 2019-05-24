package Movement;
import Behaviors.BehaviorEscape;
import Behaviors.BehaviorLimitMovement;
import Behaviors.BehaviorMoveForward;
import Behaviors.BehaviorTouchSensorDetectObstacle;
import Behaviors.BehaviorUltrasonicSensorDetectObstacle;
import Communication.Client;
import Communication.ObstacleReporter;
import Communication.PositionReporter;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.ColorAdapter;
import lejos.robotics.RangeFinderAdapter;
import lejos.robotics.TouchAdapter;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

/**
* This class will contain the Arbitrator, and thus control the behavior of the robot.
**/
public class MapPilot {
	private MovePilot pilot = null;
	
	private RangeFinderAdapter sonicRangeFinder;
	private TouchAdapter rightTouchDetector;
	private TouchAdapter leftTouchDetector;
	@SuppressWarnings("unused")
	private ColorAdapter colorAdapter;
	
	private Arbitrator arbitrator = null;
	
	// Detection range for the ultrasonic sensor, the robot will back up and turn if it detects an object within this distance.
	private final int DETECTION_RANGE = 30;
	
	// Pilot acceleration and speed values
	private final int LINEAR_ACC = 30;
	private final int ANGULAR_ACC = 30;
	private final int PILOT_SPEED = 30;
	
	// Wheel offset and diameter values
	private final double WHEEL_DIAMETER_CM = 5.6;
	private final double WHEEL_OFFSET_CM = 6.0;
	
	// The robot can travel in an n*n squre where n is the number below 
	private final int TRAVEL_RANGE_CM = 200;
	
	// Used to retrieve the position of the robot and obstacles
	private OdometryPoseProvider opp = null;
	private PositionReporter pr = null;
	private ObstacleReporter or = null;
	private Client client = null;
	
	public MapPilot(Client c) {
		this.client = c;
	}
	
	
	/**
	 *  Set up the sensors and motors used by the different behaviors, and start the arbitrator.
	 */
	public void start() {
		try
		(
			// Set up sensors
			EV3TouchSensor leftSensor = new EV3TouchSensor(SensorPort.S1);
			EV3TouchSensor rightSensor = new EV3TouchSensor(SensorPort.S4);
			EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S3);
			EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S2);
				
			// Set up motors
			EV3LargeRegulatedMotor motorB = new EV3LargeRegulatedMotor(MotorPort.B);
			EV3LargeRegulatedMotor motorC = new EV3LargeRegulatedMotor(MotorPort.C);
		
		)
		{
			setUpSensorAdapters(leftSensor, rightSensor, usSensor, colorSensor);
				
			setUpPilot(motorB, motorC);
			
			// Create the arbitrator and the behaviors.
			createArbitrator();
			arbitrator.go();
			
		}
	}
	

	/**
	 * This method will set up the sensor adapters used by the robot.
	 * @param leftSensor The left side touch sensor.
	 * @param rightSensor The right side touch sensor.
	 * @param usSensor The ultrasonic sensor.
	 */
	private void setUpSensorAdapters(EV3TouchSensor leftSensor, EV3TouchSensor rightSensor, EV3UltrasonicSensor usSensor,
									EV3ColorSensor cSensor) {
		usSensor.getDistanceMode();
		
		// Sensor adapters
		rightTouchDetector = new TouchAdapter(rightSensor);
		leftTouchDetector = new TouchAdapter(leftSensor);
		sonicRangeFinder = new RangeFinderAdapter(usSensor);
		colorAdapter = new ColorAdapter(cSensor);
	}

	/**
	 * This method will create a differential wheeled chassis using the two motors passed as 
	 * parameters, and use it to create a MovePilot.
	 * @param m1 The first wheel
	 * @param m2 The second wheel
	 */
	private void setUpPilot(EV3LargeRegulatedMotor m1, EV3LargeRegulatedMotor m2) {
		
		// Chassis and MovePilot
		Wheel wheel1 = WheeledChassis.modelWheel(m1, WHEEL_DIAMETER_CM).offset(-WHEEL_OFFSET_CM);
		Wheel wheel2 = WheeledChassis.modelWheel(m2, WHEEL_DIAMETER_CM).offset(WHEEL_OFFSET_CM);

		Chassis chassis = new WheeledChassis(new Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);
		
		pilot = new MovePilot(chassis);
		
		// Set linear and angular acceleration to a relatively low value, to prevent skipping
		pilot.setLinearAcceleration(LINEAR_ACC);
		pilot.setAngularAcceleration(ANGULAR_ACC);
		
		// Set pilot speed lower to avoid hard collisions
		pilot.setLinearSpeed(PILOT_SPEED);	
		

		// Create a new position reporter that can be used to retrieve the position of the robot
		opp = new OdometryPoseProvider(pilot);
		pr = new PositionReporter();
		or = new ObstacleReporter();
		pr.setPoseProvider(opp);
		pr.setClient(client);
		or.setClient(client);
		
		Thread posThread = new Thread(pr);
		posThread.start();
	}
	
	/**
	 * Create the behaviors, arrange them by priority and pass them to an arbitrator.
	 */
	private void createArbitrator() {

		Behavior moveForward = new BehaviorMoveForward(pilot);
		Behavior limitMovement = new BehaviorLimitMovement(opp, TRAVEL_RANGE_CM, pilot);
		Behavior touchTurn = new BehaviorTouchSensorDetectObstacle(pilot, rightTouchDetector, leftTouchDetector, or, opp);
		Behavior ultrasonicBehavior = new BehaviorUltrasonicSensorDetectObstacle(pilot, sonicRangeFinder, DETECTION_RANGE, or, opp);
		Behavior exit = new BehaviorEscape();
		
		Behavior[] behaviorList = new Behavior[] {moveForward, limitMovement, ultrasonicBehavior, touchTurn, exit};
		
		arbitrator = new Arbitrator(behaviorList);		
	}
}
