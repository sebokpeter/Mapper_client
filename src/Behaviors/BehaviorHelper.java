package Behaviors;

/**
 * This class contains methods that can be used by multiple behaviors
 * @author sebok
 *
 */
public class BehaviorHelper {

	/**
	 * Generate a random number between min and max.
	 * @param min The inclusive lower bound of the range.
	 * @param max The inclusive upper bound of the range.
	 * @return A generated random number between min and max.
	 */
	public static int randomWithRange(int min, int max)
	{
	   int range = (max - min) + 1;     
	   return (int)(Math.random() * range) + min;
	}
	
}
