package org.openmrs.module.chirdlutil.threadmgmt;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;

/**
 * Class to manage, order, and execute print job threads by printer.
 *
 * @author Steve McKee
 */
public class PrinterThreadManager {

    private static final String CHIRDLUTIL_PRINTER_THREAD_POOL_SIZE = "chirdlutil.printerThreadPoolSize";
    private static final String NO_PRINTER_NAME = "No Printer";
    
	private Map<String, ThreadPoolExecutor> printerNameToThreadExecutorMap = 
			new ConcurrentHashMap<String, ThreadPoolExecutor>(new HashMap<String, ThreadPoolExecutor>());
	private boolean shutdown = false;
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Private constructor
	 */
    private PrinterThreadManager() {
	}
	
	/**
	 * Class to hold the singleton instance of the PrinterThreadManager.
	 *
	 * @author Steve McKee
	 */
	private static class PrinterThreadManagerHolder { 
		public static final PrinterThreadManager INSTANCE = new PrinterThreadManager();
	}
 
	/**
	 * Returns the singleton instance of the PrinterThreadManager.
	 * 
	 * @return PrinterThreadManager
	 */
	public static PrinterThreadManager getInstance() {
		return PrinterThreadManagerHolder.INSTANCE;
	}
	
	/**
	 * Queues the provided Runnable by printer name and executes it when thread pool has an open thread.
	 * 
	 * @param runnable ChirdlPrinterRunnable object to execute.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void execute(ChirdlPrintJobRunnable runnable) {
		if (shutdown) {
			return;
		}
		
		String printerName = runnable.getPrinterName();
		if (printerName == null || printerName.trim().length() == 0) {
			printerName = NO_PRINTER_NAME;
		}
		
		// Find the printer name specific thread executor or create one if it can't be found.
		ThreadPoolExecutor printerJobExecutor = printerNameToThreadExecutorMap.get(printerName);
		if (printerJobExecutor == null) {
			synchronized(printerNameToThreadExecutorMap) {
				int poolSize = getThreadPoolSize();
				// Create the thread queue/pool
				BlockingQueue poolQueue = new PriorityBlockingQueue(poolSize);
				printerJobExecutor = new ChirdlUtilPrinterThreadPoolExecutor(poolSize, poolSize, 1L, TimeUnit.MILLISECONDS, poolQueue);
				printerJobExecutor.allowCoreThreadTimeOut(true);
				printerNameToThreadExecutorMap.put(printerName, printerJobExecutor);
			}
		}
		
		String printJobName = runnable.getPrintJobName();
		try {
			// Add the runnable to the pool so it can eventually get executed.
			printerJobExecutor.execute(runnable);
			log.info("Added the following to the Printer Thread Manager's execution queue - printer: " + printerName + 
				" - job name: " + printJobName);
		} catch (RejectedExecutionException ree) {
			log.error("Printer Thread Manager no longer accepting new threads.  This thread has been rejected - printer: " + 
				printerName + " - job name: " + printJobName, ree);
		} catch (Exception e) {
			log.error("Error executing Printer Thread Manager thread - printer: " + printerName + " - job name: " + printJobName, e);
		}
	}
	
	/**
	 * Shuts down the ThreadManager.  Only threads left in the pool queue will be executed, and 
	 * new threads will be rejected.  It is only advised to use this method on application shutdown.
	 */
	public void shutdown() {
		shutdownPools();
	}
	
	/**
	 * Returns a map containing the usage statistics of the printer thread pools.  The keys are the printer names, and 
	 * the value of the map is an Integer representing the size of the queue.
	 * 
	 * @return Map containing the printer name as the key and an Integer as the value representing the queue size.
	 */
    public Map<String, Integer> getThreadPoolUsage() {
		Map<String, Integer> usageMap = new HashMap<String, Integer>();
		Set<Entry<String, ThreadPoolExecutor>> entrySet = printerNameToThreadExecutorMap.entrySet();
		Iterator<Entry<String, ThreadPoolExecutor>> iter = entrySet.iterator();
		while (iter.hasNext()) {
			Entry<String, ThreadPoolExecutor> entry = iter.next();
			String printerName = entry.getKey();
			ThreadPoolExecutor executor = entry.getValue();
			if (NO_PRINTER_NAME == printerName) {
				usageMap.put(NO_PRINTER_NAME, executor.getQueue().size());
			} else {
				usageMap.put(printerName, executor.getQueue().size());
			}
		}
		
		return usageMap;
	}
	
