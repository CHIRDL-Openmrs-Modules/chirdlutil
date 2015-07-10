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

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlPrintJobRunnable;

/**
 * Print job Runnable for PDF files.
 * 
 * @author Steve McKee
 */
public class PDFPrintRunnable implements ChirdlPrintJobRunnable {
	
	private static Log log = LogFactory.getLog(PDFPrintRunnable.class);
	
	private String printerName;
	private String pdfLocation;
	
	/**
	 * Constructor method
	 * 
	 * @param printerName The name of the printer to print the job.
	 * @param pdfLocation The location of the PDF file to print.
	 */
	public PDFPrintRunnable(String printerName, String pdfLocation) {
		this.printerName = printerName;
		this.pdfLocation = pdfLocation;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Context.openSession();
		try {
			AdministrationService adminService = Context.getAdministrationService();
			Context.authenticate(adminService
					.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_USERNAME), adminService
					.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_PASSWORD));
			File pdfFile = new File(pdfLocation);
			PrintServices.printPDFFileSynchronous(printerName, pdfFile);
		}
		catch (IllegalArgumentException e) {
			log.error("Invalid parameter print PDF file " + pdfLocation + " to printer " + printerName, e);
		}
		catch (Exception e) {
			log.error("Unknown error occurred printing PDF file " + pdfLocation + " to printer " + printerName, e);
		}
		finally {
			Context.closeSession();
		}
	}
	
	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlPrintJobRunnable#getPrinterName()
	 */
	public String getPrinterName() {
		return printerName;
	}

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlPrintJobRunnable#getPDFFileLocation()
	 */
    public String getPDFFileLocation() {
	    return pdfLocation;
    }
}
