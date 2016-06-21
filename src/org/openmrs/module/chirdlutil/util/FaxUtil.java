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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.jfree.util.Log;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;

import com.biscom.ArrayOfAttachment;
import com.biscom.ArrayOfRecipientInfo;
import com.biscom.Attachment;
import com.biscom.FAXCOMX0020Service;
import com.biscom.FAXCOMX0020ServiceSoap;
import com.biscom.RecipientInfo;
import com.biscom.ResultMessage;
import com.biscom.SenderInfo;

/**
 * Utility class for handling faxes.
 * 
 * @author Steve McKee
 */
public class FaxUtil {

	private static final String PATIENT_NAME_LABEL = "Patient Name: ";
	private static final String MRN_LABEL = "MRN: ";
	private static final String FORM_LABEL = "Form: ";
	private static final int LOGON_THROUGH_USERACCOUNT = 2;
	private static final String EMPTY_STRING = ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING; //shorten name

	/**
	 * Faxes a file.
	 * 
	 * @param fileToFax The file to fax.
	 * @param from Who the fax is from.  This will be displayed on the cover letter.
	 * @param to Who the fax is to.  This will be displayed on the cover letter.
	 * @param faxNumber The number where the fax will be sent.
	 * @param patient The patient for whom the fax is being sent.  This can be null.
	 * @param formName The name of the form being sent.  This will be displayed on the cover letter.  This can be null.
	 * @throws Exception
	 */
	public static void faxFile(File fileToFax, String from, String to, String faxNumber, Patient patient, String formName) 
	throws Exception {
		if (fileToFax == null) {
			throw new IllegalArgumentException("The fileToFax parameter cannot be null.");
		} else if (from == null) {
			throw new IllegalArgumentException("The from parameter cannot be null.");
		} else if (to == null) {
			throw new IllegalArgumentException("The to parameter cannot be null.");
		} else if (faxNumber == null) {
			throw new IllegalArgumentException("The faxNumber parameter cannot be null.");
		}
		
		// Get the fax directory
		try {
			AdministrationService adminService = Context.getAdministrationService();
			String faxDirectory = adminService.getGlobalProperty("chirdlutil.outgoingFaxDirectory");
			if (faxDirectory == null || faxDirectory.trim().length() == 0) {
				String message = "Cannot fax form " + formName
				        + ".  The chirdlutil.outgoingFaxDirectory global property is not set.";
				throw new Exception(message);
			} else if (!(new File(faxDirectory).exists())) {
				String message = "Cannot fax form: " + formName
				        + ".  The chirdlutil.outgoingFaxDirectory cannot be found: " + faxDirectory;
				throw new Exception(message);
			}
			
			// copy the image file to the fax directory
			String name = fileToFax.getName();
			String destination = faxDirectory + File.separator + name;
			IOUtil.copyFile(fileToFax.getAbsolutePath(), destination);
			
			// create the control file
			String controlFilename = UUID.randomUUID().toString() + ".col";
			File controlFile = new File(faxDirectory, controlFilename);
			FileWriter writer = new FileWriter(controlFile);
			String lineSeparator = System.getProperty("line.separator");
			StringBuffer data = new StringBuffer("##filename ");
			data.append(name);
			data.append(lineSeparator);
			data.append("##covername Generic.doc");
			data.append(lineSeparator);
			data.append("##cover");
			data.append(lineSeparator);
			data.append("##from ");
			data.append(from);
			data.append(lineSeparator);
			data.append("##to ");
			data.append(to);
			data.append(lineSeparator);
			if (formName != null) {
				data.append("##message1 Form: " + formName);
				data.append(lineSeparator);
			}
			
			if (patient != null) {
				data.append("##message2 Patient: " + patient.getGivenName() + " " + patient.getFamilyName());
				data.append(lineSeparator);
				PatientIdentifier ident = patient.getPatientIdentifier();
				if (ident != null) {
					data.append("##message3 MRN: " + ident.getIdentifier());
					data.append(lineSeparator);
				}
			}
			
			data.append("##dial ");
			data.append(faxNumber);
			
			try {
				writer.write(data.toString());
			}
			finally {
				writer.flush();
				writer.close();
			}
		}
		catch (Exception e) {
			Log.error("Error faxing file: " + fileToFax, e);
			throw e;
		}
	}
	
	/**
	 * Faxes a file using the FAX COM Anywhere (biscom.com) web service using wsdl generated java classes.  
	 * @param fileToFax - the file that is being faxed.
	 * @param wsdlLocation - wsdl URL of the FAX COM web service
	 * @param faxQueue - use empty string if this is unknown
	 * @param faxNumber - destination fax number
	 * @param userName - web service username
	 * @param password - web service password
	 * @param from - sender information displayed on the the coverpage
	 * @param to - recipient information displayed on the cover page
	 * @param company - recipient company
	 * @param patient - patient for whom this fax was sent
	 * @param formName - displayed on cover page
	 * @param resolution - resolutiion of fax image
	 * @param priority - priority level of fax request
	 * @param sendTime - defines if sending on-peak or off-peak hours
	 * @author Meena Sheley 
	 */
	
