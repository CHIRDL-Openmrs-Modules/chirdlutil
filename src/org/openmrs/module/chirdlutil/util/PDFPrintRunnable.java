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


/**
 * Thread used to do auto-printing of PDF files.
 * 
 * @author Steve McKee
 */
public class PDFPrintRunnable implements Runnable {
	
	private static Log log = LogFactory.getLog(PDFPrintRunnable.class);
	private String printerName;
	private String jobName;
	private File pdfFile;
	
	/**
	 * Constructor method
	 * 
	 * @param printerName The printer where the print job will be sent.
	 * @param jobName The name of the printer job.  This can be null.
	 * @param pdfFile The PDF file to print.
	 */
	public PDFPrintRunnable(String printerName, String jobName, File pdfFile) {
		this.printerName = printerName;
		this.jobName = jobName;
		this.pdfFile = pdfFile;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
    public void run() {
	    try {
	    	PrintServices.printPDFFileSynchronous(printerName, jobName, pdfFile);
        }
        catch (PrinterException e) {
            log.error("Error printing PDF file " + pdfFile.getAbsolutePath() + " to printer " + printerName, e);
        } 
	    catch (IOException e) {
            log.error("Error loading PDF file to print " + pdfFile.getAbsolutePath(), e);
        }
	    catch (IllegalArgumentException e) {
            log.error("Invalid parameter print PDF file " + pdfFile.getAbsolutePath(), e);
        }
    }
}
