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
package org.openmrs.module.chirdlutil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.impl.LogicContextImpl;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.module.chirdlutil.util.Appointment;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.voice.VoiceCallRequest;
import org.openmrs.module.chirdlutil.util.voice.VoiceSystemUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ChirdlLocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsConstants.PERSON_TYPE;

/**
 * @author Steve McKee
 * 
 * Scheduled task to make voice calls for upcoming patient appointments.
 */
public class AppointmentVoiceCalls extends AbstractTask {
	
	private Log log = LogFactory.getLog(this.getClass());
	private static final Integer DEFAULT_APPT_TIME_SPAN = 30;
	
	@Override
	public void initialize(TaskDefinition config) {
		super.initialize(config);
		log.info("Initializing appointment call scheduled task.");
	}
	
	@Override
	public void execute() {
		Context.openSession();
		List<CallInfo> callInfo = new ArrayList<CallInfo>();
		try {
			callInfo = getCallInfo();
			if (callInfo.size() == 0) {
				return;
			}
			
			List<VoiceCallRequest> voiceRequests = new ArrayList<VoiceCallRequest>();
			for (CallInfo call : callInfo) {
				VoiceCallRequest request = call.getVoiceCallRequest();
				log.info("Appointment Call Attempt--MRN: " + call.getMRN() + " Phone: " + request.getPhoneNumber());
				voiceRequests.add(request);
			}
			
			// Upload the call data.
			String logCallsOnly = getTaskDefinition().getProperty("logCallsOnly");
			String result = "Successful";
			if (logCallsOnly == null || !logCallsOnly.equalsIgnoreCase("true")) {
				result = VoiceSystemUtil.uploadCallData(voiceRequests);
				log.info("Result of posting call data to voice service: " + result);
			}
			
			if (result != null && result.equalsIgnoreCase("Successful")) {
				// Record an observation stating we made the calls.
				PatientService patientService = Context.getPatientService();
				for (CallInfo call : callInfo) {
					Integer encounterId = call.getEncounterId();
					VoiceCallRequest request = call.getVoiceCallRequest();
					Util.saveObs(patientService.getPatient(request.getPatient().getPersonId()), call.getConceptQuestion(), 
						encounterId, String.valueOf(new Date().getTime()), new Date());
				}
			} else {
				log.error("Calls failed to upload successfully : " + result + ".");
			}
		}
		catch (Throwable e) {
			log.error("Error creating voice call requests", e);
		}
		finally {
			Context.closeSession();
		}
	}
	
	@Override
	public void shutdown() {
		super.shutdown();
		log.info("Shutting down appointment call scheduled task.");
	}
	
	/**
	 * Retrieve all the call requests to be made.
	 * 
	 * @return List of CallInfo objects.
	 */
	private List<CallInfo> getCallInfo() {
		List<CallInfo> callInfo = new ArrayList<CallInfo>();
		callInfo.addAll(getType2DiabetesCallInfo());
		return callInfo;
	}
	
