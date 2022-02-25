/**
 * 
 */
package org.openmrs.module.chirdlutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tmdugan
 * 
 */
public class ReadWriteManager
{
    private static final Logger LOG = LoggerFactory.getLogger(ReadWriteManager.class);

    private boolean writingInProgress = false;
    private int writersWaiting = 0;
    private int readers = 0;

    public void logReadWriteInfo(){
        LOG.info("Writing in progress: {}", this.writingInProgress);
        LOG.info("# of writers waiting: {}", this.writersWaiting);
        LOG.info("# of readers: {}", this.readers);
    }
    
    /**
     * Read-Write locking code attributed to Nasir Khan
     * http://www.developer.com/java/article.php/951051
     */
    public synchronized void getReadLock()
    {
        // writing is more important so always wait on writer
        while (this.writingInProgress)
        {
            try
            {
                wait();
            } catch (InterruptedException ie)
            {
                LOG.error("Error generated", ie);
                Thread.currentThread().interrupt();
            }
        }
        this.readers++;
    }

    public synchronized void releaseReadLock()
    {
        this.readers--;
        if ((this.readers == 0) & (this.writersWaiting > 0))
        {
            notifyAll();
        }
    }

    public synchronized void getWriteLock()
    {
        this.writersWaiting++;
        while ((this.readers > 0) | this.writingInProgress)
        {
            try
            {
                wait();
            } catch (InterruptedException ie)
            {
                LOG.error("Error generated", ie);
                Thread.currentThread().interrupt();
            }
        }
        this.writersWaiting--;
        this.writingInProgress = true;
    }

    public synchronized void releaseWriteLock()
    {
        this.writingInProgress = false;
        notifyAll();
    }

}
