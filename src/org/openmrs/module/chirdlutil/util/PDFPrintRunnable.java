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
import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlPrintJobRunnable;
import org.openmrs.module.chirdlutil.util.PrintServices;

/**
 * Print job Runnable for PDF files.
 * 
 * @author Steve McKee
 */
public class PDFPrintRunnable implements ChirdlPrintJobRunnable {
	
	private static Log log = LogFactory.getLog(PDFPrintRunnable.class);
	
	private String printerName;
	private String printJobName;
	private String pdfLocation;
	
	/**
	 * Constructor method
	 * 
	 * @param printerName The name of the printer to print the job.
	 * @param printJobName The name of the print job.
	 * @param pdfLocation The location of the PDF file to print.
	 */
	public PDFPrintRunnable(String printerName, String printJobName, String pdfLocation) {
		this.printerName = printerName;
		this.printJobName = printJobName;
		this.pdfLocation = pdfLocation;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			File pdfFile = new File(pdfLocation);
			PrintServices.printPDFFileSynchronous(printerName, printJobName, pdfFile);
		}
		catch (PrinterException e) {
			log.error("Error printing PDF file " + pdfLocation + " to printer " + printerName, e);
		}
		catch (IOException e) {
			log.error("Error loading PDF file to print " + pdfLocation + " to printer " + printerName, e);
		}
		catch (IllegalArgumentException e) {
			log.error("Invalid parameter print PDF file " + pdfLocation + " to printer " + printerName, e);
		}
		catch (Exception e) {
			log.error("Unknown error occurred printing PDF file " + pdfLocation + " to printer " + printerName, e);
		}
	}
	
	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlPrintJobRunnable#getPrinterName()
	 */
	public String getPrinterName() {
		return printerName;
	}
	
	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlPrintJobRunnable#getPrintJobName()
	 */
	public String getPrintJobName() {
		return printJobName;
	}
	
}
