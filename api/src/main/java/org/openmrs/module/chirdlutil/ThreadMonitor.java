/**
 * 
 */
package org.openmrs.module.chirdlutil;

import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;

/**
 * @author tmdugan
 */
public class ThreadMonitor implements Runnable {
	
	private static Log log = LogFactory.getLog(ThreadMonitor.class);
	
	private static Hashtable<Integer, Vector<Thread>> waitingHighPriorityThreads = new Hashtable<Integer, Vector<Thread>>();
	
	private static Hashtable<Integer, Vector<Thread>> activeHighPriorityThreads = new Hashtable<Integer, Vector<Thread>>();
	
	private static Hashtable<Integer, Vector<Thread>> waitingLowPriorityThreads = new Hashtable<Integer, Vector<Thread>>();
	
	private static Hashtable<Integer, Vector<Thread>> activeLowPriorityThreads = new Hashtable<Integer, Vector<Thread>>();
	
	private static ReadWriteManager waitingHighPriorityThreadsLock = new ReadWriteManager();
	
	private static ReadWriteManager waitingLowPriorityThreadsLock = new ReadWriteManager();
	
	private static ReadWriteManager activeHighPriorityThreadsLock = new ReadWriteManager();
	
	private static ReadWriteManager activeLowPriorityThreadsLock = new ReadWriteManager();
	
	public ThreadMonitor() {
	}
	
