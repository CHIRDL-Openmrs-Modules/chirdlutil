package org.openmrs.module.chirdlutil.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Calendar;
import java.util.Date;

/**
 * Filters files in a directory based on the extension and date.  Only files with an older 
 * date than the one provided will be returned.  Directories will also be returned.
 *
 * @author Steve McKee
 */
public class DeleteFileFilter implements FilenameFilter {
	private Calendar date;
	private String extension;

	/**
	 * Constructor method
	 * 
	 * @param date Date used for comparison checks.  Only files older than this date will 
	 * be returned.
	 * @param extension Only files with this extension will be returned.
	 */
	public DeleteFileFilter(Calendar date, String extension) {
		this.date = date;
		this.extension = extension;
	}

	/**
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File directory, String filename) {
		File file = new File(directory, filename);
		if (file.isDirectory()) {
			return true;
		}
		
		if (!filename.endsWith(extension)) {
			return false;
		}
		
		Date lastModDate = new Date(new File(directory, filename).lastModified());
		Date compareDate = date.getTime();
		if (lastModDate.after(compareDate)) {
			return false;
		}
		
		return true;
	}
	
}
