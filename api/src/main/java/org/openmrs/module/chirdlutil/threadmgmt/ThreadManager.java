package org.openmrs.module.chirdlutil.threadmgmt;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;

/**
 * Class to manage, order, and execute threads by clinic.  Threads will be executed in 
 * a round-robin style order by clinics to prevent clinic starvation.
 *
 * @author Steve McKee
 */
public class ThreadManager {

	@SuppressWarnings("rawtypes")
    private BlockingQueue poolQueue;
	private ThreadPoolExecutor pool;
	@SuppressWarnings("rawtypes")
    private Map<Integer, LinkedBlockingQueue> locationToQueueMap;
	private boolean shutdown = false;
	private Log log = LogFactory.getLog(this.getClass());
	
	public static final String MAIN_POOL = "Main Pool";
	/**
	 * Location ID for threads not bound to a specific location.
	 */
	public static final Integer NO_LOCATION = -1;
	
	/**
	 * Private constructor
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    private ThreadManager() {
		// Get the thread pool size
		AdministrationService adminService = Context.getAdministrationService();
		String poolSizeStr = adminService.getGlobalProperty("chirdlutil.threadPoolSize");
		if (poolSizeStr == null) {
			throw new IllegalArgumentException("Global property chirdlutil.threadPoolSize not defined.");
		}
		
		int poolSize = Integer.parseInt(poolSizeStr);
		// Create the thread queue/pool
		poolQueue = new PriorityBlockingQueue(poolSize, new ChirdlRunnableComparator());
		pool = new ChirdlUtilThreadPoolExecutor(poolSize, poolSize, 1L, TimeUnit.MILLISECONDS, poolQueue);
		pool.allowCoreThreadTimeOut(true);
		locationToQueueMap = new ConcurrentHashMap(new HashMap<Integer, LinkedBlockingQueue>());
		// Start the thread that continuously loops through the location queues and adds them to the 
		// thread pool to be executed.
//		new Thread(new Runnable() {
//
//            public void run() {
//	            loadQueue();
//            }
//			
//		}).start();
	}
	
	/**
	 * Class to hold the singleton instance of the ThreadManager.
	 *
	 * @author Steve McKee
	 */
	private static class ThreadManagerHolder { 
		public static final ThreadManager INSTANCE = new ThreadManager();
	}
 
	/**
	 * Returns the singleton instance of the ThreadManager.
	 * 
	 * @return ThreadManager
	 */
	public static ThreadManager getInstance() {
		return ThreadManagerHolder.INSTANCE;
	}
	
	/**
	 * Queues the provided Runnable by clinic and executes it when thread pool has an open thread.
	 * 
	 * @param runnable ChirdlRunnable object to execute.
	 * @param locationId Clinic location ID.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void execute(ChirdlRunnable runnable, Integer locationId) {
//		// Find the location specific queue or create one if it can't be found.
//		LinkedBlockingQueue locationQueue = locationToQueueMap.get(locationId);
//		if (locationQueue == null) {
//			synchronized(locationToQueueMap) {
//				locationQueue = new LinkedBlockingQueue();
//				locationToQueueMap.put(locationId, locationQueue);
//			}
//		}
//		
//		locationQueue.add(runnable);
		
		String name = runnable.getName();
		Integer priority = runnable.getPriority();
		try {
			// Add the runnable to the pool so it can eventually get executed.
			pool.execute(runnable);
			log.info("Added the following to the Thread Manager's execution queue - name: " + name + 
				" - priority: " + priority + " - clinic: " + locationId);
		} catch (RejectedExecutionException ree) {
			log.error("Thread Manager no longer accepting new threads.  This thread has been rejected - name: " + 
				name + " - priority: " + priority + " - clinic: " + locationId, ree);
		} catch (Exception e) {
			log.error("Error executing thread - name: " + name + " - priority: " + priority + " - clinic: " + 
				locationId, e);
		}
	}
	
	/**
	 * Shuts down the ThreadManager.  Only threads left in the pool queue will be executed, and 
	 * new threads will be rejected.  It is only advised to use this method on application shutdown.
	 */
	public void shutdown() {
		shutdownManager();
	}
	
