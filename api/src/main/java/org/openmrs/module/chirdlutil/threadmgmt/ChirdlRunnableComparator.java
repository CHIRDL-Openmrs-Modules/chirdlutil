package org.openmrs.module.chirdlutil.threadmgmt;

import java.util.Comparator;

/**
 * Comparator used to sort ChirdlRunnable objects by priority.  The priority is an integer, thus 
 * the objects are sorted numerically.  If an object is compared to another with the same priority, 
 * it is considered to be sorted below it.
 *
 * @author Steve McKee
 */
public class ChirdlRunnableComparator implements Comparator<ChirdlRunnable> {

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
    public int compare(ChirdlRunnable o1, ChirdlRunnable o2) {
		int o1Priority = o1.getPriority();
		int o2Priority = o2.getPriority();
		// Return -1 if it has a higher priority so it gets moved to the front of the queue.
		// Else return 1 so that it goes after the last one with the same priority in the list.
	    if (o1Priority < o2Priority) {
	    	return -1;
	    } else {
	    	return 1;
	    }
    }
}
