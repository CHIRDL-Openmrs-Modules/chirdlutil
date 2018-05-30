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

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.PrintServices;


/**
 * Utility used to print a file to a specific printer.
 * 
 * @author Steve McKee
 */
public class PrintFile {
    
    private static final Log LOG = LogFactory.getLog(PrintFile.class);
    
    /**
     * Prints the specified file to the specified printer.  This method does perform any rendering for specialized file formats.  
     * Please test this method with your file type and printer.
     * 
     * @param printerName The name of the printer to send the print job.
     * @param fileToPrint The file to print.
     * @param sumatraExeLocation The location of the SumatraPDF executable.
     * @throws Exception
     */
    public boolean printFile(String printerName, File fileToPrint, String sumatraExeLocation) throws Exception {
        if (fileToPrint.getAbsolutePath().toLowerCase().endsWith(ChirdlUtilConstants.FILE_EXTENSION_PDF)) {
            printPDF(printerName, fileToPrint, sumatraExeLocation);
        } else {
            PrintServices.printFile(printerName, fileToPrint);
        }
        
        return true;
    }
    
    /**
     * Prints the usage for this command line tool.
     */
    private void printUsage() {
        LOG.info("Arguments:");
        LOG.info("Arg 1: The file to print");
        LOG.info("Arg 2: The name of the printer");
        LOG.info("");
        LOG.info("Usage:");
        LOG.info("PrintFile C:\\temp\\test.pdf Clinic_Printer");
        LOG.info("This will print the test.pdf file to the Clinic_Printer printer");
    }
    
    /**
     * Main method
     * 
     * @param args
     */
    public static void main(String[] args) {
        PrintFile printFile = new PrintFile();
        if (args.length != 2 && args.length != 3) {
            printFile.printUsage();
            System.exit(1);
        }
        
        String fileStr = args[0];
        String printerName = args[1];
        String sumatraExeLocation = null;
        if (args.length == 3) {
            sumatraExeLocation = args[2];
        }
        
        try {
            boolean completed = printFile.printFile(printerName, new File(fileStr), sumatraExeLocation);
            if (!completed) {
                System.exit(1);
            }
        }
        catch (Exception e) {
            LOG.error(e);
            System.exit(1);
        }
        
        System.exit(0);
    }
    
    /**
     * Prints a PDF file.
     * 
     * @param printerName The printer where the job will be sent.
     * @param pdfFileToPrint The PDF file to print.
     * @param sumatraExeLocation The location of the SumatraPDF executable.
     */
    private void printPDF(String printerName, File pdfFileToPrint, String sumatraExeLocation) {
        try {
            if (sumatraExeLocation == null || sumatraExeLocation.trim().length() == 0) {
                LOG.error("Unable to print PDF files.  Please specify the location of the SumatraPDF executable.");
                System.exit(1);
            }
            
            ProcessBuilder pb = new ProcessBuilder("\"" + sumatraExeLocation + "\"", "-silent", "-print-to", printerName, 
                "-print-settings", "\"duplexshort\"", pdfFileToPrint.getAbsolutePath());
            Process p = pb.start();
            int exitValue = p.waitFor();
            if (exitValue != 0) {
                LOG.error("The SumatraPDF command returned error code: " + exitValue + " file " + 
                        pdfFileToPrint.getAbsolutePath() + " to printer " + printerName);
                String errorString = IOUtil.output(p.getErrorStream());
                if (errorString.trim().length() > 0) {
                    LOG.error("Error running SumatraPDF for printing file " + 
                            pdfFileToPrint.getAbsolutePath() + " to printer " + printerName + " " + errorString);
                }
                
                System.exit(1);
            }
        } catch (Exception e) {
            LOG.error(e);
            System.exit(1);
        }
    }
}
