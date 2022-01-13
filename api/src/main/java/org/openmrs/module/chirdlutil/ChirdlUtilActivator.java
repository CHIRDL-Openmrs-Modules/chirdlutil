package org.openmrs.module.chirdlutil;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.chirdlutil.util.Util;

/**
 * Purpose: Checks that module specific global properties have been set 
 *
 * @author Tammy Dugan
 *
 */
public class ChirdlUtilActivator extends BaseModuleActivator implements DaemonTokenAware {

    private static final Logger log = LoggerFactory.getLogger(ChirdlUtilActivator.class);

	/**
	 * @see org.openmrs.module.BaseModuleActivator#started()
	 */
	@Override
	public void started() {
		log.info("Starting ChirdlUtil Module");
		
		//check that all the required global properties are set
		checkGlobalProperties();
	}

	private void checkGlobalProperties()
	{
		try
		{
			AdministrationService adminService = Context.getAdministrationService();
			
			Iterator<GlobalProperty> properties = adminService
					.getAllGlobalProperties().iterator();
			GlobalProperty currProperty = null;
			String currValue = null;
			String currName = null;

			while (properties.hasNext())
			{
				currProperty = properties.next();
				currName = currProperty.getProperty();
				if (currName.startsWith("chirdlutil"))
				{
					currValue = currProperty.getPropertyValue();
					if (currValue == null || currValue.length() == 0)
					{
					    log.error(String.format("You must set a value for global property: %s", currName));
					}
				}
			}
		} catch (Exception e)
		{
			log.error("Error checking global properties for chirdlutil module");
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));

		}
	}
	
	/**
	 * @see org.openmrs.module.BaseModuleActivator#stopped()
	 */
	@Override
	public void stopped() {
		log.info("Shutting down ChirdlUtil Module");
		
		// CHICA-961 Add calls to shutdown() to allow the application to shutdown faster
		org.openmrs.module.chirdlutil.threadmgmt.ThreadManager.getInstance().shutdown();
		org.openmrs.module.chirdlutil.threadmgmt.PrinterThreadManager.getInstance().shutdown();
	}

	/**
	 * @see org.openmrs.module.DaemonTokenAware#setDaemonToken(org.openmrs.module.DaemonToken)
	 */
	@Override
	public void setDaemonToken(DaemonToken token) {
		Util.setDaemonToken(token);
	}

}
