package org.openmrs.module.chirdlutil.tools;

import java.io.File;


public class ChangeFileExtensions {
	
	public void changeExtensions(File directory, String origExt, String newExt) {
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				changeExtensions(file, origExt, newExt);
			}
			
			String filename = file.getName();
			if (filename.endsWith(origExt)) {
				String subName = filename.substring(0, filename.lastIndexOf("."));
				String newName = subName + newExt;
				file.renameTo(new File(file.getParentFile(), newName));
			}
		}
	}
	
	private void printUsage() {
		System.out.println("Arguments:");
		System.out.println("Arg 1: The directory to check (Note: this program is recursive and will apply to all " +
				"sub-directories.");
		System.out.println("Arg 2: The original file extension");
		System.out.println("Arg 3: The new file extension");
		System.out.println("");
		System.out.println("Usage:");
		System.out.println("ChangeFileExtensions C:\temp 20 xml");
		System.out.println("This will change the extension of files with the extension of 20 to xml in the " +
				"C:\temp directory");
	}
	
	/**
	 * Auto generated method comment
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ChangeFileExtensions changeExt = new ChangeFileExtensions();
		if (args.length != 3) {
			changeExt.printUsage();
			System.exit(1);
		}
		
		String directoryStr = args[0];
		String origExt = args[1];
		String newExt = args[2];
		
		File directory = new File(directoryStr);
		if (!directory.exists()) {
			System.out.println("The directory " + directoryStr + " does not exist");
			System.out.println("");
			changeExt.printUsage();
			System.exit(1);
		} else if (!directory.isDirectory()) {
			System.out.println("The directory " + directoryStr + " is not a directory");
			System.out.println("");
			changeExt.printUsage();
			System.exit(1);
		}
		
		if (!origExt.startsWith(".")) {
			origExt = "." + origExt;
		}
		
		if (!newExt.startsWith(".")) {
			newExt = "." + newExt;
		}
		
		changeExt.changeExtensions(directory, origExt, newExt);
		
		System.exit(0);
	}
	
}
