package org.openmrs.module.chirdlutil.log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.hibernateBeans.EventLog;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;

/**
 * Utility class used to assist with the logging functionality.
 *
 * @author Steve McKee
 */
public class LoggingUtil {

	/**
	 * 
	 * Creates an EventLog object from the parameters specified.
	 * 
	 * @param locationId Location ID.  This can be null;
	 * @param formId Form ID.  This can be null.
	 * @param studyId Study ID.  This can be null.
	 * @param event Event type.  This cannot be null.  Constants are found in the LoggingContants file.
	 * @param userId The user creating the event.  This cannot be null.
	 * @param description Additional information about the event.  This can be null.
	 * 
	 * @return EventLog object injected with the provided data.
	 */
	public static EventLog logEvent(Integer locationId, Integer formId, Integer studyId, 
	                                      String event, Integer userId, String description) {
		if (event == null) {
			throw new IllegalArgumentException("The event parameter cannot be null.");
		} else if (userId == null) {
			throw new IllegalArgumentException("The userId parameter cannot be null.");
		}
		
		EventLog eventLog = new EventLog();
		eventLog.setLocationId(locationId);
		eventLog.setFormId(formId);
		eventLog.setStudyId(studyId);
		eventLog.setEvent(event);
		eventLog.setEventTime(new Date());
		eventLog.setUserId(userId);
		eventLog.setDescription(description);
		
		ChirdlUtilService chirdlService = Context.getService(ChirdlUtilService.class);
		chirdlService.logEvent(eventLog);
		
		return eventLog;
	}
	
	/**
	 * Returns all possible logging event types found in the LoggingConstants class.
	 * 
	 * @return List of event type names as Strings.
	 * 
	 * @throws IllegalAccessException
	 */
	public static List<String> getAllEvents() throws IllegalAccessException{
		Field[] fields = LoggingConstants.class.getDeclaredFields();
		if (fields == null) {
			return new ArrayList<String>();
		}
		
		List<String> returnFields = new ArrayList<String>();
		for (Field field : fields) {
			String name = field.getName();
			if (name.startsWith("EVENT_")) {
				returnFields.add(field.get(LoggingConstants.class).toString());
			}
		}
		
		Collections.sort(returnFields);
		return returnFields;
	}
}
