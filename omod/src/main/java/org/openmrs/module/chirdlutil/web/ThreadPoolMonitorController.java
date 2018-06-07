package org.openmrs.module.chirdlutil.web;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ThreadPoolMonitorController {
	
	private static final Log LOG = LogFactory.getLog(ThreadPoolMonitorController.class);
	
	/** Form view name */
	private static final String THREAD_POOL_FORM_VIEW = "/module/chirdlutil/threadPoolMonitor";
    private static final String PRINTER_THREAD_POOL_FORM_VIEW = "/module/chirdlutil/printerThreadPoolMonitor";
    
    private static final String PARAM_REFRESH_RATE = "refreshRate";


	@RequestMapping(value = "module/chirdlutil/threadPoolMonitor.form", method = RequestMethod.GET) 
    protected String initThreadPoolForm(HttpServletRequest request, ModelMap map) throws Exception{
        initForm(map);
        return THREAD_POOL_FORM_VIEW;
    }
	
	@RequestMapping(value = "module/chirdlutil/printerThreadPoolMonitor.form", method = RequestMethod.GET) 
    protected String initPrinterThreadPoolForm(HttpServletRequest request, ModelMap map) throws Exception{
        initForm(map);
        return PRINTER_THREAD_POOL_FORM_VIEW;
    }
	
	private void initForm(ModelMap map) throws Exception {
		AdministrationService adminService = Context.getAdministrationService();
		Integer refreshRate = 10;
		String refreshRateStr = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_THREAD_POOL_MONITOR_REFRESH_RATE);
		if (refreshRateStr != null) {
			try {
				refreshRate = Integer.parseInt(refreshRateStr);
			} catch (NumberFormatException e) {
			    LOG.error("Error parsing the data in the " + ChirdlUtilConstants.GLOBAL_PROP_THREAD_POOL_MONITOR_REFRESH_RATE + " global property.  The " +
						"refresh rate is being defaulted to 10 seconds.", e);
				refreshRate = 10;
			}
		} else {
		    LOG.error("The global property " + ChirdlUtilConstants.GLOBAL_PROP_THREAD_POOL_MONITOR_REFRESH_RATE + " is not specified.  It is being defaulted " + 
				"to 10 seconds.");
		}
		
		map.put(PARAM_REFRESH_RATE, refreshRate);
	}
}