	/**
	 * Retrieve the call information for the Type 2 Diabetes Study.
	 * 
	 * @return List of CallInfo objects.
	 */
	private List<CallInfo> getType2DiabetesCallInfo() {
		List<CallInfo> callInfo = new ArrayList<CallInfo>();
		
		// check to see if Type 2 Diabetes is enabled
		if (!isCallEnabled("typeTwoDiabetes")) {
			log.info("Type 2 Diabetes calls are NOT enabled.  No appointment calls will be made.");
			return callInfo;
		}
		
		log.info("Type 2 Diabetes calls are enabled.  If any appointments are upcoming, calls will be made.");
		ConceptService conceptService = Context.getConceptService();
		
		Concept callMadeConcept = conceptService.getConceptByName("T2DM_Call_Made");
		if (callMadeConcept == null) {
			log.error("No concept found with name: T2DM_Call_Made.  No voice calls will be performed for Type 2 Diabetes.");
			return callInfo;
		}
		
		Concept diabetesDxConcept = conceptService.getConceptByName("T2DM_Workup");
		if (diabetesDxConcept == null) {
			log.error("No concept found with name: T2DM_Rx.  No voice calls will be performed for Type 2 Diabetes.");
			return callInfo;
		}
		
		List<Concept> conceptList = new ArrayList<Concept>();
		conceptList.add(diabetesDxConcept);
		
		Concept callScheduled = conceptService.getConceptByName("Call_Scheduled");
		if (callScheduled == null) {
			log.error("No concept found with name: Call_Scheduled.  No voice calls will be performed for " +
					"Type 2 Diabetes.");
			return callInfo;
		}
		
		List<Concept> answerList = new ArrayList<Concept>();
		answerList.add(callScheduled);
		List<PERSON_TYPE> personTypeList = new ArrayList<PERSON_TYPE>();
		personTypeList.add(PERSON_TYPE.PATIENT);
		String timeSpanStr = getTaskDefinition().getProperty("type2CallSpan");
		Integer timeSpan = null;
		if (timeSpanStr == null || timeSpanStr.trim().length() == 0) {
			log.error("The task property 'type2CallSpan' does not exist.  The default value of " + 
				DEFAULT_APPT_TIME_SPAN + " days will be used");
			timeSpan = DEFAULT_APPT_TIME_SPAN;
		}
		
		try {
			timeSpan = Integer.parseInt(timeSpanStr);
		} catch (NumberFormatException e) {
			log.error("The task property 'type2CallSpan' contains an invalid.  The default value of " + 
				DEFAULT_APPT_TIME_SPAN + " days will be used");
			timeSpan = DEFAULT_APPT_TIME_SPAN;
		}
		
		Calendar startCal = Calendar.getInstance();
		startCal.set(GregorianCalendar.DAY_OF_MONTH, startCal.get(GregorianCalendar.DAY_OF_MONTH) - timeSpan);
		Date startDate = startCal.getTime();
		
		List<Obs> obsList = Context.getObsService().getObservations(null, null, conceptList, answerList, personTypeList, 
			null, null, null, null,
			startDate, new Date(), false);
		if (obsList == null || obsList.size() == 0) {
			return callInfo;
		}
		
		// Filter out voice calls already made.
		for (int i = obsList.size()-1; i >= 0; i--) {
			Obs obs = obsList.get(i);
			LogicContext logicContext = new LogicContextImpl(obs.getPersonId());
			Result callMade = logicContext.read(obs.getPersonId(), logicContext.getLogicDataSource("CHICA"), 
				new LogicCriteriaImpl("T2DM_Call_Made").last());
			if (callMade != null && callMade.exists()) {
				obsList.remove(i);
			}
		}
		
		// See if there are any left after the filtering.
		if (obsList.size() == 0) {
			return callInfo;
		}
		
		List<Appointment> appointments = null;
		try {
	        appointments = Util.getAppointments();
	        log.info("Total appointments in file: " + appointments.size());
        }
        catch (FileNotFoundException e) {
	        log.error("Error retrieving appointments", e);
	        return callInfo;
        }
        catch (IOException e) {
        	log.error("Error retrieving appointments", e);
        	return callInfo;
        }
        
        if (appointments == null || appointments.size() == 0) {
        	return callInfo;
        }
		
        Set<Integer> patientsToCall = new HashSet<Integer>();
        PatientService patientService = Context.getPatientService();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<Integer, String> locToLocNameMap = new HashMap<Integer, String>();
        ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		for (Obs obs : obsList) {
			Integer patientId = obs.getPersonId();
			if (patientsToCall.contains(patientId)) {
				continue;
			}
			
			Patient patient = patientService.getPatient(patientId);
			String mrn = patient.getPatientIdentifier().getIdentifier();
			mrn = mrn.replaceAll("-", "");
			Integer locationId = obs.getLocation().getLocationId();
			String locName = locToLocNameMap.get(locationId);
			if (locName == null) {
				ChirdlLocationAttributeValue lav = backportsService.getLocationAttributeValue(locationId, "clinicAppointmentName");
				if (lav == null || lav.getValue() == null || lav.getValue().trim().length() == 0) {
					log.error("Please specify a location attribute for 'clinicAppointmentName'.");
					continue;
				}
				
				locName = lav.getValue();
				locToLocNameMap.put(locationId, locName);
			}
			
			Appointment appointment = findAppointment(mrn, locName, appointments);
			if (appointment == null) {
				continue;
			}
			
			String phoneNumber = appointment.getPhoneNumber();
			Date appointmentDate = null;
            try {
	            appointmentDate = formatter.parse(appointment.getApptDate());
            }
            catch (ParseException e) {
	            log.error("Error parsing appointment date " + appointment.getApptDate() + " for mrn: " + mrn, e);
	            continue;
            }
            
            Date appointmentTime = null;
            try {
            	appointmentTime = formatter.parse(appointment.getApptTime());
            } catch (ParseException e) {
            	log.error("Error parsing appointment time " + appointment.getApptTime() + " for mrn: " + mrn, e);
            	// Continue even though it fails.  This is not terribly important.
            }
            
			// Add the request.
			VoiceCallRequest request = new VoiceCallRequest(obs.getPerson(), obs.getLocation().getLocationId(), 
				appointmentDate, appointmentTime, phoneNumber);
			
			CallInfo call = new CallInfo(request, obs.getEncounter().getEncounterId(), callMadeConcept, mrn);
			callInfo.add(call);
			patientsToCall.add(patientId);
		}
		
		patientsToCall.clear();
		locToLocNameMap.clear();
		log.info("Type 2 Diabetes has " + callInfo.size() + " patient calls pending.");
		return callInfo;
	}
	
