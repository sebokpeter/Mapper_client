package Communication;

import java.security.InvalidParameterException;

import Movement.RobotPosition;

/**
 * This class is responsible for reporting obstacles to the server.
 * @author sebok
 *
 */
public class ObstacleReporter {
	private Client client = null;
	
	public ObstacleReporter() {
	}
	
	public void setClient(Client c) {
		this.client = c;
	}
	
	/**
	 * Queue a position to report to the server. This position must be an obstacle (its isObstacle() method must return true).
	 * @param pos The position of the obstacle.
	 */
	public void reportObstacle(RobotPosition pos) {
		if(!pos.isObstackle()) {
			throw new InvalidParameterException("The position must be an obstacle!");
		}
		
		client.addPosition(pos);
	}
}
