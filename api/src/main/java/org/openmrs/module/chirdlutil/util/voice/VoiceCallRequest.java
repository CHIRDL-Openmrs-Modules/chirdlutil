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
package org.openmrs.module.chirdlutil.util.voice;

import java.util.Date;

import org.openmrs.Person;


/**
 *
 * @author Steve McKee
 */
public class VoiceCallRequest {

	private Person patient;
	private Integer locationId;
	private Date appointmentDate;
	private Date appointmentTime;
	private String phoneNumber;
	
	/**
	 * Constructor method
	 * 
	 * @param patient The patient to be called.
	 * @param locationId The clinic location of the appointment.
	 * @param appointmentDate The date of the appointment.
	 * @param appointmentTime The time of the appointment.
	 */
	public VoiceCallRequest(Person patient, Integer locationId, Date appointmentDate, Date appointmentTime, 
	                        String phoneNumber) {
		this.patient = patient;
		this.locationId = locationId;
		this.appointmentDate = appointmentDate;
		this.appointmentTime = appointmentTime;
		this.phoneNumber = phoneNumber;
		
		if (patient == null) {
			throw new IllegalArgumentException("Patient cannot be null.");
		} else if (locationId == null) {
			throw new IllegalArgumentException("locationId cannot be null");
		} else if (appointmentDate == null) {
			throw new IllegalArgumentException("appointmentDate cannot be null");
		} else if (phoneNumber == null) {
			throw new IllegalArgumentException("phoneNumber cannot be null");
		}
	}
	
	/**
     * @return the patient
     */
    public Person getPatient() {
    	return patient;
    }
	
    /**
     * @return the locationId
     */
    public Integer getLocationId() {
    	return locationId;
    }
	
    /**
     * @return the appointmentDate
     */
    public Date getAppointmentDate() {
    	return appointmentDate;
    }
    
    /**
     * @return the appointmentTime
     */
    public Date getAppointmentTime() {
    	return appointmentTime;
    }
    
    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
    	return phoneNumber;
    }
}
