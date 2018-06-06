package org.openmrs.module.chirdlutil.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "module/chirdlutil/memoryLeakMonitor.form")
public class MemoryLeakMonitorController{
	
    /** Form view name */
    private static final String FORM_VIEW = "/module/chirdlutil/memoryLeakMonitor";

    @RequestMapping(method = RequestMethod.GET)
    protected String initForm(ModelMap modelMap)
    {
        return FORM_VIEW;
    }

}
