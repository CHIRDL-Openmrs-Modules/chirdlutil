package org.openmrs.module.chirdlutil.tools;

import java.io.File;
import java.util.Calendar;

/**
 * Utility class used to delete files matching a specific extension that are also older 
 * than the specified date.  This is also recursive for all sub-directories.
 *
 * @author Steve McKee
 */
public class DeleteFiles {
	
	/**
	 * Deletes the files in the specified directory (and sub-directories) that match 
	 * the provided extension and are older than the number of days provided.
	 * 
	 * @param directory The directory to check
	 * @param extension The file extension to match
	 * @param daysOld Files older than this will be deleted.
	 */
	public void deleteFiles(File directory, String extension, Integer daysOld) {
		Calendar cal = Calendar.getInstance();
		// We need to convert to the negative inverse
		daysOld = daysOld - (daysOld * 2);
		cal.add(Calendar.DAY_OF_MONTH, daysOld);
		DeleteFileFilter filter = new DeleteFileFilter(cal, extension);
		delete(directory, extension, filter);
	}
	
	private void delete(File directory, String extension, DeleteFileFilter filter) {
		File[] files = directory.listFiles(filter);
		for (File file : files) {
			if (file.isDirectory()) {
				delete(file, extension, filter);
			} else {
				System.out.println("Deleting: " + file.getAbsolutePath());
				file.delete();
			}
		}
	}
	
	private void printUsage() {
		System.out.println("Arguments:");
		System.out.println("Arg 1: The directory to check");
		System.out.println("Arg 2: The files extension to check");
		System.out.println("Arg 3: The past n days worth of files to keep");
		System.out.println("");
		System.out.println("Usage:");
		System.out.println("DeleteFiles C:\\temp sql 5");
		System.out.println("This will delete all sql files in the C:\\temp directory older than 5 days");
	}
	
	/**
	 * Auto generated method comment
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		DeleteFiles delFiles = new DeleteFiles();
		if (args.length != 3) {
			delFiles.printUsage();
			System.exit(1);
		}
		
		String directoryStr = args[0];
		String extension = args[1];
		String daysOldStr = args[2];
		
		File directory = new File(directoryStr);
		if (!directory.exists()) {
			System.out.println("The directory " + directoryStr + " does not exist");
			System.out.println("");
			delFiles.printUsage();
			System.exit(1);
		} else if (!directory.isDirectory()) {
			System.out.println("The directory " + directoryStr + " is not a directory");
			System.out.println("");
			delFiles.printUsage();
			System.exit(1);
		}
		
		Integer daysOld = null;
		try {
			daysOld = Integer.parseInt(daysOldStr);
		} catch (NumberFormatException e) {
			System.out.println("The number of days is not an Integer: " + daysOldStr);
			System.out.println("");
			delFiles.printUsage();
			System.exit(1);
		}
		
		delFiles.deleteFiles(directory, extension, daysOld);
		
		System.exit(0);
	}
	
}
