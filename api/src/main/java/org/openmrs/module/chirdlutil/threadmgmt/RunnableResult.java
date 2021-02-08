package org.openmrs.module.chirdlutil.threadmgmt;

/**
 * Runnable that can return a result if the thread executing it waits for completion.
 * 
 * @author Steve McKee
 * @param <V> The expected response object
 */
public interface RunnableResult<V> extends Runnable {

	/**
	 * Returns a result.
	 * 
	 * @return The result matching the result type specified when instantiating this class
	 */
	public V getResult();
	
	/**
	 * Returns exception created during the process.
	 * 
	 * @return The exception that occurred
	 */
	public Exception getException();
}
