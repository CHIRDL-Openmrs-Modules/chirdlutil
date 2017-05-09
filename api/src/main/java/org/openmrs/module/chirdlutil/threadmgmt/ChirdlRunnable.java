package org.openmrs.module.chirdlutil.threadmgmt;

/**
 * Interface defining an object executed by the ThreadManager
 *
 * @author Steve McKee
 */
public interface ChirdlRunnable extends Runnable {
	
	/** Thread priorities **/
	public static final int PRIORITY_ONE = 1;
	public static final int PRIORITY_TWO = 2;
	public static final int PRIORITY_THREE = 3;
	public static final int PRIORITY_FOUR = 4;
	public static final int PRIORITY_FIVE = 5;

	/**
	 * Returns the priority of the Runnable object.
	 * 
	 * @return The priority of the Runnable object.
	 */
	public int getPriority();
	
	/**
	 * Returns the name of the Runnable object.
	 * 
	 * @return The name of the Runnable object.
	 */
	public String getName();
}
