package org.openmrs.module.chirdlutil.db.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.chirdlutil.db.ChirdlUtilDAO;
import org.openmrs.module.chirdlutil.hibernateBeans.EventLog;

/**
 * Hibernate implementations of ChirdlUtil related database functions.
 * 
 * @author Tammy Dugan
 */
public class HibernateChirdlUtilDAO implements ChirdlUtilDAO {

	
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * Empty constructor
	 */
	public HibernateChirdlUtilDAO() {
	}
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public EventLog logEvent(EventLog eventLog) {
		sessionFactory.getCurrentSession().saveOrUpdate(eventLog);
		return eventLog;
    }

    public List<EventLog> getEventLogs(Integer eventId, Integer locationId, Integer formId, Integer studyId, String event,
                                       Date startDate, Date endDate, Integer userId, String description) {
    	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EventLog.class).addOrder(Order.desc("eventTime"));
    	if (eventId != null) {
    		criteria.add(Restrictions.eq("eventId", eventId));
    	}
    	if (locationId != null) {
    		criteria.add(Restrictions.eq("locationId", locationId));
    	}
    	if (formId != null) {
    		criteria.add(Restrictions.eq("formId", formId));
    	}
    	if (studyId != null) {
    		criteria.add(Restrictions.eq("studyId", studyId));
    	}
    	if (startDate != null && endDate != null) {
    		criteria.add(Restrictions.between("eventTime", startDate, endDate));
    	} else {
    		if (startDate != null) {
    			criteria.add(Restrictions.ge("eventTime", startDate));
    		} else if (endDate != null) {
    			criteria.add(Restrictions.le("eventTime", endDate));
    		}
    	}
    	if (event != null) {
    		criteria.add(Restrictions.like("event", event));
    	}
    	if (userId != null) {
    		criteria.add(Restrictions.eq("userId", userId));
    	}
    	if (description != null) {
    		criteria.add(Restrictions.like("description", description, MatchMode.ANYWHERE));
    	}
		
		List<EventLog> logs = criteria.list();
		if (logs == null) {
			logs = new ArrayList<EventLog>();
		}
		
		return logs;
    }
}

