package org.openmrs.module.chirdlutil.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.chirdlutil.db.ChirdlUtilDAO;
import org.openmrs.module.chirdlutil.hibernateBeans.EventLog;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;

/**
 * Defines implementations of services used by this module
 * 
 * @author Tammy Dugan
 */
public class ChirdlUtilServiceImpl implements ChirdlUtilService {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private ChirdlUtilDAO dao;
	
	/**
	 * Empty constructor
	 */
	public ChirdlUtilServiceImpl() {
	}
	
	/**
	 * @return ChirdlUtilDAO
	 */
	public ChirdlUtilDAO getChirdlUtilDAO() {
		return this.dao;
	}
	
	/**
	 * Sets the DAO for this service. The dao allows interaction with the database.
	 * 
	 * @param dao
	 */
	public void setChirdlUtilDAO(ChirdlUtilDAO dao) {
		this.dao = dao;
	}
	
    public EventLog logEvent(EventLog eventLog) {
	    return getChirdlUtilDAO().logEvent(eventLog);
    }

    public List<EventLog> getEventLogs(Integer eventId, Integer locationId, Integer formId, Integer studyId, String event,
                                       Date startDate, Date endDate, Integer userId, String description) {
	    return getChirdlUtilDAO().getEventLogs(eventId, locationId, formId, studyId, event, startDate, 
	    	endDate, userId, description);
    }
    
    public boolean tableExists(String tableName)
	{
		return getChirdlUtilDAO().tableExists(tableName);	
	}
	
	public void executeSql(String sql)
	{
		getChirdlUtilDAO().executeSql(sql);
	}
}
