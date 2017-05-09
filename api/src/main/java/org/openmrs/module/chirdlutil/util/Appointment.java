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

/**
 * Beaning containg the information provided from the appointment scheduling system.
 *
 * @author Steve McKee
 */
public class Appointment {

	private String encounterId;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String apptDate;
	private String apptTime;
	private String clinicLocation;
	private String mrn;
	private String status;
	
	/**
	 * Constructor Method
	 */
	public Appointment() {
	}

    /**
     * @return the encounterId
     */
    public String getEncounterId() {
    	return encounterId;
    }

    /**
     * @param encounterId the encounterId to set
     */
    public void setEncounterId(String encounterId) {
    	this.encounterId = encounterId;
    }
	
    /**
     * @return the firstName
     */
    public String getFirstName() {
    	return firstName;
    }
	
    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
    	this.firstName = firstName;
    }
	
    /**
     * @return the lastName
     */
    public String getLastName() {
    	return lastName;
    }
	
    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
    	this.lastName = lastName;
    }
	
    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
    	return phoneNumber;
    }
	
    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
    	this.phoneNumber = phoneNumber;
    }
	
    /**
     * @return the apptDate
     */
    public String getApptDate() {
    	return apptDate;
    }
	
    /**
     * @param apptDate the apptDate to set
     */
    public void setApptDate(String apptDate) {
    	this.apptDate = apptDate;
    }
	
    /**
     * @return the apptTime
     */
    public String getApptTime() {
    	return apptTime;
    }
	
    /**
     * @param apptTime the apptTime to set
     */
    public void setApptTime(String apptTime) {
    	this.apptTime = apptTime;
    }
	
    /**
     * @return the clinicLocation
     */
    public String getClinicLocation() {
    	return clinicLocation;
    }
	
    /**
     * @param clinicLocation the clinicLocation to set
     */
    public void setClinicLocation(String clinicLocation) {
    	this.clinicLocation = clinicLocation;
    }
	
    /**
     * @return the mrn
     */
    public String getMrn() {
    	return mrn;
    }
	
    /**
     * @param mrn the mrn to set
     */
    public void setMrn(String mrn) {
    	this.mrn = mrn;
    }
	
    /**
     * @return the status
     */
    public String getStatus() {
    	return status;
    }
	
    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
    	this.status = status;
    }
}
