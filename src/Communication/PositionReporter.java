package Communication;

import Movement.RobotPosition;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.Pose;

/**
 * This class is attempts to retrieve and report the position of the robot every 0.5 seconds.
 * @author sebok
 *
 */
public class PositionReporter implements Runnable {

	private OdometryPoseProvider opp = null;
	
	// Used to send position data to the server
	private Client client = null;
	
	/**
	 * Assign an OdometryPoseProvider to this instance. It will be used used to retrieve the position of the robot;
	 * @param opp The OdometryPoseProvider assigned. 
	 */
	public void setPoseProvider(OdometryPoseProvider opp) {
		this.opp = opp;
	}
	/**
	 * Assign a Client to this instance. It will be used to report the position of the robot.
	 * @param c The Client instance assigned.
	 */
	public void setClient(Client c) {
		this.client = c;
	}
	
	@Override
	public void run() {
		
		while (true) {
			Pose p = opp.getPose();
			RobotPosition pos = new RobotPosition(p.getX(), p.getY(), false);
			
			client.addPosition(pos);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