	public static void faxFileByWebService(File fileToFax, String wsdlLocation, String faxQueue, String faxNumber, 
			String userName, String password, String from, String to, String company, Patient patient, String formName, int resolution, int priority, String sendTime)
			throws Exception {
		
		if (fileToFax == null) {
			throw new IllegalArgumentException("The 'fileToFax' parameter cannot be null.");
		}else if (wsdlLocation == null){
			throw new IllegalArgumentException("The fax service 'wsdl location' cannot be null.");
		}else if (from == null) {
			throw new IllegalArgumentException("The 'from' parameter cannot be null.");
		} else if (to == null) {
			throw new IllegalArgumentException("The fax 'to' parameter cannot be null.");
		} else if (faxNumber == null) {
			throw new IllegalArgumentException("The 'faxNumber' parameter cannot be null.");
		} else if (userName == null ){
			throw new IllegalArgumentException("The 'userName' parameter cannot be null.");
		}else if (password == null){
			throw new IllegalArgumentException("The 'password' parameter cannot be null.");
		}else if (formName == null){
			throw new IllegalArgumentException("The 'formName' parameter cannot be null.");
		}else if (patient == null){
			throw new IllegalArgumentException("The 'patient' parameter cannot be null.");
		}
		
		if (faxQueue == null){
			//use empty string for default queue
			faxQueue  = EMPTY_STRING;
		}
		
		if (!ChirdlUtilConstants.FAX_SEND_TIME_IMMEDIATE.equals(sendTime) && !ChirdlUtilConstants.FAX_SEND_TIME_OFF_PEAK.equals(sendTime)){
			sendTime = ChirdlUtilConstants.FAX_SEND_TIME_IMMEDIATE;
		}
		
		if (priority < ChirdlUtilConstants.FAX_PRIORITY_LOW || priority > ChirdlUtilConstants.FAX_PRIORITY_URGENT){
			priority = ChirdlUtilConstants.FAX_PRIORITY_NORMAL;
		}
		
		try {
	
			FAXCOMX0020Service service = new FAXCOMX0020Service();
			FAXCOMX0020ServiceSoap port = service.getFAXCOMX0020ServiceSoap();
			
			String subject = FORM_LABEL +  formName;
			String memo = FORM_LABEL +  formName;
			memo += ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED + MRN_LABEL;
			
			//Add patient information to coversheet memo 
			PatientIdentifier ident = patient.getPatientIdentifier();
			if (ident != null){
				memo += ident.getIdentifier();
			}
			
			//Add name to coversheet memo
			memo += ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED +  PATIENT_NAME_LABEL;
			String firstName = patient.getGivenName();
			String lastName = patient.getFamilyName();
			if (!EMPTY_STRING.equals(firstName.trim()) && !EMPTY_STRING.equals(lastName.trim())){
				memo += lastName + ", " + firstName;
			}
				
			//add attachments
			Attachment attachment = new Attachment();
			attachment.setFileName(fileToFax.getName());
			byte[] fileContents;
			try {
				FileInputStream fin = new FileInputStream(fileToFax);
				fileContents = new byte[(int)fileToFax.length()];
				fin.read(fileContents);
				fin.close();
				attachment.setFileContent(fileContents);
			} catch (IOException e) {
				Log.error("Exception reading contents of fax file: " + fileToFax.getName());
			}
			
			ArrayOfAttachment attachments = new ArrayOfAttachment();
			attachments.getAttachment().add(attachment);
			
			//sender
			SenderInfo sender = new SenderInfo();
			sender.setName(from);
			
			//recipient
			ArrayOfRecipientInfo recipients = new ArrayOfRecipientInfo();
			RecipientInfo recInfo = new RecipientInfo();
			recInfo.setName(to);
			recInfo.setFaxNumber(faxNumber);
			recInfo.setCompany(company);
			recipients.getRecipientInfo().add(recInfo);
			
			String idTag = EMPTY_STRING; //optional and unknown at this point
			String coverPage = EMPTY_STRING;  // empty string will use Eskenazi Health default coverpage
			String tsi = EMPTY_STRING; //Transmitting Station ID
			
			ResultMessage rm = port.loginAndSendNewFaxMessage("", userName, password, LOGON_THROUGH_USERACCOUNT, idTag, 
					priority, sendTime, resolution, subject, coverPage,
					memo, sender, recipients, attachments , tsi);
			Log.info("Fax result: " + rm.getDetail());
		
			
		} catch (Exception e) {
			throw e;
		}
	
		
	}
}