	/**
	 * Shuts down all the printer thread pools that were created.
	 */
	private void shutdownPools() {
		shutdown = true;
		Collection<ThreadPoolExecutor> threadPools = printerNameToThreadExecutorMap.values();
		if (threadPools == null) {
			return;
		}
		
		for (ThreadPoolExecutor threadPool : threadPools) {
			shutdownAndAwaitTermination(threadPool);
		}
	}
	
	/**
	 * Shuts down a thread pool.
	 * 
	 * @param pool The thread pool to shut down.
	 */
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
	
	/**
	 * Returns the thread pool size as stated in the chirdlutil.printerThreadPoolSize global property.
	 * 
	 * @return The threadPoolSize as stated in the chirdlutil.printerThreadPoolSize global property.
	 */
	private int getThreadPoolSize() {
		AdministrationService adminService = Context.getAdministrationService();
		String poolSizeStr = adminService.getGlobalProperty(CHIRDLUTIL_PRINTER_THREAD_POOL_SIZE);
		if (poolSizeStr == null || poolSizeStr.trim().length() == 0) {
			log.error("Global property " + CHIRDLUTIL_PRINTER_THREAD_POOL_SIZE + " not defined.  A default pool size of 1 will be used.");
			return new Integer(1);
		}
		
		try {
			return Integer.parseInt(poolSizeStr);
		} catch (NumberFormatException e) {
			log.error("Global property " + CHIRDLUTIL_PRINTER_THREAD_POOL_SIZE + " is not a valid integer.  A default pool size of 1 will be used.");
			return new Integer(1);
		}
	}
	
	/**
	 * ThreadPoolExecutor that will execute the threads for a printer queue.
	 *
	 * @author Steve McKee
	 */
	private class ChirdlUtilPrinterThreadPoolExecutor extends ThreadPoolExecutor {

		/**
		 * Constructor method
		 * 
		 * @param corePoolSize The initial size of the thread pool.
		 * @param maximumPoolSize The maximum size of the thread pool.
		 * @param keepAliveTime How long to keep the active threads alive after they are finished.
		 * @param unit The unit of time for the keepAliveTime parameter.
		 * @param workQueue The queue that will hold awaiting jobs.
		 */
		public ChirdlUtilPrinterThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> workQueue) {
	        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }
		
		@Override
		protected void beforeExecute(Thread t, Runnable r) {
			if (r instanceof ChirdlPrintJobRunnable) {
				ChirdlPrintJobRunnable cpr = (ChirdlPrintJobRunnable)r;
				log.info("Printer Thread Manager before execute - printer: " + cpr.getPrinterName() + 
					" - job name: " + cpr.getPrintJobName() + " - time: " + new Timestamp(new Date().getTime()) + 
					" - active threads: " + getActiveCount() + " - threads in pool: " + getPoolSize() + 
					" - tasks in queue: " + getQueue().size());
			}
			
			super.beforeExecute(t, r);
		}
		
		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			if (r instanceof ChirdlPrintJobRunnable) {
				ChirdlPrintJobRunnable cpr = (ChirdlPrintJobRunnable)r;
				log.info("Printer Thread Manager after execute - printer: " + cpr.getPrinterName() + 
					" - job name: " + cpr.getPrintJobName() + " - time: " + new Timestamp(new Date().getTime()) + 
					" - active threads: " + getActiveCount() + " - threads in pool: " + getPoolSize() + 
					" - tasks in queue: " + getQueue().size());
			}
			
			super.afterExecute(r, t);
		}
	}
}
