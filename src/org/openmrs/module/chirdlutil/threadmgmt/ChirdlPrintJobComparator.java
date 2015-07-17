package org.openmrs.module.chirdlutil.threadmgmt;

import java.util.Comparator;

/**
 * Comparator used to sort ChirdlPrintJobRunnable objects.
 *
 * @author Steve McKee
 */
public class ChirdlPrintJobComparator implements Comparator<ChirdlPrintJobRunnable> {

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
    public int compare(ChirdlPrintJobRunnable o1, ChirdlPrintJobRunnable o2) {
		// There is currently no prioritization for print jobs.
    	return 0;
    }
}
