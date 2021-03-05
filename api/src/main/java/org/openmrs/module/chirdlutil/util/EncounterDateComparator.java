package org.openmrs.module.chirdlutil.util;

import java.util.Comparator;

import org.openmrs.Encounter;

/**
 * Comparator to order Encounters by date.
 *
 * @author Steve McKee
 */
public class EncounterDateComparator implements Comparator<Encounter> {

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Encounter o1, Encounter o2) {
		if (o1 == null && o2 == null) {
			return 0;
		}
		
		if (o1 == null) {
			return -1;
		}
		
		if (o2 == null) {
			return 1;
		}
		
		if (o1.getEncounterDatetime() == null && o2.getEncounterDatetime() == null) {
			return 0;
		}
		
		if (o1.getEncounterDatetime() == null) {
			return -1;
		}
		
		if (o2.getEncounterDatetime() == null) {
			return 1;
		}
		
		return o1.getEncounterDatetime().compareTo(o2.getEncounterDatetime());
	}
	
}
