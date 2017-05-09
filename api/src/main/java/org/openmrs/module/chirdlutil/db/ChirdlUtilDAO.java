package org.openmrs.module.chirdlutil.db;

import java.util.Date;
import java.util.List;

import org.openmrs.module.chirdlutil.hibernateBeans.EventLog;
import org.springframework.transaction.annotation.Transactional;

/**
 * ChirdlUtil related database functions
 * 
 * @author Tammy Dugan
 */
@Transactional
public interface ChirdlUtilDAO {
	
	/**
	 * Logs an event to the database.
	 * 
	 * @param eventLog The EventLog data to store in the database.
	 * 
	 * @return The EventLog stored in the database.
	 */
	public EventLog logEvent(EventLog eventLog);
	
	/**
	 * Method to find event logs based on search criteria.  Specify data for any of the parameters to find matches in the
	 * log.  All parameters can be specified as null.
	 *
	 * @param eventId The event ID.
	 * @param locationId The location ID.
	 * @param formId The form ID.
	 * @param studyId The study ID.
	 * @param event The event.  Constants for event can be found in the org.openmrs.module.chirdlutil.log.LoggingConstants 
	 * class.
	 * @param startDate The starting date of the event to search.
	 * @param endDate The ending date of the event to search.
	 * @param userId The ID of the user committing the event.
	 * @param description The description of the event.
	 * 
	 * @return List of EventLog object matching the search criteria.  This will not return null.
	 */
	public List<EventLog> getEventLogs(Integer eventId, Integer locationId, Integer formId, Integer studyId, String event, 
		Date startDate, Date endDate, Integer userId, String description);

	/**
	 * Returns true if the table already exists in the database
	 * @param tableName name of the table to check
	 * @return boolean true if the table exists in the database
	 */
	public boolean tableExists(String tableName);
	
	/**
	 * Executes an sql string
	 * @param sql sql string to execute
	 */
	public void executeSql(String sql);
	
}
