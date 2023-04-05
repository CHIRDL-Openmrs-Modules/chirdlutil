package org.openmrs.module.chirdlutil.tools;

import java.io.File;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class used to delete files matching a specific extension that are also older 
 * than the specified date.  This is also recursive for all sub-directories.
 *
 * @author Steve McKee
 */
public class DeleteFiles {
    
    private static final Logger LOG = LoggerFactory.getLogger(DeleteFiles.class);
    
    /**
     * Deletes the files in the specified directory (and sub-directories) that match 
     * the provided extension and are older than the number of days provided.
     * 
     * @param directory The directory to check
     * @param extension The file extension to match
     * @param daysOld Files older than this will be deleted.
     */
    public void deleteFiles(File directory, String extension, Integer daysOld) {
        Calendar cal = Calendar.getInstance();
        // We need to convert to the negative inverse
        daysOld = daysOld - (daysOld * 2);
        cal.add(Calendar.DAY_OF_MONTH, daysOld);
        DeleteFileFilter filter = new DeleteFileFilter(cal, extension);
        delete(directory, extension, filter);
    }
    
    private void delete(File directory, String extension, DeleteFileFilter filter) {
        File[] files = directory.listFiles(filter);
        for (File file : files) {
            if (file.isDirectory()) {
                delete(file, extension, filter);
            } else {
                LOG.info("Deleting: {}", file.getAbsolutePath());
                if (!file.delete()) {
                    LOG.error("Could not delete file {}", file.getAbsolutePath());
                }
            }
        }
    }
    
    private void printUsage() {
        LOG.info("Arguments:");
        LOG.info("Arg 1: The directory to check");
        LOG.info("Arg 2: The files extension to check");
        LOG.info("Arg 3: The past n days worth of files to keep");
        LOG.info("");
        LOG.info("Usage:");
        LOG.info("DeleteFiles C:\\temp sql 5");
        LOG.info("This will delete all sql files in the C:\\temp directory older than 5 days");
    }
    
    /**
     * Auto generated method comment
     * 
     * @param args
     */
    public static void main(String[] args) {
        DeleteFiles delFiles = new DeleteFiles();
        if (args.length != 3) {
            delFiles.printUsage();
            System.exit(1);
        }
        
        String directoryStr = args[0];
        String extension = args[1];
        String daysOldStr = args[2];
        
        File directory = new File(directoryStr);
        if (!directory.exists()) {
            LOG.error("The directory {} does not exist ", directoryStr);
            LOG.error("");
            delFiles.printUsage();
            System.exit(1);
        } else if (!directory.isDirectory()) {
            LOG.error("The directory {} is not a directory", directoryStr );
            LOG.error("");
            delFiles.printUsage();
            System.exit(1);
        }
        
        Integer daysOld = null;
        try {
            daysOld = Integer.parseInt(daysOldStr);
        } catch (NumberFormatException e) {
            LOG.error("The number of days is not an Integer: {}", daysOldStr);
            LOG.error("");
            delFiles.printUsage();
            System.exit(1);
        }
        
        delFiles.deleteFiles(directory, extension, daysOld);
        
        System.exit(0);
    }
    
}
