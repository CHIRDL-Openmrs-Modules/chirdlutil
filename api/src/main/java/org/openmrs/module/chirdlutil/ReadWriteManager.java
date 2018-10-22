/**
 * 
 */
package org.openmrs.module.chirdlutil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author tmdugan
 * 
 */
public class ReadWriteManager
{
    private static final Log LOG = LogFactory.getLog(ReadWriteManager.class);

    private boolean writingInProgress = false;
    private int writersWaiting = 0;
    private int readers = 0;

    public void logReadWriteInfo(){
        LOG.info("Writing in progress: "+this.writingInProgress);
        LOG.info("# of writers waiting: "+this.writersWaiting);
        LOG.info("# of readers: "+this.readers);
    }
    
    /**
     * Read-Write locking code attributed to Nasir Khan
     * http://www.developer.com/java/article.php/951051
     */
    synchronized public void getReadLock()
    {
        // writing is more important so always wait on writer
        while (this.writingInProgress)
        {
            try
            {
                wait();
            } catch (InterruptedException ie)
            {
                LOG.error(ie);
                Thread.currentThread().interrupt();
            }
        }
        this.readers++;
    }

    synchronized public void releaseReadLock()
    {
        this.readers--;
        if ((this.readers == 0) & (this.writersWaiting > 0))
        {
            notifyAll();
        }
    }

    synchronized public void getWriteLock()
    {
        this.writersWaiting++;
        while ((this.readers > 0) | this.writingInProgress)
        {
            try
            {
                wait();
            } catch (InterruptedException ie)
            {
                LOG.error(ie);
                Thread.currentThread().interrupt();
            }
        }
        this.writersWaiting--;
        this.writingInProgress = true;
    }

    synchronized public void releaseWriteLock()
    {
        this.writingInProgress = false;
        notifyAll();
    }

}
