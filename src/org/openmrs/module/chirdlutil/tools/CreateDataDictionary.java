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
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * This class parses mlm files and creates a csv file containing prompts and leaf content as well as
 * question/answer pairs.
 */
public class CreateDataDictionary {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			if (args == null || args.length < 2) {
				System.err
				        .println("A minimum number of two arguments (a rule directory and an output filename) are required.");
				return;
			}
			
			//The last argument is the csv file
			//The preceeding arguments are the directories to search for mlms
			String outputFileName = args[args.length - 1];
			ArrayList<File> parentDirectories = new ArrayList<File>();
			for (int i = 0; i < args.length - 1; i++) {
				try {
					parentDirectories.add(new File(args[i]));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			processFile(parentDirectories, new File(outputFileName));
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * create the data dictionary and write to a csv file
	 * 
	 * @param parentDirectories
	 * @param outputFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void processFile(ArrayList<File> parentDirectories, File outputFile) throws FileNotFoundException,
	    IOException {
		
		if (outputFile.getName().endsWith(".csv")) {
			File[] directories = new File[parentDirectories.size()];
			directories = parentDirectories.toArray(directories);
			
			List<DataDictionaryDescriptor> list = new ArrayList<DataDictionaryDescriptor>();
			createDataDictionaryFile(directories, list);
			exportDataDictionary(outputFile, list);
		} else {
			System.err.println("Error writing to " + outputFile);
		}
	}
	
	/**
	 * Create DataDictionaryDescriptor objects containing storeObs values, the prompt text, and
	 * corresponding leaf/answer
	 * 
	 * @param files
	 * @param list
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void createDataDictionaryFile(File[] files, List<DataDictionaryDescriptor> list)
	    throws FileNotFoundException, IOException {
		final int NUM_BOXES = 6;
		Pattern storeObsPattern = Pattern.compile("CALL\\s*storeObs\\s*With\\s*\"(.+)\"\\s*,\\s*\"(.+)\"\\s*;",Pattern.CASE_INSENSITIVE);
		Pattern ifBoxPattern = Pattern.compile("if.*Box.*then",Pattern.CASE_INSENSITIVE);
		Pattern writePattern = Pattern.compile("write\\s*\\(\\s*\"(.*)\"\\s*\\)\\s*;",Pattern.CASE_INSENSITIVE);
		
		//loop through the mlms
		for (File file : files) {
			
			String currFilename = file.getName();
			
			//skip the retired folder
			if (currFilename.contains("retired")) {
				continue;
			}
			
			if (file.isDirectory()) {
				
				createDataDictionaryFile(file.listFiles(), list);
				continue;
			}
			
			String mlmFilename = file.getPath();
			if (!mlmFilename.endsWith("mlm")) {
				continue;
			}
			BufferedReader reader = new BufferedReader(new FileReader(mlmFilename));
			
			int writeBoxNum = 1;
			
			//process each line of the mlm looking for storeObs
			try {
				HashMap<String, ArrayList<ConceptPair>> boxObs = new HashMap<String, ArrayList<ConceptPair>>();
				HashMap<String, String> writeMap = new HashMap<String, String>();
				String line = null;
				ArrayList<String> currentBoxes = new ArrayList<String>();
				while ((line = reader.readLine()) != null) {
					
					if (line.trim().length() == 0) {
						continue;
					}
					
					if(line.toLowerCase().trim().startsWith("action:")){
						currentBoxes = new ArrayList<String>();
					}
					
					Matcher m = null;
					boolean matches = false;
					
					//Look for Box* if statement
					m = ifBoxPattern.matcher(line);
					matches = m.find();
					if (matches) {
						currentBoxes = new ArrayList<String>();
						
						//create a list of all boxes in the If statement
						//ignore the box if the logic is NOT Box<i>
						for (int i = 1; i <= NUM_BOXES; i++) {
							Pattern p = Pattern.compile("if.*Box" + i + ".*then",Pattern.CASE_INSENSITIVE);
							m = p.matcher(line);
							matches = m.find();
							if (matches) {
								p = Pattern.compile("if.*NOT.*Box" + i + ".*then",Pattern.CASE_INSENSITIVE);
								m = p.matcher(line);
								matches = m.find();
								if (!matches) {
									currentBoxes.add("Box" + i);
								}
							}
						}
					}
					
					//create a map of storeObs for each box from the If statement
					Matcher m2 = storeObsPattern.matcher(line);
					boolean matches2 = m2.find();
					
					if (matches2) {
						ConceptPair conceptPair = new ConceptPair(m2.group(1), m2.group(2));
						
						for (String currentBox : currentBoxes) {
							ArrayList<ConceptPair> obs = boxObs.get(currentBox);
							if (obs == null) {
								obs = new ArrayList<ConceptPair>();
								boxObs.put(currentBox, obs);
							}
							obs.add(conceptPair);
						}
					}
					
					//create a map of write statements by Box<i> 
					m = writePattern.matcher(line);
					matches = m.find();
					if (matches) {
						if (m.group(1).trim().length() > 0) {
							if (writeMap.keySet().size() == 0) {
								writeMap.put("prompt", m.group(1));
								writeBoxNum--;
							} else {
								writeMap.put("Box" + writeBoxNum, m.group(1));
							}
						}
						writeBoxNum++;
					}
					
				}
				
				reader.close();
				
				String promptText = writeMap.get("prompt");
				
				for (int i = 1; i <= NUM_BOXES; i++) {
					String key = "Box" + i;
					
					String leafText = writeMap.get(key);
					ArrayList<ConceptPair> conceptPairs = boxObs.get(key);
					if (conceptPairs != null) {
						for (ConceptPair currPair : conceptPairs) {
							String question = currPair.getQuestionConceptName();
							String answer = currPair.getAnswerConceptName();
							if (leafText == null && key.equalsIgnoreCase("Box1")) {
								leafText = "yes";
							} else if (leafText == null && key.equalsIgnoreCase("Box2")) {
								leafText = "no";
							}
							if (leafText == null || promptText == null) {
								System.err.println("Error with leaf or prompt text for " + currFilename);
								continue; //this error usually means that there is an error in the rule with storeObs and write alignment
							}
							DataDictionaryDescriptor ddDescriptor = new DataDictionaryDescriptor();
							ddDescriptor.setAnswer(answer);
							ddDescriptor.setFilename(currFilename);
							ddDescriptor.setLeafText(leafText.trim());
							ddDescriptor.setPromptText(promptText.trim());
							ddDescriptor.setQuestion(question);
							list.add(ddDescriptor);
						}
					}
					
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * Write the data dictionary to a csv file
	 * 
	 * @param outputFile
	 * @param list
	 * @throws IOException
	 */
	public static void exportDataDictionary(File outputFile, List<DataDictionaryDescriptor> list) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		CSVWriter csvWriter = null;
		try {
			csvWriter = new CSVWriter(writer);
			String[] columnNames = new String[5];
			columnNames[0] = "rule filename";
			columnNames[1] = "question";
			columnNames[2] = "answer";
			columnNames[3] = "leaf";
			columnNames[4] = "prompt";
			csvWriter.writeNext(columnNames);
			for (DataDictionaryDescriptor dataDictionaryDescriptor : list) {
				String[] item = new String[5];
				item[0] = dataDictionaryDescriptor.getFilename();
				item[1] = dataDictionaryDescriptor.getQuestion();
				item[2] = dataDictionaryDescriptor.getAnswer();
				item[3] = dataDictionaryDescriptor.getLeafText();
				item[4] = dataDictionaryDescriptor.getPromptText();
				csvWriter.writeNext(item);
				csvWriter.flush();
			}
			csvWriter.close();
			
		}
		catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		
	}
}
