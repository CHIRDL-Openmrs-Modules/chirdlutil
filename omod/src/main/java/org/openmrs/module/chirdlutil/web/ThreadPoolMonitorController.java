package org.openmrs.module.chirdlutil.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.springframework.web.servlet.mvc.SimpleFormController;


public class ThreadPoolMonitorController extends SimpleFormController {
	
	private Log log = LogFactory.getLog(this.getClass());

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
	
	@SuppressWarnings("rawtypes")
    @Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		AdministrationService adminService = Context.getAdministrationService();
		Integer refreshRate = 10;
		String refreshRateStr = adminService.getGlobalProperty("chirdlutil.threadMonitorRefreshRate");
		if (refreshRateStr != null) {
			try {
				refreshRate = Integer.parseInt(refreshRateStr);
			} catch (NumberFormatException e) {
				log.error("Error parsing the data in the chirdlutil.threadMonitorRefreshRate global property.  The " +
						"refresh rate is being defaulted to 10 seconds.", e);
				refreshRate = 10;
			}
		} else {
			log.error("The global property chirdlutil.threadMonitorRefreshRate is not specified.  It is being defaulted " + 
				"to 10 seconds.");
		}
		
		map.put("refreshRate", refreshRate);
		
		return map;
	}
}
