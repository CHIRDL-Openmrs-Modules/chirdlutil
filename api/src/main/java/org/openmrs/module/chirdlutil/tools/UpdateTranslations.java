/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chirdlutil.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmrs.module.chirdlutil.util.IOUtil;

/**
 * This class reads a txt file of rules with content for PSF translations and updates the mlms to
 * include it.
 */
public class UpdateTranslations {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			if (args == null || args.length < 2) {
				System.err.println("A minimum number of two arguments (a rule directory and a txt file) are required.");
				return;
			}
			
			//The last argument is the txt file
			//The preceeding arguments are the directories to search for mlms
			String storeNotesFile = args[args.length - 1];
			ArrayList<File> parentDirectories = new ArrayList<File>();
			for (int i = 0; i < args.length - 1; i++) {
				try {
					parentDirectories.add(new File(args[i]));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			processFile(parentDirectories, new File(storeNotesFile));
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Make sure the new translation file exists and is in the correct format
	public static void processFile(ArrayList<File> parentDirectories, File storeNotesFile) throws FileNotFoundException,
	    IOException {
		if (!storeNotesFile.exists()) {
			System.err.print("File: " + storeNotesFile.getPath() + " does not exist.");
			return;
		}
		
		if (storeNotesFile.getName().endsWith(".txt")) {
			addTranslation(parentDirectories, storeNotesFile);
		}
	}
	
	//Look for each mlm file listed in the txt file and update the translation
	public static void addTranslation(ArrayList<File> parentDirectories, File storeNotesFile) throws FileNotFoundException,
	    IOException {
		
		List<TranslationDescriptor> translations = getTranslationInfo(new FileReader(storeNotesFile));
		File result = null;
		int lineNum = 1;
		//look through each entry in the csv file
		for (TranslationDescriptor translationDescriptor : translations) {
			lineNum++;
			String fileName = translationDescriptor.getFileName();
			String translation = translationDescriptor.getTranslation();
			String english = translationDescriptor.getEnglish();
			
			if (fileName == null || fileName.length() == 0 || translation == null || translation.length() == 0) {
				System.err.print("Line " + lineNum + " was skipped because the filname name or translations were invalid.");
				continue;//skip because there is not enough content for a translation
			}
			
			//look for the mlm file
			for (File currParentDirectory : parentDirectories) {
				result = searchDirectoryForFile(currParentDirectory, fileName);
				if (result != null) {
					System.out.println(result.toString());
					break;
				}
			}
			
			if (result == null) {
				System.out.println("Could not find file " + fileName);
			} else {
				String mlmOldFileName = result.getPath();
				String mlmNewFileName = mlmOldFileName + "new";
				//write the storeNote in the proper place
				try {
					BufferedReader reader = new BufferedReader(new FileReader(mlmOldFileName));
					BufferedWriter writer = new BufferedWriter(new FileWriter(mlmNewFileName));
					String line = null;
					
					while ((line = reader.readLine()) != null) {
						
						if (line.toLowerCase().contains("write") && !line.contains("At Spanish")) {
							line = "write (\"" + english + "\");";
						} else {
							//Look for the line with the spanish translation
							if (line.contains("At Spanish")) {
								line = "write (\"" + translation + "\") At Spanish;";
							}
						}
						writer.write(line + "\n");
						
						writer.flush();
					}
					
					writer.close();
					reader.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				try {
					//update the old file and remove the temp file
					IOUtil.copyFile(mlmNewFileName, mlmOldFileName);
					IOUtil.deleteFile(mlmNewFileName);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//recursively search directories for the file
	private static File searchDirectoryForFile(File directory, String filename) {
		
		//don't search the retired directory
		if (directory.getPath().contains("_retired")) {
			return null;
		}
		
		File file = new File(directory, filename);
		
		if (file.exists()) {
			return file;
		}
		
		File[] files = directory.listFiles();
		
		for (File currFile : files) {
			if (currFile.isDirectory()) {
				File result = searchDirectoryForFile(currFile, filename);
				
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}
	
	//parse the txt file to get a list of TranslationDescriptor objects for each of the rows
	private static List<TranslationDescriptor> getTranslationInfo(FileReader fileReader) throws FileNotFoundException,
	    IOException {
		List<TranslationDescriptor> list = new ArrayList<TranslationDescriptor>();
		BufferedReader br = new BufferedReader(fileReader);
		try {
			String line = br.readLine();
			String fileName = "";
			String translation = "";
			String english = "";
			
			while (line != null) {
				int index = line.indexOf("Filename:");
				if (index > -1) {
					fileName = line.substring(index + "Filename:".length()).trim();
				}
				index = line.indexOf("English:");
				
				if (index > -1) {
					english = line.substring(index + "English:".length()).trim();
				}
				index = line.indexOf("Update:");
				if (index > -1) {
					translation = line.substring(index + "Update:".length()).trim();
					list.add(new TranslationDescriptor(fileName, translation, english));
					
				}
				
				line = br.readLine();
			}
		}
		finally {
			br.close();
		}
		return list;
	}
	
}
