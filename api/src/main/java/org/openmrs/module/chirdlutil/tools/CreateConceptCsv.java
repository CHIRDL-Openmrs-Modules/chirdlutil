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
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * This class parses mlm files and creates a csv file with all concepts stored via storeObs in the mlm.
 */
public class CreateConceptCsv {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			if (args == null || args.length < 2) {
				System.err.println(
						"A minimum number of two arguments (a rule directory and an output filename) are required.");
				return;
			}

			// The last argument is the csv file
			// The preceeding arguments are the directories to search for mlms
			String outputFileName = args[args.length - 1];
			ArrayList<File> parentDirectories = new ArrayList<File>();
			for (int i = 0; i < args.length - 1; i++) {
				try {
					parentDirectories.add(new File(args[i]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			processFile(parentDirectories, new File(outputFileName));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * create the list of concepts and write to a csv file
	 * 
	 * @param parentDirectories
	 * @param outputFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void processFile(ArrayList<File> parentDirectories, File outputFile)
			throws FileNotFoundException, IOException {

		if (outputFile.getName().endsWith(".csv")) {
			File[] directories = new File[parentDirectories.size()];
			directories = parentDirectories.toArray(directories);

			HashSet<ConceptPair> set = new HashSet<ConceptPair>();
			createConceptFile(directories, set);
			exportConcepts(outputFile, set);
		} else {
			System.err.println("Error writing to " + outputFile);
		}
	}

	/**
	 * Create ConceptPair objects containing storeObs values
	 * @param files
	 * @param set
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void createConceptFile(File[] files, HashSet<ConceptPair> set)
			throws FileNotFoundException, IOException {
		Pattern storeObsPattern = Pattern.compile("CALL\\s*storeObs\\s*With\\s*\"(.+)\"\\s*,\\s*\"(.+)\"\\s*;",
				Pattern.CASE_INSENSITIVE);

		// loop through the mlms
		for (File file : files) {

			String currFilename = file.getName();

			// skip the retired folder
			if (currFilename.contains("retired")) {
				continue;
			}

			if (file.isDirectory()) {

				createConceptFile(file.listFiles(), set);
				continue;
			}

			String mlmFilename = file.getPath();
			if (!mlmFilename.endsWith("mlm")) {
				continue;
			}
			BufferedReader reader = new BufferedReader(new FileReader(mlmFilename));

			// process each line of the mlm looking for storeObs
			try {
				String line = null;
				while ((line = reader.readLine()) != null) {

					if (line.trim().length() == 0) {
						continue;
					}

					// create a set of storeObs
					Matcher m = storeObsPattern.matcher(line);
					boolean matches = m.find();

					if (matches) {
						ConceptPair conceptPair = new ConceptPair(m.group(1), m.group(2));
						set.add(conceptPair);

					}

				}

				

			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 *  Write the concepts to a csv file
	 * @param outputFile
	 * @param set
	 * @throws IOException
	 */
	public static void exportConcepts(File outputFile, HashSet<ConceptPair> set) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		CSVWriter csvWriter = null;
		try {
			csvWriter = new CSVWriter(writer);
			String[] columnNames = new String[6];

			columnNames[0] = "name";
			columnNames[1] = "concept class";
			columnNames[2] = "datatype";
			columnNames[3] = "description";
			columnNames[4] = "units";
			columnNames[5] = "parent concept";
			csvWriter.writeNext(columnNames);
			HashSet<String> questionNames = new HashSet<String>();
			
			//write the question answer pairs
			for (ConceptPair conceptPair : set) {

				String question = conceptPair.getQuestionConceptName();
				String answer = conceptPair.getAnswerConceptName();

				questionNames.add(question);

				String[] item = new String[6];
				item[0] = answer;
				item[1] = "CHICA";
				item[2] = "Coded";
				item[3] = answer;
				item[4] = null;
				item[5] = question;
				csvWriter.writeNext(item);
				csvWriter.flush();
			}

			//write all the question concepts
			for (String question : questionNames) {

				String[] item = new String[6];
				item[0] = question;
				item[1] = "CHICA";
				item[2] = "Coded";
				item[3] = null;
				item[4] = null;
				item[5] = null;
				csvWriter.writeNext(item);
				csvWriter.flush();
			}
			

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}finally{
			try {
				csvWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