	/**
	 * Returns a map containing the usage statistics of the main thread pool along with the location thread pools.  The key 
	 * name for the main thread pool is "Main Pool".  All other keys are location names.  The value of the map is an Integer 
	 * representing the size of the queue.
	 * 
	 * @return Map containing the location name (and "main pool") as the key and an Integer as the value representing the 
	 * queue size.
	 */
	@SuppressWarnings("rawtypes")
    public Map<String, Integer> getThreadPoolUsage() {
		Map<String, Integer> usageMap = new HashMap<String, Integer>();
		LocationService locationService = Context.getLocationService();
		usageMap.put(MAIN_POOL, poolQueue.size());
		Set<Entry<Integer, LinkedBlockingQueue>> entrySet = locationToQueueMap.entrySet();
		Iterator<Entry<Integer, LinkedBlockingQueue>> iter = entrySet.iterator();
		while (iter.hasNext()) {
			Entry<Integer, LinkedBlockingQueue> entry = iter.next();
			Integer locationId = entry.getKey();
			LinkedBlockingQueue locationQueue = entry.getValue();
			if (NO_LOCATION == locationId) {
				usageMap.put("No Location", locationQueue.size());
			} else {
				Location location = locationService.getLocation(locationId);
				if (location != null) {
					usageMap.put(location.getName(), locationQueue.size());
				}
			}
		}
		
		return usageMap;
	}
	
	private void shutdownManager() {
		shutdown = true;
		shutdownAndAwaitTermination(pool);
	}
	
	private void shutdownAndAwaitTermination(final ExecutorService pool) {
		// Disable new tasks from being submitted
		pool.shutdown(); 
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being canceled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					log.error("Pool did not terminate");
			}
		}
		catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
	
	@SuppressWarnings("rawtypes")
    private void loadQueue() {
		// Stop executing if the pool has been explicitly shutdown.
		while (!shutdown) {
			// Get all the queues from the map.
			Set<Entry<Integer, LinkedBlockingQueue>> entrySet = locationToQueueMap.entrySet();
			Iterator<Entry<Integer, LinkedBlockingQueue>> iter = entrySet.iterator();
			while (iter.hasNext()) {
				Entry<Integer, LinkedBlockingQueue> entry = iter.next();
				Integer locationId = entry.getKey();
				LinkedBlockingQueue locationQueue = entry.getValue();
				// Grab the first task off of the head of the queue.
				ChirdlRunnable runnable = (ChirdlRunnable)locationQueue.poll();
				if (runnable != null) {
					String name = runnable.getName();
					Integer priority = runnable.getPriority();
					try {
						// Add the runnable to the pool so it can eventually get executed.
						pool.execute(runnable);
						log.info("Added the following to the Thread Manager's execution queue - name: " + name + 
							" - priority: " + priority + " - clinic: " + locationId);
					} catch (RejectedExecutionException ree) {
						log.error("Thread Manager no longer accepting new threads.  This thread has been rejected - name: " + 
							name + " - priority: " + priority + " - clinic: " + locationId, ree);
					} catch (Exception e) {
						log.error("Error executing thread - name: " + name + " - priority: " + priority + " - clinic: " + 
							locationId, e);
					}
				}
			}
		}
		
		// Clear out the map for memory purposes.
		locationToQueueMap.clear();
	}
	
	private class ChirdlUtilThreadPoolExecutor extends ThreadPoolExecutor {

		public ChirdlUtilThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> workQueue) {
	        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }
		
		@Override
		protected void beforeExecute(Thread t, Runnable r) {
			if (r instanceof ChirdlRunnable) {
				ChirdlRunnable cr = (ChirdlRunnable)r;
				log.info("Thread Manager before execute - name: " + cr.getName() + 
					" - priority: " + cr.getPriority() + " - time: " + new Timestamp(new Date().getTime()) + 
					" - active threads: " + getActiveCount() + " - threads in pool: " + getPoolSize() + 
					" - tasks in queue: " + getQueue().size());
			}
			
			super.beforeExecute(t, r);
		}
		
		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			if (r instanceof ChirdlRunnable) {
				ChirdlRunnable cr = (ChirdlRunnable)r;
				log.info("Thread Manager after execute - name: " + cr.getName() + 
					" - priority: " + cr.getPriority() + " - time: " + new Timestamp(new Date().getTime()) + 
					" - active threads: " + getActiveCount() + " - threads in pool: " + getPoolSize() + 
					" - tasks in queue: " + getQueue().size());
			}
			
			super.afterExecute(r, t);
		}
		
	}
}
