package org.openmrs.module.chirdlutil.tools;

import java.io.File;

/**
 * This class will take a given source directory, copy it and all its sub-directories to a 
 * given target directory.  However, this will only copy directories.  No files will be 
 * copied to the target directory.
 *
 * @author Steve McKee
 */
public class CopyDirectoryCmdLine {
    
	private void copyDirectory(File dirToCopy, File destDir, boolean verbose) {
		String dirName = dirToCopy.getName();
		File newDir = new File(destDir, dirName);
		newDir.mkdirs();
		File[] files = dirToCopy.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				if (verbose) {
					System.out.println("Copying: " + file.getAbsolutePath());
				}
				
				dirName = file.getName();
				boolean success = createDirectory(file, new File(newDir, dirName), verbose);
				if (!success) {
					return;
				}
			} else {
				if (verbose) {
					System.out.println(file.getAbsolutePath() + " is not a directory.");
				}
			}
		}
	}
	
	private boolean createDirectory(File dirToCopy, File destDir, boolean verbose) {
		destDir.mkdirs();
		File[] files = dirToCopy.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				if (verbose) {
					System.out.println("Copying: " + file.getAbsolutePath());
				}
				
				String dirName = file.getName();
				boolean success = createDirectory(file, new File(destDir, dirName), verbose);
				if (!success) {
					return false;
				}
			} else {
				if (verbose) {
					System.out.println(file.getAbsolutePath() + " is not a directory.");
				}
			}
		}
		
		return true;
	}
	
	private void printUsage() {
		System.out.println("Arguments:");
		System.out.println("Arg 1: The directory to copy");
		System.out.println("Arg 2: The directory where the copy will be placed");
		System.out.println("Options:");
		System.out.println("-v\tverbose");
		System.out.println("");
		System.out.println("Usage:");
		System.out.println("CopyDirectoryCmdLine C:\\temp\\copyDirectory C:\\temp\\targetDirectory -v");
		System.out.println("This will copy the copyDirectory folder and it subfolders (without files) to the C:\\temp\\targetDirectory directory");
	}

    public static void main(String[] args) {
    	CopyDirectoryCmdLine copyDirectories = new CopyDirectoryCmdLine();
		if (args.length < 2 || args.length > 3) {
			copyDirectories.printUsage();
			System.exit(1);
		}
		
		String copyDir = args[0];
		String targetDir = args[1];
		boolean verbose = false;
		if(args.length == 3) {
			String verboseStr = args[2];
			if ("-v".equalsIgnoreCase(verboseStr)) {
				verbose = true;
			}
		}
		
		File copyDirectory = new File(copyDir);
		if (!copyDirectory.exists()) {
			System.out.println("The directory " + copyDir + " does not exist");
			System.out.println("");
			copyDirectories.printUsage();
			System.exit(1);
		} else if (!copyDirectory.isDirectory()) {
			System.out.println("The directory " + copyDir + " is not a directory");
			System.out.println("");
			copyDirectories.printUsage();
			System.exit(1);
		}
		
		File targetDirectory = new File(targetDir);
		if (!targetDirectory.exists()) {
			System.out.println("The directory " + targetDir + " does not exist");
			System.out.println("");
			copyDirectories.printUsage();
			System.exit(1);
		} else if (!targetDirectory.isDirectory()) {
			System.out.println("The directory " + targetDir + " is not a directory");
			System.out.println("");
			copyDirectories.printUsage();
			System.exit(1);
		}
		
		copyDirectories.copyDirectory(copyDirectory, targetDirectory, verbose);
		
		System.exit(0);
    }
}