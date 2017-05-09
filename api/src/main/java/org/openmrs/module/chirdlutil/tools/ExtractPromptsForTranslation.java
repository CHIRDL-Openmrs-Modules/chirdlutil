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
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * This class creates a csv file with mlm filename, English prompts, and Spanish prompt
 */
public class ExtractPromptsForTranslation {
	
	/**
	 * Auto generated method comment
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			if (args == null) {
				return;
			}
			
			//the first argument is the output csv file
			//subsequent arguments are input file directories
			FileOutputStream output = new FileOutputStream(args[0]);
			output.write("Rule name,English prompt, Spanish prompt\n".getBytes());
			
			if (args.length > 1) {
				for (int i = 1; i < args.length; i++) {
					
					processFile(new File(args[i]), output);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void processFile(File file, OutputStream output) {
		if (!file.exists()) {
			return;
		}
		
		if (file.isDirectory()) {
			//ignore retired rules
			if (file.getName().indexOf("retired") > -1) {
				return;
			}
			File[] files = file.listFiles();
			for (File currFile : files) {
				processFile(currFile, output);
			}
		} else {
			//only process mlm files
			if (file.getName().endsWith(".mlm")) {
				extractPromptsForTranslation(file, output);
			}
		}
	}
	
	public static void extractPromptsForTranslation(File file, OutputStream output) {
		ArrayList<String> prompts = new ArrayList<String>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				//look for the write sections
				if (line.toLowerCase().indexOf("write") > -1 && line.toLowerCase().indexOf("\"") > -1) {
					prompts.add(line);
				}
			}
			//only process mlms with a Spanish translation
			if (prompts.size() == 2 && prompts.get(1).toLowerCase().indexOf("spanish") > -1) {
				output.write(file.getName().getBytes());
				
				output.write(",\"".getBytes());
				output.write(formatString(prompts.get(0)).getBytes());
				output.write("\",\"".getBytes());
				output.write(formatString(prompts.get(1)).getBytes());
				output.write("\"\n".getBytes());
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * pull out the prompt text between the quotes
	 */
	public static String formatString(String str1) {
		str1 = str1.substring(str1.indexOf('"') + 1);
		str1 = str1.substring(0, str1.indexOf('"'));
		
		return str1;
	}
}
