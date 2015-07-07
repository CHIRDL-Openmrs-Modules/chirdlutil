package org.openmrs.module.chirdlutil.extension.html;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

/**
 * Purpose: Provides links to jsp pages for the module
 * on the administration page of openmrs
 * 
 * @author Tammy Dugan
 *
 */
public class AdminList extends AdministrationSectionExt {

	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	@Override
	public String getTitle() {
		return "chirdlutil.title";
	}
	
	@Override
	public Map<String, String> getLinks() {
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("module/chirdlutil/memoryLeakMonitor.form", "Memory Leak Monitor");
		map.put("module/chirdlutil/logViewer.form", "Event Log Viewer");
		map.put("module/chirdlutil/threadPoolMonitor.form", "Thread Pool Monitor");
		map.put("module/chirdlutil/printerThreadPoolMonitor.form", "Printer Queue Monitor");

		return map;
	}
}
