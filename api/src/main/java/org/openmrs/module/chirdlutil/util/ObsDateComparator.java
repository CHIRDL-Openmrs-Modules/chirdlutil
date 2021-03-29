package org.openmrs.module.chirdlutil.util;

import java.util.Comparator;

import org.openmrs.Obs;

/**
 * Comparator to order Observations by date.  The oldest will be on the front of the list.
 *
 * @author Steve McKee
 */
public class ObsDateComparator implements Comparator<Obs> {

	/**
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Obs o1, Obs o2) {
		if (o1 == null && o2 == null) {
			return 0;
		}
		
		if (o1 == null) {
			return -1;
		}
		
		if (o2 == null) {
			return 1;
		}
		
		if (o1.getObsDatetime() == null && o2.getObsDatetime() == null) {
			return 0;
		}
		
		if (o1.getObsDatetime() == null) {
			return -1;
		}
		
		if (o2.getObsDatetime() == null) {
			return 1;
		}
		
		return o1.getObsDatetime().compareTo(o2.getObsDatetime());
	}
	
}