	/**
	 * Searches the upcoming appointments to find a match for a MRN.
	 * 
	 * @param mrn The MRN to find in the appointment list.
	 * @param apptClinicName The String that must be found somewhere in the appointment clinic.
	 * @param appointments List of appointments to search.
	 * @return Appointment object if a match is found, null otherwise.
	 */
	private Appointment findAppointment(String mrn, String apptClinicName, List<Appointment> appointments) {
		for (Appointment appointment : appointments) {
			String apptMrn = appointment.getMrn();
			if (apptMrn != null && apptMrn.contains(mrn) && appointment.getClinicLocation() != null && 
					appointment.getClinicLocation().contains(apptClinicName)) {
				return appointment;
			}
		}
		
		return null;
	}
	
	/**
	 * Checks to see if a specific call is enabled.
	 * 
	 * @param callName The name of call to be made.  This is associated with the "callsToMake" property for the task.
	 * @return true if the call is enabled for the task, false otherwise.
	 */
	private boolean isCallEnabled(String callName) {
		String callsToMake = getTaskDefinition().getProperty("callsToMake");
		if (callsToMake == null || callsToMake.trim().length() == 0) {
			return false;
		}
		
		String[] calls = callsToMake.split(",");
		for (String call : calls) {
			call = call.trim();
			if (callName.equalsIgnoreCase(call)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Class to store the VoiceCallRequest along with ObsAttributeValue that will stored when the voice call information is 
	 * successfully uploaded.
	 *
	 * @author Steve McKee
	 */
	private class CallInfo {
		
		private VoiceCallRequest voiceCallRequest;
		private Integer encounterId;
		private Concept conceptQuestion;
		private String mrn;
		
		/**
		 * Constructor method
		 * 
		 * @param voiceCallRequest The VoiceCallRequest object containing the information for the voice call system.
		 * @param encounterId The encounter ID.
		 * @param conceptQuestion The Concept that will have an answer saved to determine the call was made.
		 * @param mrn The patient MRN identifier.
		 */
		public CallInfo(VoiceCallRequest voiceCallRequest, Integer encounterId, Concept conceptQuestion, String mrn) {
			this.voiceCallRequest = voiceCallRequest;
			this.encounterId = encounterId;
			this.conceptQuestion = conceptQuestion;
			this.mrn = mrn;
		}
		
        /**
         * @return the voiceCallRequest
         */
        public VoiceCallRequest getVoiceCallRequest() {
        	return voiceCallRequest;
        }
		
        /**
         * @return the voiceCallComplete
         */
        public Integer getEncounterId() {
        	return encounterId;
        }
        
        /**
         * @return the conceptQuestion
         */
        public Concept getConceptQuestion() {
        	return conceptQuestion;
        }
        
        /**
         * @return the mrn
         */
        public String getMRN() {
        	return mrn;
        }
	}
}
