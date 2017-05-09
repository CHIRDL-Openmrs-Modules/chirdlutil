package org.openmrs.module.chirdlutil.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Calendar;
import java.util.Date;


/**
 * File filter class used to return any files (not directories) outside the specified days.
 *
 * @author Steve McKee
 */
public class FileAgeFilter implements FileFilter {
	
	int daysToKeep = 1;
	
	/**
	 * Constructor method
	 * 
	 * @param daysToKeep Files within the day number window will not be returned.  Anything older will be returned.
	 */
	public FileAgeFilter(int daysToKeep) {
		this.daysToKeep = daysToKeep;
	}

	/**
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File pathname) {
		// Directories do not get moved.
		if (pathname.isDirectory()) {
			return false;
		}
		
		// Create a Calendar to hold the last modified date/time of the file.
        Calendar fileCal = Calendar.getInstance();
        fileCal.setTime(new Date(pathname.lastModified()));
        
        // Create a Calendar that is the current date/time minus the number of days we keep.
        Calendar compareCal = Calendar.getInstance();
        compareCal.add(Calendar.DAY_OF_MONTH, -daysToKeep);
        // Accept the file if its last modified date/time is outside the number of days we want to keep.
        if (fileCal.compareTo(compareCal) < 0) {
        	return true;
        }
        
        return false;
    }
	
}
