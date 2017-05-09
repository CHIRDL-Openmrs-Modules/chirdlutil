package org.openmrs.module.chirdlutil.threadmgmt;



public class TestThreadManager {
	
	/**
	 * Auto generated method comment
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ThreadManager manager = ThreadManager.getInstance();
		for (int i = 0; i < 1000; i++) {
			int priority = -1;
			int clinic = -1;
			if (i < 300) {
				priority = ChirdlRunnable.PRIORITY_THREE;
			} else if (i < 600) {
				priority = ChirdlRunnable.PRIORITY_TWO;
			} else {
				priority = ChirdlRunnable.PRIORITY_ONE;
			}
			
			if (i % 3 == 0) {
				clinic = 1;
			} else if (i % 3 == 1) {
				clinic = 2;
			} else {
				clinic = 3;
			}
			
			final int p = priority;
			final int c = clinic;
			manager.execute(new ChirdlRunnable() {
				
				/**
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					try {
						// Make the thread sleep for a bit to simulate processing time.
	                    Thread.sleep(100);
                    }
                    catch (InterruptedException e) {
	                    e.printStackTrace();
                    }
                    
					System.out.println("Thread: " + getName() + " Priority: " + getPriority() + 
						" just processed for clinic: " + c + ".");
				}
				
				/**
				 * @see ChirdlRunnable#getPriority()
				 */
				public int getPriority() {
					return p;
				}

				/**
				 * @see ChirdlRunnable#getName()
				 */
                public String getName() {
	                return "Test thread";
                }
			}, c);
		}
	}
	
}
