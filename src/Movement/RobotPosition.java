package Movement;

import java.io.Serializable;

/**
 * Represents a position (either the robot or an obstacle) using x and y coordinates.
 * @author sebok
 *
 */
public class RobotPosition implements Serializable {
	private static final long serialVersionUID = -1217202219684572342L;
	private float x;
	private float y;
	private float heading;
	private boolean isObstacle;
	
	
	public RobotPosition(float x, float y, boolean obs) {
		this.x = x;
		this.y = y;
		this.isObstacle = obs;
		this.heading = Float.NaN;
	}
	
	public void setHeading(float hdn) {
		this.heading = hdn;
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public float getHeading() throws IllegalAccessException {
		if(this.heading == Float.NaN) {
			throw new IllegalAccessException("Heading is not set on this object");
		}
		
		return this.heading;
	}
	
	public boolean isObstackle() {
		return this.isObstacle;
	}
}

