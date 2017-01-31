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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class reads a csv file and reassigns mlm priorities accordingly. It expects name and
 * new_priority column. The name is the token name of the rule
 */
public class ConvertRules {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			if (args == null || args.length < 2) {
				return;
			}
			File[] parentDirectories = new File[args.length - 1];
			for (int i = 0; i < args.length - 1; i++) {
				try {
					parentDirectories[i] = new File(args[i]);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			String outputDirectory = args[args.length - 1];
			updateMLMs(parentDirectories, outputDirectory);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Look for each mlm file listed in the csv file and update its priority
	public static void updateMLMs(File[] files, String outputDirectory) throws FileNotFoundException, IOException {
		
		//look for the mlm file
		for (File file : files) {
			
			if (!file.getAbsolutePath().equals(outputDirectory)) {
				if (file.isDirectory()) {
					if(!file.getName().contains("retired")){
						updateMLMs(file.listFiles(), outputDirectory);
					}
				} else {
					processFile(file, outputDirectory);
				}
			}
		}
		
	}
	
	/**
	 * Auto generated method comment
	 * 
	 * @param file
	 */
	private static void processFile(File file, String outputPath) {
		String oldFileName = file.getPath();
		
		if (!oldFileName.endsWith("mlm")) {
			return;
		}
		String directory = file.getParentFile().getName();
		
		File outputDirectory = new File(outputPath + "/" + directory);
		
		outputDirectory.mkdirs();
		
		String newFileName = outputDirectory + "\\" + file.getName();
		
		System.out.println("Converting " + oldFileName + "...");
		Pattern p = null;
		Matcher m = null;
		boolean matches = false;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(oldFileName));
			BufferedWriter writer = new BufferedWriter(new FileWriter(newFileName));
			String line = null;
			Boolean inLogicSection = false;
			Boolean inDataSection = false;
			Boolean inActionSection = false;
			String result = "";
			String extraVariables = "";
			String logicExtraVariables = "";
			int openIf = 0;
			while ((line = reader.readLine()) != null) {
				
				if (line.trim().length() == 0||line.trim().startsWith("/*")){
					result+=line+"\n";
					continue;
				}
				
				//add value for validation
				if (line.toLowerCase().indexOf("validation:") > -1) {
					line = "Validation: testing;;";
				}
				
				//remove everything but actual date from date field
				if (line.toLowerCase().indexOf("date:") > -1) {
					int index = line.indexOf("T");
					if (index > 0) {
						line = line.substring(0, index) + ";;";
					} else {
						
						//replaces time if T divider is missing
						p = Pattern.compile("(\\s*Date:\\s*\\d\\d\\d\\d-\\d\\d-\\d\\d)(\\s+\\d\\d:\\d\\d:\\d\\d)");
						m = p.matcher(line);
						matches = m.find();
						
						if (matches) {
							
							line = m.replaceFirst("$1");
						}
					}
				}
				
				//make sure all open ifs are closed before consume mode
				if (line.toLowerCase().contains("mode = consume")) {
					if (openIf > 0) {
						while (openIf > 0) {
							result = result + "\nendif;\n";
							openIf--;
						}
					}
				}
				
				//make sure endif has a semicolon
				if (line.toLowerCase().indexOf("endif") > -1) {
					if (openIf > 0) {
						int index = line.indexOf(";");
						if (index == -1) {
							line = line + ";";
						}
					} else {
						line = ""; //ignore the line
					}
				}
				
				p = Pattern.compile("(.+)(\\|\\|\\s+)(\\w+\\s+)(\\|\\|\\s+=)(.+)");
				m = p.matcher(line);
				matches = m.matches();
				
				//convert vertical pipes to proper assignment
				if (matches) {
					
					line = m.replaceFirst("$1$3:=$5");
					if(openIf>0){
						line = line + "\n" + "endif;";
					}
				}
				
				p = Pattern.compile("\\s+[Cc][Oo][Nn][Cc][Ll][Uu][Dd][Ee]\\s+");
				m = p.matcher(line);
				matches = m.find();
				
				//add endif after conclude
				if (matches) {
					
					p = Pattern.compile(".*[Ee][Ll][Ss][Ee]\\s+([Cc][Oo][Nn][Cc][Ll][Uu][Dd][Ee]\\s+.*)");
					m = p.matcher(line);
					matches = m.matches();
					
					//convert Else conclude <boolean>; endif; --> conclude <boolean>;
					if (matches) {
						
						line = m.replaceFirst("$1");
					} else {
						if(openIf>0){
							line = line + "\n" + "endif;";
						}
					}
				}
				
				//remove age_min and move to Data section
				if (line.toLowerCase().indexOf("age_min:") > -1) {
					extraVariables += line.replaceFirst(":", ":=").replace(";;", ";") + "\n";
					line = "";
				}
				
				//remove age max and move to Data section
				if (line.toLowerCase().indexOf("age_max:") > -1) {
					extraVariables += line.replaceFirst(":", ":=").replace(";;", ";") + "\n";
					line = "";
				}
				
				//replace "is in" with "in"
				line = line.replaceAll("is in", "in");
				
				if (line.toLowerCase().indexOf("data:") > -1) {
					inDataSection = true;
				}
				
				if (line.toLowerCase().indexOf("logic:") > -1) {
					inLogicSection = true;
					inDataSection = false;
				}

				if (line.toLowerCase().indexOf("action:") > -1) {
					inActionSection = true;
					inLogicSection = false;
				}
				
				if (inLogicSection) {
					//look for calls in the logic section
					if(line.trim().toLowerCase().startsWith("call")){
						line = "temp:="+line.trim();
					}
					
					p = Pattern.compile("(\\w+\\s*=\\s*)(no)(\\))");
					m = p.matcher(line);
					matches = m.find();
					
					//make sure reserved word "no" has quotes around it
					if (matches) {
						
						line = m.replaceAll("$1\"$2\"$3");
					}
					//make sure all open ifs are closed before end of logic section
					if (line.toLowerCase().contains(";;")) {
						if (openIf > 0) {
							while (openIf > 0) {
								result = result + "\nendif;\n";
								openIf--;
							}
						}
					}
				} else {
					//look for calls that already have a variable assignment
					p = Pattern.compile(".+:=\\s*[Cc][Aa][Ll][Ll]\\s+.+");
					m = p.matcher(line);
					matches = m.find();
					
					//move calls with assignments in the action section to the logic section
					if (matches) {
						logicExtraVariables += line + "\n";
						continue;
					}
				}
				
				//fix missing semicolon after Explanation
				if (line.indexOf("Explanation:") > -1 || line.indexOf("Purpose:") > -1) {
					line = line.replaceAll(";", "");
					line = line + ";;";
				}
				
				//fix read Last X {}
				p = Pattern.compile("(.*)([Rr][Ee][Aa][Dd].+[Ll]ast.+\\d+\\s+)(\\{.*)");
				m = p.matcher(line);
				matches = m.find();
				
				if (matches) {
					line = m.replaceFirst("$1$2from $3");
				}
				
				p = Pattern.compile("\\{(.*)\\}");
				m = p.matcher(line);
				matches = m.find();
				
				//make sure datasource has from
				if (matches) {
					
					if (!m.group(1).toLowerCase().contains("from")) {
						line = m.replaceFirst("{$1 from CHICA}");
					}
				}
				
				p = Pattern.compile("Priority:\\s*(\\w*)\\s*");
				m = p.matcher(line);
				matches = m.find();
				
				//remove blank priorities
				if (matches) {
					if (m.group(1).trim().length() == 0) {
						line = "";
					}
				}
				
				p = Pattern.compile("\\s*[Ii]f.*'.*then");
				m = p.matcher(line);
				matches = m.find();
				//replace single quotes with double quotes in If statements
				if (matches) {
					line = line.substring(0, m.end()).replaceAll("'", "\"") + line.substring(m.end());
				}
				
				//count open if statements
				p = Pattern.compile("\\s*[Ii]f.*then");
				m = p.matcher(line);
				matches = m.find();
				
				if (matches&&(inDataSection||inLogicSection||inActionSection)) {
					openIf++;
				}
				if (line.contains("endif")) {
					openIf--;
				}
				
				result += line + "\n";
			}
			
			//add rule type to Data section
			if (result.indexOf("PSF") > -1) {
				extraVariables += "Rule_Type:= PSF;\n";
			} else {
				
				if (result.indexOf("PWS") > -1) {
					extraVariables += "Rule_Type:= PWS;\n";
				}
			}
			
			//Add firstname to Data section, if needed
			if (result.indexOf("firstname") > -1) {
				extraVariables += "firstname:= call firstName;\n";
			}
			
			//Add Gender to Data section, if needed
			if (result.indexOf("Gender") > -1 || result.indexOf("hisher") > -1) {
				extraVariables += "Gender:= read Last {gender from person};\n";
			}
			//Add hisher to Data section, if needed
			if (result.indexOf("hisher") > -1) {
				//only add if not already there
				p = Pattern.compile("hisher\\s*:=");
				m = p.matcher(result);
				if (!m.find()) {
					extraVariables += "If (Gender = M) then hisher := \"his\";\n" + "endif;\n"
					        + "If (Gender = F) then hisher := \"her\";\n" + "endif;\n";
				}
			}
			
			//add extra variables to the data section
			int index = result.indexOf("Data:");
			result = result.substring(0, index + "Data:".length()) + "\n" + extraVariables + "\n"
			        + result.substring(index + "Data:".length(), result.length());
			
			//add extra variables to the logic section
			if (logicExtraVariables.length() > 0) {
				index = result.indexOf("Logic:");
				result = result.substring(0, index + "Logic:".length()) + "\n" + logicExtraVariables + "\n"
				        + result.substring(index + "Logic:".length(), result.length());
			}
			
			//remove empty If then statements
			p = Pattern.compile("If[^\\n\\r]+?then\\s+?endif", Pattern.DOTALL);
			m = p.matcher(result);
			while (m.find()) {
				result = m.replaceFirst("");
				m = p.matcher(result);
			}
			
			writer.write(result);
			writer.flush();
			writer.close();
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
