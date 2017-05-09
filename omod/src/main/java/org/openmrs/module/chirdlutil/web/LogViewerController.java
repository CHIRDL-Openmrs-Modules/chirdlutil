package org.openmrs.module.chirdlutil.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.hibernateBeans.EventLog;
import org.openmrs.module.chirdlutil.log.LoggingUtil;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.springframework.web.servlet.mvc.SimpleFormController;


public class LogViewerController extends SimpleFormController {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		return "testing";
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		LocationService locService = Context.getLocationService();
		map.put("locations", locService.getAllLocations(false));
		FormService formService = Context.getFormService();
		map.put("forms", formService.getAllForms(true));
		map.put("events", LoggingUtil.getAllEvents());
		UserService userService = Context.getUserService();
		map.put("users", userService.getAllUsers());
		List<EventLog> eventLogs = getEventLogs(request, map);
		map.put("eventLogs", eventLogs);
		
		List<Integer> months = new ArrayList<Integer>();
		for (int i = 1; i < 13; i++) {
			months.add(i);
		}
		map.put("months", months);
		
		List<Integer> days = new ArrayList<Integer>();
		for (int i = 1; i < 32; i++) {
			days.add(i);
		}
		map.put("days", days);
		
		Calendar calendar = Calendar.getInstance();
		Integer year = calendar.get(Calendar.YEAR);
		List<Integer> years = new ArrayList<Integer>();
		for (int i = year; i >= (year - 10); i--) {
			years.add(i);
		}
		map.put("years", years);
		
		Integer currentMonth = calendar.get(Calendar.MONTH);
		map.put("currentMonth", currentMonth + 1);
		Integer currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		map.put("currentDay", currentDay);
		Integer currentYear = calendar.get(Calendar.YEAR);
		map.put("currentYear", currentYear);
		String event = request.getParameter("event");
		if (event == null) {
			// Only set these for the initial load of the page.  Event is null
			// on the initial load.
			map.put("initialEndDateMonth", currentMonth + 1);
			map.put("initialEndDateDay", currentDay);
			map.put("initialEndDateYear", currentYear);
		}
		
		return map;
	}
	
	private List<EventLog> getEventLogs(HttpServletRequest request, Map<String, Object> map) {
		ChirdlUtilService chirdlService = Context.getService(ChirdlUtilService.class);
		Integer locationId = null;
		Integer formId = null;
		Integer studyId = null;
		Integer userId = null;
		String event = request.getParameter("event");
		if (event == null) {
			return new ArrayList<EventLog>();
		}
		
		String locationIdStr = request.getParameter("locationId");
		String formIdStr = request.getParameter("formId");
		String studyIdStr = request.getParameter("studyId");
		String userIdStr = request.getParameter("user");
		String description = request.getParameter("description");
		if (locationIdStr != null && !locationIdStr.equals("None Selected")) {
			map.put("initialLocation", locationIdStr);
			locationId = Integer.parseInt(locationIdStr);
		}
		
		if (formIdStr != null && !formIdStr.equals("None Selected")) {
			map.put("initialForm", formIdStr);
			formId = Integer.parseInt(formIdStr);
		}
		
		if (studyIdStr != null && studyIdStr.trim().equals("")) {
			studyIdStr = null;
		} else {
			map.put("initialStudyId", studyIdStr);
			studyId = Integer.parseInt(studyIdStr);
		}
		
		if (event != null && event.equals("None Selected")) {
			event = null;
		} else if (event != null) {
			map.put("initialEvent", event);
		}
		
		if (userIdStr != null && !userIdStr.equals("None Selected")) {
			map.put("initialUser", userIdStr);
			userId = Integer.parseInt(userIdStr);
		}
		
		if (description != null && description.trim().equals("")) {
			description = null;
		} else if (description != null) {
			map.put("initialDescription", description);
		}
		
		Date startDate = null;
		Date endDate = null;
		String startDateMonthStr = request.getParameter("startDateMonth");
		map.put("initialStartDateMonth", startDateMonthStr);
		String startDateDayStr = request.getParameter("startDateDay");
		map.put("initialStartDateDay", startDateDayStr);
		String startDateYearStr = request.getParameter("startDateYear");
		map.put("initialStartDateYear", startDateYearStr);
		String endDateMonthStr = request.getParameter("endDateMonth");
		map.put("initialEndDateMonth", endDateMonthStr);
		String endDateDayStr = request.getParameter("endDateDay");
		map.put("initialEndDateDay", endDateDayStr);
		String endDateYearStr = request.getParameter("endDateYear");
		map.put("initialEndDateYear", endDateYearStr);
		if (startDateMonthStr != null && startDateDayStr != null && startDateYearStr != null) {
			Integer startDateMonth = Integer.parseInt(startDateMonthStr);
			Integer startDateDay = Integer.parseInt(startDateDayStr);
			Integer startDateYear = Integer.parseInt(startDateYearStr);
			startDate = new GregorianCalendar(startDateYear, startDateMonth -1, startDateDay, 0, 0, 0).getTime();
		}
		
		if (endDateMonthStr != null && endDateDayStr != null && endDateYearStr != null) {
			Integer endDateMonth = Integer.parseInt(endDateMonthStr);
			Integer endDateDay = Integer.parseInt(endDateDayStr);
			Integer endDateYear = Integer.parseInt(endDateYearStr);
			endDate = new GregorianCalendar(endDateYear, endDateMonth -1, endDateDay, 23, 59, 59).getTime();
		}
		
		return chirdlService.getEventLogs(null, locationId, formId, studyId, event, startDate, endDate, userId, description);
	}
}
