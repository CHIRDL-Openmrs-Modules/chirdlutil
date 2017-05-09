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

import org.openmrs.module.chirdlutil.util.IOUtil;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

/**
 * This class reads a csv file and reassigns mlm priorities accordingly. It expects name and new_priority column. The name is the token name of the rule
 */
public class UpdateMLMPriorities {
	
	/**
	 *
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			if (args == null || args.length < 2) {
				return;
			}
			
			//The last argument is the csv file
			//The preceeding arguments are the directories to search for mlms
			String newPrioritiesFile = args[args.length - 1];
			ArrayList<File> parentDirectories = new ArrayList<File>();
			for (int i = 0; i < args.length - 1; i++) {
				try {
					parentDirectories.add(new File(args[i]));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			processFile(parentDirectories, new File(newPrioritiesFile));
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Make sure the new priorities files exists and is in the correct format
	public static void processFile(ArrayList<File> parentDirectories, File newPrioritiesFile) throws FileNotFoundException, IOException {
		if (!newPrioritiesFile.exists()) {
			return;
		}
		
		if (newPrioritiesFile.getName().endsWith(".csv")) {
			updatePriorities(parentDirectories, newPrioritiesFile);
		}
	}
	
	//Look for each mlm file listed in the csv file and update its priority
	public static void updatePriorities(ArrayList<File> parentDirectories, File newPrioritiesFile) throws FileNotFoundException, IOException {
		
		List<PriorityDescriptor> prioritiesToFix = getPriorityInfo(new FileInputStream(newPrioritiesFile));
		File result = null;
		//look through each entry in the csv file
		for (PriorityDescriptor priorityDescriptor : prioritiesToFix) {
			String newPriority = priorityDescriptor.getNewPriority();
			String ruleName = priorityDescriptor.getName();
			
			if(!ruleName.endsWith(".mlm")){
				ruleName+=".mlm";
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
				//rewrite the priority line with the new priority
				try {
					BufferedReader reader = new BufferedReader(new FileReader(mlmOldFileName));
					BufferedWriter writer = new BufferedWriter(new FileWriter(mlmNewFileName));
					String line = null;
					
					while ((line = reader.readLine()) != null) {
						
						//look for the write sections
						if (line.toLowerCase().indexOf("priority:") > -1) {
							writer.write("Priority: " + newPriority + ";;\n");
						} else {
							writer.write(line+"\n");
							writer.flush();
						}
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
		if(directory.getPath().contains("_retired")){
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
	
	//parse the csv file to get a list of PriorityDescriptor objects for each of the rows
	private static List<PriorityDescriptor> getPriorityInfo(InputStream inputStream) throws FileNotFoundException,
	                                                                                IOException {
		
		List<PriorityDescriptor> list = null;
		try {
			InputStreamReader inStreamReader = new InputStreamReader(inputStream);
			CSVReader reader = new CSVReader(inStreamReader, ',');
			HeaderColumnNameTranslateMappingStrategy<PriorityDescriptor> strat = new HeaderColumnNameTranslateMappingStrategy<PriorityDescriptor>();
			
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("name", "name");
			map.put("new_priority", "newPriority");
			
			strat.setType(PriorityDescriptor.class);
			strat.setColumnMapping(map);
			
			CsvToBean<PriorityDescriptor> csv = new CsvToBean<PriorityDescriptor>();
			list = csv.parse(strat, reader);
			
			if (list == null) {
				return new ArrayList<PriorityDescriptor>();
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