	private static void startThread(ReadWriteManager waitingThreadsLock, Integer locationId, Thread thread,
	                                             Hashtable<Integer, Vector<Thread>> waitingThreads, Integer priority) {
		try {
			
			waitingThreadsLock.getReadLock();
			Vector<Thread> threads = waitingThreads.get(locationId);
			waitingThreadsLock.releaseReadLock();
			
			if (threads == null) {
				waitingThreadsLock.getWriteLock();
				waitingThreads.put(locationId, new Vector<Thread>());
				waitingThreadsLock.releaseWriteLock();
			}
			waitingThreadsLock.getReadLock();
			threads = waitingThreads.get(locationId);
			waitingThreadsLock.releaseReadLock();
			
			thread.setPriority(priority);
			
			// thread is added
			waitingThreadsLock.getWriteLock();
			threads.add(thread);
			waitingThreadsLock.releaseWriteLock();
			
		}
		catch (Exception e) {
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
	}
	
	public static void startHighPriorityThread(Integer locationId, Thread thread) {
		Integer priority = Thread.NORM_PRIORITY + 1;
		startThread(waitingHighPriorityThreadsLock, locationId, thread, waitingHighPriorityThreads, priority);
	}
	
	public static void startLowPriorityThread(Integer locationId, Thread thread) {
		Integer priority = Thread.NORM_PRIORITY;
		startThread(waitingLowPriorityThreadsLock, locationId, thread, waitingLowPriorityThreads, priority);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Context.openSession();
		int loops = 0;
		
		try {
			AdministrationService adminService = Context.getAdministrationService();
			Context.authenticate(adminService.getGlobalProperty("scheduler.username"), adminService
			        .getGlobalProperty("scheduler.password"));
			
			while (true == true) {
				threadMonitor();
				Thread.sleep(100);//check each tenth of a second
				
				if (loops % 50 == 0) {
					logThreadCounts();
				}
				loops++;
			}
		}
		catch (Exception e) {
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		finally {
			Context.closeSession();
		}
		
	}
	
	private static void logThreadCounts() {
		log.info("Total number of active high priority threads: ");
		
		activeHighPriorityThreadsLock.getReadLock();
		for (Integer locationId : activeHighPriorityThreads.keySet()) {
			Vector<Thread> locationActiveThreads = activeHighPriorityThreads.get(locationId);
			if (locationActiveThreads != null) {
				log.info("Location id: " + locationId + " size: " + locationActiveThreads.size());
			}
		}
		activeHighPriorityThreadsLock.releaseReadLock();
		
		activeLowPriorityThreadsLock.getReadLock();
		log.info("Total number of active low priority threads: ");
		for (Integer locationId : activeLowPriorityThreads.keySet()) {
			Vector<Thread> locationActiveThreads = activeLowPriorityThreads.get(locationId);
			if (locationActiveThreads != null) {
				log.info("Location id: " + locationId + " size: " + locationActiveThreads.size());
			}
		}
		activeLowPriorityThreadsLock.releaseReadLock();
		
		waitingHighPriorityThreadsLock.getReadLock();
		log.info("Total number of waiting high priority threads: ");
		for (Integer locationId : waitingHighPriorityThreads.keySet()) {
			Vector<Thread> locationActiveThreads = waitingHighPriorityThreads.get(locationId);
			if (locationActiveThreads != null) {
				log.info("Location id: " + locationId + " size: " + locationActiveThreads.size());
			}
		}
		waitingHighPriorityThreadsLock.releaseReadLock();
		
		waitingLowPriorityThreadsLock.getReadLock();
		log.info("Total number of waiting low priority threads: ");
		for (Integer locationId : waitingLowPriorityThreads.keySet()) {
			Vector<Thread> locationActiveThreads = waitingLowPriorityThreads.get(locationId);
			if (locationActiveThreads != null) {
				log.info("Location id: " + locationId + " size: " + locationActiveThreads.size());
			}
		}
		waitingLowPriorityThreadsLock.releaseReadLock();
	}
	
	private void removeInactiveThreads(Hashtable<Integer, Vector<Thread>> activeThreads, ReadWriteManager activeThreadsLock) {
		
		activeThreadsLock.getReadLock();
		Set<Integer> locationIds = activeThreads.keySet();
		activeThreadsLock.releaseReadLock();
		
		for (Integer locationId : locationIds) {
			
			activeThreadsLock.getReadLock();
			Vector<Thread> locationActiveThreads = activeThreads.get(locationId);
			activeThreadsLock.releaseReadLock();
			
			if (locationActiveThreads != null) {
				for (Thread currThread : locationActiveThreads) {
					if (!currThread.isAlive()) {
						activeThreadsLock.getWriteLock();
						locationActiveThreads.remove(currThread);
						activeThreadsLock.releaseWriteLock();
					}
				}
			}
		}
	}
	
	private void activateThreads(Hashtable<Integer, Vector<Thread>> activeThreads,
	                             Hashtable<Integer, Vector<Thread>> waitingThreads, 
	                             Integer maxActiveThreadsByLocation,
	                             ReadWriteManager activeThreadsLock,
	                             ReadWriteManager waitingThreadsLock) {
		
		waitingThreadsLock.getReadLock();
		Set<Integer> locationIds = waitingThreads.keySet();
		waitingThreadsLock.releaseReadLock();
		
		for (Integer locationId : locationIds) {
			
			activeThreadsLock.getReadLock();
			Vector<Thread> activeThreadsByLocation = activeThreads.get(locationId);
			activeThreadsLock.releaseReadLock();
			
			waitingThreadsLock.getReadLock();
			Vector<Thread> waitingThreadsByLocation = waitingThreads.get(locationId);
			waitingThreadsLock.releaseReadLock();
			
			if (activeThreadsByLocation == null) {
				activeThreadsByLocation = new Vector<Thread>();
			}
			if (activeThreadsByLocation.size() < maxActiveThreadsByLocation) {
				
				for (Thread currThread : waitingThreadsByLocation) {
					if (activeThreadsByLocation.size() < maxActiveThreadsByLocation) {
						currThread.start();
						
						activeThreadsLock.getWriteLock();
						activeThreadsByLocation.add(currThread);
						activeThreadsLock.releaseWriteLock();
						
						waitingThreadsLock.getWriteLock();
						waitingThreadsByLocation.remove(currThread);
						waitingThreadsLock.releaseWriteLock();
					}
				}
			}
			
		}
	}
	
	private void threadMonitor() {
		
		final int maxHighPriorByLocation = 1;
		final int maxLowPriorByLocation = 1;
		
		try {
			removeInactiveThreads(activeHighPriorityThreads,activeHighPriorityThreadsLock);
			removeInactiveThreads(activeLowPriorityThreads,activeLowPriorityThreadsLock);
			
			activateThreads(activeHighPriorityThreads, waitingHighPriorityThreads, maxHighPriorByLocation,
				activeHighPriorityThreadsLock,waitingHighPriorityThreadsLock);
			activateThreads(activeLowPriorityThreads, waitingLowPriorityThreads, maxLowPriorByLocation,
				activeLowPriorityThreadsLock,waitingLowPriorityThreadsLock);
		}
		catch (Exception e) {
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
	}
}
