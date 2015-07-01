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
package org.openmrs.module.chirdlutil.util;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * Utility class for printing needs.
 *
 * @author Steve McKee
 */
public class PrintServices {

	private static Log log = LogFactory.getLog(PrintServices.class);
	
	/**
	 * Prints the specified file to the specified printer.  This method does perform any rendering for specialized file formats.  
	 * Please test this method with your file type and printer.
	 * 
	 * @param printerName The name of the printer to send the print job.
	 * @param fileToPrint The file to print.
	 * @throws IOException
	 * @throws PrintException
	 */
    public static void printFile(String printerName, File fileToPrint) throws IOException, PrintException {
    	if (printerName == null || printerName.trim().length() == 0) {
    		log.error("A valid printerName parameter was not provided: " + printerName);
    		throw new IllegalArgumentException("A valid printerName parameter was not provided: " + printerName);
    	} else if (fileToPrint == null || !fileToPrint.exists() || !fileToPrint.canRead()) {
    		log.error("A valid fileToPrint parameter was not provided: " + fileToPrint);
    		throw new IllegalArgumentException("A valid printerName parameter was not provided: " + fileToPrint);
    	}
    	
    	PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(new PrinterName(printerName, null)); 
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, printServiceAttributeSet);
        if (printServices == null || printServices.length == 0) {
        	log.error("No printers found for " + printerName);
        	throw new IllegalArgumentException("No printers found for " + printerName);
        }
        
        PrintService selectedService = printServices[0];
        FileInputStream psStream = null;  
        psStream = new FileInputStream(fileToPrint);  
        
        DocPrintJob printJob = selectedService.createPrintJob();
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc document = new SimpleDoc(psStream, flavor, null);
        printJob.print(document, null);
    }
    
    /**
     * Prints a specified PDF file to a specified printer.  This method uses a thread to execute the print job.
     * 
     * @param printerName The name of the printer to use to print the PDF file.
     * @param pdfFile The PDF File to print.
     */
    public static void printPDFFileAsynchronous(String printerName, File pdfFile) {
    	Runnable printRunnable = new PDFPrintRunnable(printerName, pdfFile);
    	Thread printThread = new Thread(printRunnable);
    	printThread.start();
    }
    
    /**
     * Prints a specified PDF file to a specified printer.  This is a synchronous print job.
     * 
     * @param printerName The name of the printer to use to print the PDF file.
     * @param pdfFile The PDF File to print.
     * @throws IOException
     * @throws PrinterException
     */
    public static void printPDFFileSynchronous(String printerName, File pdfFile) throws IOException, PrinterException {
    	if (printerName == null || printerName.trim().length() == 0) {
    		log.error("A valid printerName parameter was not provided: " + printerName);
    		throw new IllegalArgumentException("A valid printerName parameter was not provided: " + printerName);
    	} else if (pdfFile == null || !pdfFile.exists() || !pdfFile.canRead()) {
    		log.error("A valid fileToPrint parameter was not provided (or unable to read): " + pdfFile);
    		throw new IllegalArgumentException("A valid printerName parameter was not provided: " + pdfFile);
    	}
    	
    	PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(new PrinterName(printerName, null)); 
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, printServiceAttributeSet);
        if (printServices == null || printServices.length == 0) {
        	log.error("No printers found for " + printerName);
        	return;
        }
        
        PDDocument document = null;
        try {
	        PrintService selectedService = printServices[0];
	        PrinterJob printJob = PrinterJob.getPrinterJob();
		    printJob.setPrintService(selectedService);
		    document = PDDocument.load(pdfFile);
		    document.silentPrint(printJob);
        } finally {
        	if (document != null) {
        		document.close();
        	}
        }
    }
}
