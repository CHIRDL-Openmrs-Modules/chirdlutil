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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmrs.module.chirdlutil.util.IOUtil;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

/**
 * This class reads a csv file of rules with content for PSF storeNotes and updates the mlms
 * to include it.
 */
public class AddPSFStoreNotes {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			if (args == null || args.length < 2) {
				return;
			}
			
			//The last argument is the csv file
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
	
	//Make sure the new storeNotes file exists and is in the correct format
	public static void processFile(ArrayList<File> parentDirectories, File storeNotesFile) throws FileNotFoundException,
	    IOException {
		if (!storeNotesFile.exists()) {
			return;
		}
		
		if (storeNotesFile.getName().endsWith(".csv")) {
			addStoreNote(parentDirectories, storeNotesFile);
		}
	}
	
	//Look for each mlm file listed in the csv file add the PSF storeNotes
	public static void addStoreNote(ArrayList<File> parentDirectories, File storeNotesFile) throws FileNotFoundException,
	    IOException {
		
		List<NoteContentDescriptor> psfNotes = getNoteInfo(new FileInputStream(storeNotesFile));
		File result = null;
		//look through each entry in the csv file
		for (NoteContentDescriptor noteDescriptor : psfNotes) {
			
			String heading = noteDescriptor.getHeading().trim();
			String noNote = noteDescriptor.getNoNote().trim();
			String yesNote = noteDescriptor.getYesNote().trim();
			String ruleName = noteDescriptor.getRuleName().trim();
			
			if (ruleName == null || ruleName.length() == 0 || heading == null || heading.length() == 0) {
				continue;//skip because there is not enough content for a storeNote
			}
			
			if (!ruleName.endsWith(".mlm")) {
				ruleName += ".mlm";
			}
			
			//look for the mlm file
			for (File currParentDirectory : parentDirectories) {
				result = searchDirectoryForFile(currParentDirectory, ruleName);
				if (result != null) {
					break;
				}
			}
			
			if (result == null) {
				System.out.println("Could not find file " + ruleName);
			} else {
				String mlmOldFileName = result.getPath();
				String mlmNewFileName = mlmOldFileName + "new";
				//write the storeNote in the proper place
				try {
					BufferedReader reader = new BufferedReader(new FileReader(mlmOldFileName));
					BufferedWriter writer = new BufferedWriter(new FileWriter(mlmNewFileName));
					String line = null;
					
					while ((line = reader.readLine()) != null) {
						Pattern p = null;
						Matcher m = null;
						boolean matches = false;
						writer.write(line + "\n");

						//Look for Box1 if statement
						p = Pattern.compile("\\s*[Ii]f.*Box1.*then");
						m = p.matcher(line);
						matches = m.find();
						if (matches) {
							if (yesNote != null && yesNote.length() > 0) {
								writer.write("\tCALL storeNote With \""+yesNote+"\", \""+heading+"\";\n");
							}
						}
						
						//Look for Box2 if statement
						p = Pattern.compile("\\s*[Ii]f.*Box2.*then");
						m = p.matcher(line);
						matches = m.find();
						if (matches) {
							if (noNote != null && noNote.length() > 0) {
								writer.write("\tCALL storeNote With \""+noNote+"\", \""+heading+"\";\n");
							}
						}
						
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
		
		File file = new File(directory.getPath() + File.separator + filename);
		
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
	
	//parse the csv file to get a list of NoteContentDescriptor objects for each of the rows
	private static List<NoteContentDescriptor> getNoteInfo(InputStream inputStream) throws FileNotFoundException,
	    IOException {
		
		List<NoteContentDescriptor> list = null;
		try {
			InputStreamReader inStreamReader = new InputStreamReader(inputStream);
			CSVReader reader = new CSVReader(inStreamReader, ',');
			HeaderColumnNameTranslateMappingStrategy<NoteContentDescriptor> strat = new HeaderColumnNameTranslateMappingStrategy<NoteContentDescriptor>();
			
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("Rule name", "ruleName");
			map.put("Note if Yes", "yesNote");
			map.put("Note if No", "noNote");
			map.put("category/heading", "heading");
			
			strat.setType(NoteContentDescriptor.class);
			strat.setColumnMapping(map);
			
			CsvToBean<NoteContentDescriptor> csv = new CsvToBean<NoteContentDescriptor>();
			list = csv.parse(strat, reader);
			
			if (list == null) {
				return new ArrayList<NoteContentDescriptor>();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			inputStream.close();
		}
		return list;
	}
	
}
