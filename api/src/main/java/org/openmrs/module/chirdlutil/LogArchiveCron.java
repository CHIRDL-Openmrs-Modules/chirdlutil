/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 *
 */

package org.openmrs.module.chirdlutil;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * <p>
 * This class mainly archives the Tomcat log files (move the old files from %TOMCAT_HOME%\logs or
 * $TOMCAT_HOME/logs directory to the archive-directory.    Based on the same logic, the archived files
 * in archive-directory will be deleted if those files exceed the retention time.
 * </p> <p>
 * This class will be scheduled to run once a week on weekend night so as to avoid the clinic peak time.
 * </p> <p>
 * The retention time for the original log files and the archived files are default to seven and fourteen days
 * respectively.  Both are configurable via the logRetentionDays and archiveRetentionDays schedule task
 * properties respectively.
 * </p> <p>
 * The Archive directory can also be configured via the schedule task property of archiveDirectory.
 * </p> <p>
 * The configuration hierarchy is:  Variables can be defined in System domain, global (OpenMRS) domain, 
 *  or scheduler-task level,  and variables defined in scheduler-task domain have the highest priority.
 */
public class LogArchiveCron extends AbstractTask 
{	
	/**
	 *  For logging messages.
	 */
	private Log log = LogFactory.getLog(this.getClass());
	
	private String archiveDir = null;              
	private String logDir = null;
	private int logRetentionDays = 7;                                    
	private int archiveRetentionDays = logRetentionDays * 2 ; 

	
	/* (non-Javadoc)
	 * @see org.openmrs.scheduler.tasks.AbstractTask#initialize(org.openmrs.scheduler.TaskDefinition)
	 */
	@Override
	public void initialize(TaskDefinition config)
	{
	    super.initialize(config);
		Context.openSession();
		
		try 
		{
		    init(config);
		} catch (Exception e) 
		{
		    this.log.info("Failed to initialize Cron job for archiving the logs, please check error logs.");
		    this.log.error("Initialization failed {" + e.getMessage() + "}");
		    this.log.error(Util.getStackTrace(e));
		}
		
		Context.closeSession();
	}


	/* (non-Javadoc)
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() 
	{
		Context.openSession();

		try
		{
			Date lastRunDate = GregorianCalendar.getInstance().getTime();
			
			archiveFiles();
			
			log.info("LogArchiveCron job last run on: " + lastRunDate.toString());
		} catch (Exception e)
		{
            this.log.error(e.getMessage());
            this.log.error(Util.getStackTrace(e));
		} finally 
		{
			Context.closeSession();
		}	
	}

	
	/**
	 *  Archive the log files into the specified location.
	 */
	private void archiveFiles() 
	{
    	  deleteOldFiles(archiveDir, null, archiveRetentionDays);
    	  deleteOldFiles(logDir, archiveDir, logRetentionDays);
	}
	
	
	/**
	 * Delete the old files from the specified directory (for instance, the archive directory or logfile directory)
	 * @param fromDir - The directory where old files are to be archive/deleted.
	 * @param toDir    -  The directory where old files are to be deleted.
	 * @param retention - defines which files are to be deleted, which are to be retained.
	 */
    private void deleteOldFiles(String fromDir, String toDir, int retention) {
        File files[] = null;
        String filePath = null;
        Calendar cutoff = GregorianCalendar.getInstance();
        cutoff.add(GregorianCalendar.DAY_OF_YEAR, (-1 * retention));

        if (fromDir == null || fromDir.equals("")) 
            this.log.error("Please make sure both logfile and archivefile directories are defined either in global, task, or system scope.");

        files = (new File(fromDir)).listFiles();

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					if (files[i].lastModified() < cutoff.getTimeInMillis()) {
						filePath = files[i].getName();
						if (toDir != null)
							IOUtil
							        .renameFile(files[i].getPath(), new File(archiveDir + File.separator + filePath)
							                .getPath());
						else
							IOUtil.deleteFile(files[i].getPath());
					}
				}
			}
		}
    }
	
	
	/**
	 * Initialize the variables and check the archiving directory existence..
	 * 
	 * @param config - TaskDefinition 
	 */
	private void init(TaskDefinition config) throws Exception
	{
	    this.log.info("Initializing Cron job for archiving the logs");
	    
	    AdministrationService adminSvc = Context.getAdministrationService();
        String tempStr;
        File aDir = null;
        
        // Get the original logfile directory
        if( (logDir = config.getProperty("logDirectory")) == null || logDir.equals("") 
                                                                                                    || (aDir = new File(logDir)) == null ||  !aDir.exists() ) 
        {
            if( (logDir = adminSvc.getGlobalProperty("chirdlutil.logDirectory")) == null || logDir.equals("")) 
                logDir = System.getenv("TOMCAT_HOME") + File.separator + "logs";
                         
            if ( (aDir = new File(logDir)) == null || !aDir.exists() )
                log.error("Need to define logDirectory in task, global or system domain without typo.");
        }
        
        // Get or create Archive Directory      
        if( (archiveDir = config.getProperty("archiveDirectory")) == null || archiveDir.equals("") 
                                                                        || (aDir = new File(archiveDir)) == null || (!aDir.exists() && !aDir.mkdirs()) )
        {
            if(  (archiveDir = adminSvc.getGlobalProperty("chirdlutil.archiveDirectory")) == null
                                                                        || (aDir = new File(archiveDir)) == null ||  (!aDir.exists() && !aDir.mkdirs()) )
                log.error("Need to define archiveDirectory in task, global domain without typo.");
        }

        // Get Rention Days if defined
        try 
        {
            if ( (tempStr = config.getProperty("logRetentionDays")) != null 
                    || (tempStr = adminSvc.getGlobalProperty("chirdlutil.logRetentionDays")) != null )
                logRetentionDays = Integer.parseInt(tempStr);

            if ( (tempStr = config.getProperty("archiveRetentionDays")) != null
                    || (tempStr = adminSvc.getGlobalProperty("chirdlutil.archiveRetentionDays")) != null )
                archiveRetentionDays = Integer.parseInt(tempStr);
        } catch(Exception e) {
            ;
        }

        this.log.info("Finished initializing Cron job for archiving the logs");
	}
	
	
	/* (non-Javadoc)
	 * @see org.openmrs.scheduler.tasks.AbstractTask#shutdown()
	 */
	@Override
	public void shutdown()
	{
		super.shutdown();
	}
	
}
