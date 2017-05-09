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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


/**
 *
 * @author Steve McKee
 */
public class LogMonitor {
	
	private final String LATEST_DATE = "latestDate";
	private final String LAST_FILE_SIZE = "lastFileSize";
	private final String FILE_TO_MONITOR = "fileToMonitor";
	private final String SEARCH_STRING = "searchString";
	private final String PATH_TO_PV = "pathToPV";
	private final String PROCESS_TO_KILL = "processToKill";
	private final String APP_TO_START = "appToStart";
	
    public boolean searchLog(File propFile) {
    	
    	File logFile = new File("logMonitor.log");
		FileWriter logWriter = null;
		try {
			logWriter = new FileWriter(logFile, true);
			Properties props = new Properties();
			props.load(new FileInputStream(propFile));
			
			// Get the file to search.
			String searchFileStr = props.getProperty(FILE_TO_MONITOR);
			if (searchFileStr == null) {
				logWriter.write("No value set for required property fileToMonitor.\n");
				return false;
			}
			
			File searchFile = new File(searchFileStr);
			if (!searchFile.exists() || !searchFile.canRead()) {
				logWriter.write("The provided file for the property fileToMonitor cannot be found read: " + searchFileStr + 
					"\n");
				return false;
			}
			
			if (searchFile.isDirectory()) {
				logWriter.write("The provided file for the property fileToMonitor cannot be a directory: " + searchFileStr + 
					"\n");
				return false;
			}
			
			// Get the string to find.
			String searchString = props.getProperty(SEARCH_STRING);
			if (searchString == null || searchString.trim().length() == 0) {
				logWriter.write("The value for property searchString cannot be null or empty.\n");
				return false;
			}
			
			// Check the file size.  If it has not changed, there's no reason to continue checking.
			long fileSize = searchFile.length();
			String lastFileSizeStr = props.getProperty(LAST_FILE_SIZE);
			if (lastFileSizeStr != null) {
				long lastFileSize = Long.parseLong(lastFileSizeStr);
				if (fileSize == lastFileSize) {
					return true;
				}
			}
				
			// Get the saved latest error found
			String latestDateStr = props.getProperty(LATEST_DATE);
			BufferedReader fileReader = null;
			try {
				fileReader = new BufferedReader(new FileReader(searchFile));
				String line;
				boolean error = false;
				String dateStr = null;
				DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
	            while ((line = fileReader.readLine()) != null) {
	            	if (line.contains(searchString)) {
	            		dateStr = line.substring(0, 22);
	            		try {
	            			if (latestDateStr == null) {
	            				// This is the first error found.
	            				error = true;
	            			} else {
	            				// Check to see if the date is later than the stored date.
	            				Date date = formatter.parse(dateStr);
	    	                    Date latestDate = formatter.parse(latestDateStr);
	    	                    if (latestDate.compareTo(date) < 0) {
	    	                    	error = true;
	    	                    }
	            			}
	                    }
	                    catch (ParseException e) {
		                    String errorStr = exceptionToString(e);
		                    logWriter.write(errorStr + "\n");
	                    }
	            	}
	            }
	            
	            FileWriter writer = null;
	            try {
	            	writer = new FileWriter(propFile);
		            if (error) {
		            	// We've found a new error.  Get the process to kill.
		            	logWriter.write("New " + searchString + " encountered on " + dateStr + ".\n");
		            	String processToKill = props.getProperty(PROCESS_TO_KILL);
		            	if (processToKill == null || processToKill.trim().length() == 0) {
		            		logWriter.write("The property processToKill cannot be null or empty.\n");
		        			return false;
		            	}
		            	
		            	// Get the application to kill the process.
		            	String pathToPV = props.getProperty(PATH_TO_PV);
		            	if (pathToPV == null || pathToPV.trim().length() == 0) {
		            		logWriter.write("The property pathToPV cannot be null or empty.\n");
		        			return false;
		            	}
		            	
		            	File PVFile = new File(pathToPV);
		            	if (!PVFile.exists() || !PVFile.canExecute()) {
		            		logWriter.write("The value for property pathToPV cannot be found or cannot be executed: " + 
		            			pathToPV + "\n");
		        			return false;
		            	}
		            	
		            	// Kill the application
		            	logWriter.write("Killing process " + processToKill + "...");
		            	Runtime.getRuntime().exec(pathToPV + " -kf " + processToKill);
		            	logWriter.write("complete\n");
		            	
		            	// Start the application
		            	String appToStartStr = props.getProperty(APP_TO_START);
		            	if (appToStartStr == null || appToStartStr.trim().length() == 0) {
		            		logWriter.write("The property appToStart cannot be null or empty.\n");
		        			return false;
		            	}
		            	
		            	File appToStart = new File(appToStartStr);
		            	if (!appToStart.exists() || !appToStart.canExecute()) {
		            		logWriter.write("The value for property appToStart cannot be found or cannot be executed: " + 
		            			appToStartStr + "\n");
		        			return false;
		            	}
		            	
		            	// Sleep for a period of time to allow the application to properly shutdown.
		            	try {
		                    Thread.sleep(5000);
	                    }
	                    catch (InterruptedException e) {
	                    	String errorStr = exceptionToString(e);
		                    logWriter.write(errorStr + "\n");
	                    }
		            	
	                    logWriter.write("Starting application: " + appToStartStr + "...");
		            	Runtime.getRuntime().exec(appToStartStr);
		            	logWriter.write("complete\n");
		            	
		            	// Log the date for comparison the next time around.
		            	props.setProperty(LATEST_DATE, dateStr);
		            }
		            
		            // Save the file size for comparison the next time around.
		            props.setProperty(LAST_FILE_SIZE, String.valueOf(fileSize));
	            } finally {
	            	if (writer != null) {
	            		props.store(writer, null);
	            		writer.close();
	            	}
	            }
			} finally {
				if (fileReader != null) {
					fileReader.close();
				}
			} 
		} catch (Exception e) {
			String errorStr = exceptionToString(e);
            try {
	            logWriter.write(errorStr + "\n");
            }
            catch (IOException e1) {
	            e.printStackTrace();
            }
		} finally {
			if (logWriter != null) {
				try {
	                logWriter.flush();
	                logWriter.close();
                }
                catch (IOException e) {
	                e.printStackTrace();
                }
			}
		}
		
		return true;
	}
    
    private void printUsage() {
		System.out.println("Arguments:");
		System.out.println("Arg 1: The location of the preference file");
		System.out.println("");
		System.out.println("Usage:");
		System.out.println("LogMonitor C:\\temp\\logMonitor.prop");
	}
    
    private String exceptionToString(Exception e) {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	e.printStackTrace(pw);
    	return sw.toString();
    }
	
	/**
	 * Auto generated method comment
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		LogMonitor logMonitor = new LogMonitor();
		if (args.length != 1) {
			logMonitor.printUsage();
			System.exit(1);
		}
		
		String propFileStr = args[0];
		File propFile = new File(propFileStr);
        boolean success = logMonitor.searchLog(propFile);
        if (success) {
        	System.exit(0);
        }
        
        System.exit(1);
	}
	
}